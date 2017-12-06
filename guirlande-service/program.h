/*
 * program.h
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#ifndef PROGRAM_H_
#define PROGRAM_H_

#include <stddef.h>

struct program_part
{
	int states[16];
	struct program_part *next; /* reference cyclique */
};

typedef int program_part[16];

struct program
{
	struct program_part *first;
	struct program_part *current;
};

#endif /* PROGRAM_H_ */
