#include "../include/connectionHandler.h"
#include <stdlib.h>
#include <boost/thread.hpp>
#include <boost/chrono.hpp>
#include <boost/algorithm/string.hpp>
#include <fstream>

bool cont(false);
bool ext(true);

/**
 * decleration of the second thread that reads from client socket
 * @param ch
 */
void readFromCSocket(ConnectionHandler *ch ){

    //go in infinity loop until the user wont exit by using the signout command
    while(1) {
        int len;
        std::string answer;

        //in case the client or the server disconnect
        if (!ch->getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        //remove the /n from the massage
        len = answer.length();
        answer.resize(len - 1);

        std::cout << answer << std::endl;

        //check if the message is signout succseeded or not if yes exit from the loop else do nothing
        if (answer == "ACK signout succeeded") {
            ext = false;
            cont = true;
            break;
        } else if (answer == "ERROR signout failed") {
            cont = true;
        }
    }
}
int main (int argc, char *argv[]) {
      if (argc < 3) {
          std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
          return -1;
      }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    //make new connection handler and give reference to the second thread
    ConnectionHandler *connectionHandler = new ConnectionHandler(host, port);
    if (!(*connectionHandler).connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        delete connectionHandler;
        return 1;
    }

    //define second thread and bind him with the connaction handler
    boost::thread thread( boost::bind(&readFromCSocket , connectionHandler ));

    //infinity loop will exit only when we recive from the server signout succseeded message
    while (ext) {

        cont = false;

        //get the request from the user
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);

        if (!(*connectionHandler).sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        //if the user request for signout wait until the server response if succsess exit else continue
        if(boost::iequals(line , "signout")){
            while(!cont);
        }
    }
    delete connectionHandler;
    return 0;
}
