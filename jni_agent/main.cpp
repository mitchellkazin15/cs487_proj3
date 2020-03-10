#include "main.h"

/*This file is the main and only agent src file. It launches a thread to emit a beacon and execute commands from server*/

//Execute GetLocalOS from java rpc manager
void getLocalOS(GET_LOCAL_OS *ds){
    sprintf(ds->OS,"Linux");
    ds->valid = '1';
}

//Execute GetLocalTime from java rpc manager
void getLocalTime(GET_LOCAL_TIME *ds){
    time_t my_time = time(nullptr);
    ds->time = (int) my_time;
    ds->valid = '1';
}

//Continually sends beacons every time interval seconds
void sendBeacon(int cSock, beacon_t *buffer){
    cout << "Emitting Beacon..." << endl;
    while(true){
        size_t buffer_len = sizeof(beacon_t);

        int32_t sent_bytes = send(cSock, buffer, buffer_len, 0);
        if (sent_bytes < 0)
        {
            fprintf(stderr,"Failed to send Beacon to %d \n", buffer->destIP);
        }
        sleep(buffer->timeInterval);
    }
}

// BeaconSender thread starts here and establishes UDP socket connection
void * socketConnect(void *beacon){
    beacon_t *buffer = (beacon_t *)beacon;
    int cSock;
    if ((cSock = socket(AF_INET, SOCK_DGRAM, 0)) < 0){
        perror("socket");
        printf("Failed to create socket\n");
        abort ();
    }
    struct sockaddr_in sin;
    memset (&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = buffer->destIP;
    sin.sin_port = htons(UDP_PORT);

    if (connect(cSock, (struct sockaddr *) &sin, sizeof(sin)) < 0){
        fprintf(stderr, "Cannot connect to server\n");
        abort();
    }
    sendBeacon(cSock, buffer);
}

//Command Agent thread starts here and establishes tcp connection
//receive and execute RPC calls from java manager
void * cmdAgent(void *beacon){
    beacon_t *beac = (beacon_t *) beacon;

    int sock;
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket");
        printf("Failed to create socket\n");
        abort();
    }
    struct sockaddr_in s;
    memset(&s, 0, sizeof(s));
    s.sin_family = AF_INET;
    s.sin_addr.s_addr = beac->destIP;
    s.sin_port = htons(beac->CmdPort);


    if (connect(sock, (struct sockaddr *) &s, sizeof(s)) < 0) {
        fprintf(stderr, "Cannot connect to server\n");
        abort();
    }

    cout << "Establishing connection on port " << beac->CmdPort << endl;
    while(true) {
        cout << "Waiting for command..." << endl;
        vector<char> buf(5000); // you are using C++ not C
        int bytes = recv(sock, buf.data(), buf.size(), 0);
        string *cmd = new string(buf.data());
        cout << "cmd: " << *cmd << endl;


        char packet[1024];
        if(cmd->substr(0,12).compare("GetLocalTime") == 0){
            GET_LOCAL_TIME *ds = (GET_LOCAL_TIME *)malloc(sizeof(GET_LOCAL_TIME));
            getLocalTime(ds);
            sprintf(packet, "%d %c\n",ds->time,ds->valid);
        }
        if(cmd->substr(0,10).compare("GetLocalOS") == 0){
            GET_LOCAL_OS *ds = (GET_LOCAL_OS *)malloc(sizeof(GET_LOCAL_OS));
            getLocalOS(ds);
            sprintf(packet, "%s %c\n",ds->OS,ds->valid);
        }
        cout << "Sending response..." << endl;
        write (sock, packet, strlen(packet));
        cout << "Packet sent" << endl << endl;
    }
}

//Execution starts here. reads IP's from command line, builds a beacon, and starts the threads
int main(int argc, char* argv[]){

    string *destIP = new string("127.0.0.1");
    string *srcIP = new string("127.0.0.1");

    if(argc == 3){
        destIP = new string(argv[1]);
        srcIP = new string(argv[2]);
    }
    else if(argc == 2){
        destIP = new string(argv[1]);
    }

    srand(time(0));
    beacon_t *beacon = (beacon_t*)malloc(sizeof(beacon_t));
    beacon->ID = (u_int32_t)rand();
    beacon->StartUpTime = (u_int32_t)time(nullptr);
    beacon->timeInterval = 3;
    beacon->destIP = inet_addr(destIP->c_str());
    beacon->srcIP = inet_addr(srcIP->c_str());
    beacon->CmdPort = UDP_PORT + (rand() % 100);
    pthread_t beaconSender = *(new pthread_t);
    pthread_t cmdReciever = *(new pthread_t);
    pthread_create(&beaconSender, nullptr, socketConnect, (void *)beacon);
    sleep(2);
    pthread_create(&cmdReciever, nullptr, cmdAgent, (void *)beacon);
    pthread_join(cmdReciever, nullptr);
    pthread_join(beaconSender, nullptr);
    return 0;
}
#pragma clang diagnostic pop