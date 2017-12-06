#include <pigpio.h>

#include "gpio.h"
#include "errhandler.h"

static int gpio_out[] = {
	14, 15, 18, 23, 24, 25, 8, 7,
	3, 4, 17, 27, 22, 10, 9, 11
};

static int gpio_in = 2;

void gpio_init() {
	gpioCfgInterfaces(PI_DEFAULT_IF_FLAGS | PI_DISABLE_FIFO_IF | PI_DISABLE_SOCK_IF);
	if(gpioInitialise() < 0) {
		err_die("Can't initialise pigpio library");
	}

	for(size_t id=0; id<16; ++id) {
		gpioSetMode(gpio_out[id], PI_OUTPUT);
		gpioSetPWMrange(gpio_out[id], 100);
	}
	gpioSetMode(gpio_in, PI_INPUT);
}

void gpio_terminate() {
	for(size_t id=0; id<16; ++id) {
		gpioSetMode(gpio_out[id], PI_INPUT);
	}
	gpioSetMode(gpio_in, PI_INPUT);

	gpioTerminate();
}

void gpio_set(int id, int value) {
	gpioPWM(gpio_out[id], value);
}

int gpio_get() {
	return gpioRead(gpio_in);
}

