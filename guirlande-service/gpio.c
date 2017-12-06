/*
 * gpio.c
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/types.h>
#include <unistd.h>

#define PATH_MAX 1024 // #include <linux/limits.h>

#include "gpio.h"
#include "errhandler.h"

#define GPIO_BASE "/sys/class/gpio/"
#define PWM_BASE "/sys/class/soft_pwm/"
#define EXPORT "export"
#define UNEXPORT "unexport"
#define GPIO_PREFIX "gpio"
#define PWM_PREFIX "pwm"

struct gpio
{
	int gpioid;
	int value;
	int fd;
};

struct gpio gpio_out[16];
struct gpio gpio_in;

static void gpio_close(struct gpio *gpio);
static void gpio_open(struct gpio *gpio, int isout);
static void gpio_export_unexport(int pwm, int gpioid, int isexport);

void gpio_init()
{
	gpio_out[0].gpioid = 14;
	gpio_out[1].gpioid = 15;
	gpio_out[2].gpioid = 18;
	gpio_out[3].gpioid = 23;
	gpio_out[4].gpioid = 24;
	gpio_out[5].gpioid = 25;
	gpio_out[6].gpioid = 8;
	gpio_out[7].gpioid = 7;

	gpio_in.gpioid = 2;

	gpio_out[8].gpioid = 3;
	gpio_out[9].gpioid = 4;
	gpio_out[10].gpioid = 17;
	gpio_out[11].gpioid = 27;
	gpio_out[12].gpioid = 22;
	gpio_out[13].gpioid = 10;
	gpio_out[14].gpioid = 9;
	gpio_out[15].gpioid = 11;

	for(size_t i=0; i<sizeof(gpio_out)/sizeof(gpio_out[0]); i++)
	{
		gpio_out[i].fd = -1;
		gpio_out[i].value = -1;
		gpio_set(i, 0);
	}

	gpio_in.value = GPIO_MIN;
	gpio_open(&gpio_in, 0);
}

void gpio_terminate()
{
	gpio_close(&gpio_in);

	for(size_t i=0; i<sizeof(gpio_out)/sizeof(gpio_out[0]); i++)
	{
		gpio_set(i, 0);
		gpio_close(gpio_out+i);
	}
}

void gpio_set(int id, int value)
{
	struct gpio *gpio = gpio_out + id;
	if(gpio->value == value)
		return;
	int oldpwm = gpio->value != GPIO_MIN && gpio->value != GPIO_MAX;
	int newpwm = value != GPIO_MIN && value != GPIO_MAX;

	if(oldpwm != newpwm)
		gpio_close(gpio);
	gpio->value = value;
	if(oldpwm != newpwm)
		gpio_open(gpio, 1);

	if(newpwm)
	{
		char sval[15];
		sprintf(sval, "%i", gpio->value * 100);
		lseek(gpio->fd, 0, SEEK_SET);
		write(gpio->fd, sval, strlen(sval));
		// lseek(gpio->fd, 0, SEEK_SET);
	}
	else
	{
		lseek(gpio->fd, 0, SEEK_SET);
		write(gpio->fd, gpio->value == GPIO_MAX ? "1" : "0", 1);
	}

	gpio->value = value;
}

int gpio_get()
{
	char c;
	lseek(gpio_in.fd, 0, SEEK_SET);
	read(gpio_in.fd, &c, 1);
	return c == '1';
}

void gpio_close(struct gpio *gpio)
{
	int pwm = gpio->value != GPIO_MIN && gpio->value != GPIO_MAX;
	if(gpio->fd != -1)
	{
		close(gpio->fd);
		gpio_export_unexport(pwm, gpio->gpioid, 0);
	}
}

void gpio_open(struct gpio *gpio, int isout)
{
	int pwm = gpio->value != GPIO_MIN && gpio->value != GPIO_MAX;
	int gpioid = gpio->gpioid;
	gpio_export_unexport(pwm, gpioid, 1);

	if(pwm)
	{
		const char *period = "10000";
		char filename[PATH_MAX];
		sprintf(filename, "%s%s%i/period", PWM_BASE, PWM_PREFIX, gpioid);
		int fp = open(filename, O_WRONLY);
		if(fp == -1)
			err_die("error opening file : %s", filename);

		write(fp, period, strlen(period));
		close(fp);

		sprintf(filename, "%s%s%i/pulse", PWM_BASE, PWM_PREFIX, gpioid);
		gpio->fd = open(filename, O_WRONLY);
		if(gpio->fd == -1)
			err_die("error opening file : %s", filename);
	}
	else
	{
		const char *direction = isout ? "out" : "in";
		char filename[PATH_MAX];
		sprintf(filename, "%s%s%i/direction", GPIO_BASE, GPIO_PREFIX, gpioid);
		int fp = open(filename, O_WRONLY);
		if(fp == -1)
			err_die("error opening file : %s", filename);

		write(fp, direction, strlen(direction));
		close(fp);

		sprintf(filename, "%s%s%i/value", GPIO_BASE, GPIO_PREFIX, gpioid);
		gpio->fd = open(filename, isout ? O_WRONLY : O_RDONLY);
		if(gpio->fd == -1)
			err_die("error opening file : %s", filename);
	}
}

void gpio_export_unexport(int pwm, int gpioid, int isexport)
{
	char filename[PATH_MAX];
	char content[10];

	sprintf(filename, "%s%s", pwm ? PWM_BASE : GPIO_BASE, isexport ? EXPORT : UNEXPORT);
	sprintf(content, "%i", gpioid);

	int fp = open(filename, O_WRONLY);
	if(fp == -1)
		err_die("error opening file : %s", filename);

	write(fp, content, strlen(content));
	close(fp);
}
