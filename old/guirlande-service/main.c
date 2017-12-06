/*
 * main.c
 *
 *  Created on: 5 juin 2013
 *      Author: pumbawoman
 */

// pour daemon()
#define _BSD_SOURCE
#define _XOPEN_SOURCE
#include <unistd.h>
 #include <sys/wait.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <signal.h>
#include <sys/types.h>

#include "gpio.h"
#include "net.h"
#include "executor.h"
#include "errhandler.h"
#include "config.h"

static void startup(int argc, char **argv);
static void init();
static void terminate(int signum);
static pid_t read_pid();
static int get_status(); // -1 = erreur, 1 = running, 0 = stopped

int main(int argc, char **argv)
{
	startup(argc, argv);

	init();
	signal(SIGTERM, terminate);
	if(debug)
		signal(SIGINT, terminate);

	// boucle principale
	for(;;)
	{
		net_loop(10000); // 10ms
		executor_loop();
	}

	return EXIT_SUCCESS;
}

void startup(int argc, char **argv)
{
	int help = 0;
	int unknown = 0;
	int status = 0;
	int start = 0;
	int stop = 0;

	if(argc < 2)
		help = 1;
	else if(!strcmp(argv[1], "help"))
		help = 1;
	else if(!strcmp(argv[1], "debug"))
		debug = 1;
	else if(!strcmp(argv[1], "status"))
		status = 1;
	else if(!strcmp(argv[1], "start"))
		start = 1;
	else if(!strcmp(argv[1], "stop"))
		stop = 1;
	else
	{
		help = 1;
		unknown = 1;
		fprintf(stderr, "unknown option : %s\n", argv[1]);
	}

	if(help)
	{
		puts("options :");
		puts(" debug : interactive, ctrl+c to exit");
		puts(" help : print this help");
		puts(" start : start service");
		puts(" stop : stop service");
		puts(" status : get service status");
		exit(unknown ? EXIT_FAILURE : EXIT_SUCCESS);
	}

	if(debug)
		return;

	if(start)
	{
		if(get_status() == 1)
		{
			puts("already running");
			exit(EXIT_SUCCESS);
		}

		puts("starting");
		puts("started");

		if(daemon(0, 0) == -1)
			err_die("daemon error");

		FILE *fd = fopen(CONFIG_PID_FILE, "w");
		fprintf(fd, "%i", getpid());
		fclose(fd);
		return;
	}

	if(stop)
	{
		pid_t pid = read_pid();
		if(pid == -1)
		{
			puts("not running");
			exit(EXIT_SUCCESS);
		}

		if(!kill(pid, SIGTERM))
		{
			puts("stopping");
		}
		else
		{
			fprintf(stderr, "error : %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		int status;
		waitpid(pid, &status, 0);
		puts("stopped");

		exit(EXIT_SUCCESS);
	}

	if(status)
	{
		switch(get_status())
		{
			case 1:
				puts("running");
				exit(EXIT_SUCCESS);
				break;

			case 0:
				puts("not running");
				exit(EXIT_SUCCESS);
				break;
		}
	}
}

pid_t read_pid()
{
	FILE *fd = fopen(CONFIG_PID_FILE, "r");
	if(!fd)
		return (pid_t)-1;

	pid_t pid;
	fscanf(fd, "%i", &pid);

	fclose(fd);

	return pid;
}

int get_status() // 1 = running, 0 = stopped
{
	int pid = read_pid();
	if(pid == (pid_t)-1)
		return 0;

	if(!kill(pid, 0))
		return 1;

	if(errno == ESRCH)
		return 0;

	fprintf(stderr, "unknown : %s\n", strerror(errno));
	exit(EXIT_FAILURE);
}

void init()
{
	out_log("initializing");
	gpio_init();
	net_init();
	executor_init();
	out_log("initialized");
}

void terminate(int signum)
{
	// eviter le warning
	signum = 0;
	if(signum != 0)
		return;

	out_log("terminating");
	executor_terminate();
	net_terminate();
	gpio_terminate();
	out_log("terminated");

	exit(EXIT_SUCCESS);
}
