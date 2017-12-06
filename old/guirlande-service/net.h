/*
 * net.h
 *
 *  Created on: 6 juin 2013
 *      Author: pumbawoman
 */

#ifndef NET_H_
#define NET_H_

extern void net_init();
extern void net_terminate();

extern void net_loop(int timeout_usec);
extern void net_send_next();

#endif /* NET_H_ */
