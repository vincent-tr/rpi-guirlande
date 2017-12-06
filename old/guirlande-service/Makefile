SHELL   = /bin/sh
CC      = gcc
FLAGS   = -std=c99
CFLAGS  = -pedantic -Wall -Wextra
TARGET  = guirlande-service
SOURCES = $(shell echo *.c)
HEADERS = $(shell echo *.h)
OBJECTS = $(SOURCES:.c=.o)

all: $(TARGET)
 
$(TARGET): $(OBJECTS)
	$(CC) $(FLAGS) $(CFLAGS) -o $(TARGET) $(OBJECTS)

%.o: %.c $(HEADERS) $(COMMON)
	$(CC) $(FLAGS) $(CFLAGS) -c -o $@ $<
  
clean:
	-rm -f $(OBJECTS)
	-rm -f $(TARGET)
  