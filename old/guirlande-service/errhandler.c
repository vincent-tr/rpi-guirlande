/*
 * errhandler.c
 *
 *  Created on: 5 juin 2013
 *      Author: pumbawoman
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <errno.h>

#include "errhandler.h"
#include "config.h"

int debug = 0;

#define LEVEL_OUT 0
#define LEVEL_ERROR 1

static FILE *log_open(int level);
static void log_close(FILE *fp);

void out_log(const char *format, ...)
{
	va_list args;
	FILE *fp = log_open(LEVEL_OUT);

	va_start(args, format);
	vfprintf(fp, format, args);
	va_end(args);
	fprintf(fp, "\n");

	log_close(fp);
}

void err_log(const char *format, ...)
{
	va_list args;
	FILE *fp = log_open(LEVEL_ERROR);

	va_start(args, format);
	vfprintf(fp, format, args);
	va_end(args);
	fprintf(fp, "\n%s\n", strerror(errno));

	log_close(fp);
}

void err_die(const char *format, ...)
{
	va_list args;
	FILE *fp = log_open(LEVEL_ERROR);

	va_start(args, format);
	vfprintf(fp, format, args);
	va_end(args);
	fprintf(fp, "\n%s\n", strerror(errno));

	log_close(fp);

	exit(EXIT_FAILURE);
}

void err_check_die_internal(const void *value, const char *error)
{
	if(value)
		return;

	FILE *fp = log_open(LEVEL_ERROR);
	fprintf(fp, "err_check failed : %s\n", error);
	log_close(fp);

	exit(EXIT_FAILURE);
}

FILE *log_open(int level)
{
	if(debug)
		return level == LEVEL_OUT ? stdout : stderr;

	return fopen(CONFIG_LOG_FILE, "a");
}

void log_close(FILE *fp)
{
	if(debug)
		return;

	fclose(fp);
}
