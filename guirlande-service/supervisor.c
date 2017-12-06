#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>

#include "supervisor.h"
#include "gpio.h"
#include "executor.h"
#include "config.h"
#include "errhandler.h"

struct state_def {
  int state[16];
};

struct program_def {
  const char *name;
  const char *description;
  size_t states_count;
  struct state_def *states;

  struct program_def *next; // cyclic
};

static int prev_input = 0;
static struct program_def *current_program;
static struct program_def *last_program; // while init

static void init_program_list();
static void register_program(const char *name, const char *description, size_t count, const struct state_def states[]);
static void next_program();
static void setup_current_program();

#define REGISTER_PROGRAM(name, description, states) {                                   \
  struct state_def _states[] = states;                                                  \
  register_program(name, description, sizeof(_states) / sizeof((_states)[0]), _states); \
}

#include "supervisor-programs.h"

void supervisor_init() {
  init_program_list();
  last_program->next = current_program;
  last_program = NULL;

  setup_current_program();
}

void supervisor_terminate() {
  struct program_def *current = current_program;
  while(current)
  {
    struct program_def *next = current->next;
    free(current->states);
    free(current);
    current = next;

    if(current == current_program) {
      current = NULL; // on est revenu au debut
    }
  }
  current_program = NULL;
}

void supervisor_loop() {
  int current_input = gpio_get();
  if(!prev_input && current_input) {
    next_program();
  }

  prev_input = current_input;
}

void register_program(const char *name, const char *description, size_t count, struct state_def states[]) {
  size_t size = sizeof(struct state_def) * count;
  struct program_def *prog = malloc(sizeof(*prog));
  prog->next = NULL;
  prog->name = name;
  prog->description = description;
  prog->states_count = count;
  prog->states = malloc(size);
  memcpy(prog->states, states, size);

  if(!current_program) {
    current_program = last_program = prog;
    return;
  }

  last_program->next = prog;
  last_program = prog;
}

void next_program() {
  current_program = current_program->next;
  setup_current_program();
}

void setup_current_program() {
  out_log("switching to program %s (%s)", current_program->name, current_program->description);

  struct program *pgm = malloc(sizeof(*pgm));
  struct program_part *part, *prev;

  pgm->current = NULL;
  pgm->first = NULL;

  for(size_t states_index = 0; states_index < current_program->states_count; ++states_index) {

    part = malloc(sizeof(*part));
    if(pgm->first)
      prev->next = part;
    else
      pgm->first = part;

    part->next = pgm->first;
    prev = part;

    memcpy(part->states, current_program->states[states_index].state, sizeof(part->states));

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

  out_log("end of program : %i states", current_program->states_count);
}

