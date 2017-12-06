PIGPIO_DIR = ../../pigpio
SHELL      = /bin/sh
CC         = gcc
FLAGS      = -std=c99
CFLAGS     = -O3 -pedantic -Wall -Wextra
LDFLAGS    =
TARGET     = guirlande-service
SOURCES    = $(shell echo *.c)
HEADERS    = $(shell echo *.h)
OBJECTS    = $(SOURCES:.c=.o)

all: $(TARGET)

$(TARGET): $(OBJECTS)
	$(CC) $(FLAGS) -o $(TARGET) $(OBJECTS) $(LDFLAGS)

%.o: %.c $(HEADERS) $(COMMON)
	$(CC) $(FLAGS) $(CFLAGS) -c -o $@ $<

clean:
	-rm -f $(OBJECTS)
	-rm -f $(TARGET)
