/*
 * net.c
 *
 *  Created on: 6 juin 2013
 *      Author: pumbawoman
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>

#include "net.h"
#include "executor.h"
#include "config.h"
#include "errhandler.h"

static int server;
#define BUFFER_SIZE 65536 // taille max d'un message
static char buffer[BUFFER_SIZE];

static struct program *read_program(const char *buffer, size_t len);

void net_init()
{
    struct sockaddr_in saddr;

    server = socket(AF_INET, SOCK_DGRAM, 0);

    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    saddr.sin_port = htons(CONFIG_PROGRAM_PORT);

    bind(server, (struct sockaddr*)&saddr, sizeof(saddr));
}

void net_terminate()
{
	close(server);
}

void net_send_next()
{
	struct sockaddr_in saddr;
	const char *msg = "msg";

	int client = socket(AF_INET, SOCK_DGRAM, 0);

	memset(&saddr, 0, sizeof(saddr));
	saddr.sin_family = AF_INET;
	saddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
	saddr.sin_port = htons(CONFIG_NEXT_PORT);

	sendto(client, msg, strlen(msg), 0, (struct sockaddr *)&saddr, sizeof(saddr));
	out_log("sending next");

	close(client);
}

void net_loop(int timeout_usec)
{
    fd_set fds;
    struct timeval tv;
	struct sockaddr_in saddr;
    socklen_t len = sizeof(saddr);

    FD_ZERO(&fds);
    FD_SET(server, &fds);
    tv.tv_sec = 0;
    tv.tv_usec = timeout_usec;

    switch(select(server + 1, &fds, NULL, NULL, &tv))
    {
    case -1:
    	err_die("error selecting socket");
    	break;
    case 0: // rien à lire
    	return;

    default: // à lire, on passe à la suite
    	break;
    }

    int datalen = recvfrom(server, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&saddr, &len);
    if(datalen == -1)
    	err_die("error reading socket");

    struct program *pgm = read_program(buffer, datalen);
    if(!pgm)
    	return;

    executor_set(pgm);
}

struct program *read_program(const char *buffer, size_t len)
{
	out_log("receiving program");

	if(len % 16 != 0)
	{
		err_log("bad program format : len % 16 != 0");
		return NULL;
	}

	struct program *pgm = malloc(sizeof(*pgm));
	struct program_part *part, *prev;

	pgm->current = NULL;
	pgm->first = NULL;

	for(size_t stateIndex = 0; stateIndex < len / 16; stateIndex++)
	{
		part = malloc(sizeof(*part));
		if(pgm->first)
			prev->next = part;
		else
			pgm->first = part;

		part->next = pgm->first;
		prev = part;

		for(size_t i=0; i<sizeof(part->states)/sizeof(part->states[0]); i++)
			part->states[i] = buffer[stateIndex * 16 + i];

		out_log("%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i",
			part->states[0],
			part->states[1],
			part->states[2],
			part->states[3],
			part->states[4],
			part->states[5],
			part->states[6],
			part->states[7],
			part->states[8],
			part->states[9],
			part->states[10],
			part->states[11],
			part->states[12],
			part->states[13],
			part->states[14],
			part->states[15]);
	}

	out_log("program received : %i states", len / 16);

	return pgm;
}
