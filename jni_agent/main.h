//
// Created by mjkazin on 2/17/20.
//

#ifndef RPC_CPP_MAIN_H
#define RPC_CPP_MAIN_H

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <pthread.h>
#include <stdint.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/utsname.h>

#include <cstdlib>
#include <iostream>
#include <vector>
#include <string>
#include <ctime>

using namespace std;

//This file is the header for main.cpp and holds all struct definition and include statements

//these are constant that the agent will use until termination
#define UDP_PORT 42636

//Structure that encodes information to be sent in a UDP beacon
typedef struct BEACON
{
    u_int32_t ID;               // randomly generated during startup
    u_int32_t StartUpTime;      // the time when the client starts
    u_int32_t srcIP;            // the IP address of this client
    u_int32_t destIP;	        // IP address of the manager
    u_int32_t timeInterval;     // beacon repeat interval
    u_int32_t CmdPort; 	        // the client listens to this port for cmd
} beacon_t;

//struct return for getLocalTime() call
typedef struct
{
    int    time;
    char   valid;
} GET_LOCAL_TIME;

//struct return for getLocalOS() call
typedef struct
{
    char  OS[16];
    char  valid;
} GET_LOCAL_OS;

#endif //RPC_CPP_MAIN_H
