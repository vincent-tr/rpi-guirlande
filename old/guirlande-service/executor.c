/*
 * executor.c
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#include <stdio.h>
#include <stdlib.h>

#include "config.h"
#include "errhandler.h"
#include "program.h"
#include "executor.h"
#include "gpio.h"

static struct program *current;

static void free_current();
static struct program *create_default();

void executor_init()
{
	// cr�ation d'un programme par d�faut vide
	struct program *pgm = create_default();
	executor_set(pgm);
}

void executor_terminate()
{
	free_current();
}

struct program *create_default()
{
	struct program *pgm = malloc(sizeof(*pgm));
	pgm->current = NULL;

	struct program_part *part = malloc(sizeof(*part));
	part->next = part;
	for(size_t i=0; i<sizeof(part->states)/sizeof(part->states[0]); i++)
		part->states[i] = 0;

	pgm->first = part;
	return pgm;
}

void free_current()
{
	if(!current)
		return;

	current->current = current->first;
	while(current->current)
	{
		struct program_part *delpart = current->current;
		if(delpart->next == current->first)
			current->current = NULL; // on est revenu au d�but
		else
			current->current = delpart->next;
		free(delpart);
	}

	free(current);
	current = NULL;
}

void executor_set(struct program *program)
{
	free_current();

	current = program;
	current->current = current->first;
}

void executor_loop()
{
	// d�finition du gpio
	struct program_part *part = current->current;

	for(size_t i=0; i<sizeof(part->states)/sizeof(part->states[0]); i++)
	{
		gpio_set(i, part->states[i]);
	}

	// passage  l'�tape suivante
	current->current = part->next;
}

