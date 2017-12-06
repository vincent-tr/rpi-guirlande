/*
 * gpio.h
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#ifndef GPIO_H_
#define GPIO_H_

extern void gpio_init();
extern void gpio_terminate();

extern void gpio_set(int id, int value);
extern int gpio_get();

#define GPIO_MIN 0
#define GPIO_MAX 100

#endif /* GPIO_H_ */
