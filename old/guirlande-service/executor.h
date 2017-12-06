/*
 * executor.h
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#ifndef EXECUTOR_H_
#define EXECUTOR_H_

#include "program.h"

extern void executor_init();
extern void executor_terminate();

extern void executor_set(struct program *program);
extern void executor_loop();

#endif /* EXECUTOR_H_ */
