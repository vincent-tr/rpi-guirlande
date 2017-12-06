/*
 * errhandler.h
 *
 *  Created on: 4 juin 2013
 *      Author: pumbawoman
 */

#ifndef ERRHANDLER_H_
#define ERRHANDLER_H_

extern void out_log(const char *format, ...);
extern void err_log(const char *format, ...);
extern void err_die(const char *format, ...);
#define err_check_die(expr) err_check_die_internal(expr, ##expr)
extern void err_check_die_internal(const void *value, const char *error);

extern int debug;

#endif /* ERRHANDLER_H_ */
