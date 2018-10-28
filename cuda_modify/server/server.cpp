/**
 * Webservice for Stereolab. The service will listen on
 * http://HOSTNAME:PORT/Stereolab
 */
 
#include "kernel.h" 

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>

#define STD_PORT "5000"  // the port users will be connecting to

struct img_header {
	int zeilen;
	int spalten;
	int tau;
	int fenster_breite;
	int fenster_hoehe;
};

void sigchld_handler(int s)
{
	(void)s; // quiet unused variable warning

	// waitpid() might overwrite errno, so we save and restore it:
	int saved_errno = errno;

	while(waitpid(-1, NULL, WNOHANG) > 0);

	errno = saved_errno;
}


// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}

	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

int main(int argc, char *argv[])
{
	int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr; // connector's address information
	socklen_t sin_size;
	struct sigaction sa;
	int yes=1;
	char s[INET6_ADDRSTRLEN];
	int rv;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_INET6;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP

    char* port = STD_PORT;
	if (argc == 2) {
        port = argv[1];
    }
        
	if ((rv = getaddrinfo(NULL, port, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}

		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
				sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}

		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("server: bind");
			continue;
		}

		break;
	}

	freeaddrinfo(servinfo); // all done with this structure

	if (p == NULL)  {
		fprintf(stderr, "server: failed to bind\n");
		exit(1);
	}

	if (listen(sockfd, 10) == -1) {
		perror("listen");
		exit(1);
	}

	sa.sa_handler = sigchld_handler; // reap all dead processes
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa, NULL) == -1) {
		perror("sigaction");
		exit(1);
	}

	printf("server: waiting for connections...\n");

	while(1) {  // main accept() loop
		sin_size = sizeof their_addr;
		new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
		if (new_fd == -1) {
			perror("accept");
			continue;
		}

		inet_ntop(their_addr.ss_family,
			get_in_addr((struct sockaddr *)&their_addr),
			s, sizeof s);
		printf("server: got connection from %s\n", s);

		if (!fork()) { // this is the child process
			close(sockfd); // child doesn't need the listener
			
            //~ while(1) {
                //~ /*char in_buf[MSG_BUF_SIZE];
                //~ char out_buf[MSG_BUF_SIZE];
                //~ memset(&in_buf,0,MSG_BUF_SIZE);
                //~ memset(&out_buf,0,MSG_BUF_SIZE);*/#
                //~ img_header in_header;
                
                //~ if (recv(new_fd, &in_header, sizeof(in_header), 0) == -1) {
                    //~ perror("recv");
                    //~ exit(1);
                //~ }
                //~ printf("%d\n", in_header.zeilen);
                //~ printf("%d\n", in_header.spalten);
                //~ printf("%d\n", in_header.tau);
                //~ printf("%d\n", in_header.fenster_breite);
                //~ printf("%d\n", in_header.fenster_hoehe);
                //~ //parse(in_buf, out_buf);
				//~ //doCUDACalc((signed char*)p->leftImage, (signed char*)p->rightImage, p->rows, p->columns,p->tauMax, r->profile, (unsigned char*)r->valid, p->b, p->h, p->useS, p->useF, p->s);
                //~ /*if (send(new_fd, out_buf, strlen(out_buf), 0) == -1) {
                    //~ perror("send");
                    //~ exit(1);
                //~ }*/
            //~ }
			img_header header;
			
			if (recv(new_fd, &header, sizeof(header), 0) == -1) {
				perror("recv");
				exit(1);
			}
			printf("%d\n", header.zeilen);
			printf("%d\n", header.spalten);
			printf("%d\n", header.tau);
			printf("%d\n", header.fenster_breite);
			printf("%d\n", header.fenster_hoehe);
			
			char* leftImage = (char*)malloc(header.zeilen * header.spalten * sizeof(char));
			char* rightImage = (char*)malloc(header.zeilen * header.spalten * sizeof(char));
			
			int* profile = (int*)malloc(header.zeilen * header.spalten * sizeof(int));
			char* valid = (char*)malloc(header.zeilen * header.spalten * sizeof(char));
			int rec_byte_number = 0;
			int rec_overall = 0;
			char* curImagePointer = leftImage;
			
			//printf("%d\n", sizeof(char));
			
			printf("bytes to receive %d\n", header.zeilen * header.spalten * sizeof(char));
			while((rec_byte_number = recv(new_fd, curImagePointer, (header.zeilen * header.spalten * sizeof(char)) - rec_overall, 0)) > 0) {
				curImagePointer=curImagePointer + rec_byte_number;
				rec_overall = rec_overall + rec_byte_number;
				printf("Got %d\n", rec_byte_number);
			}
			printf("Got left image %d\n", rec_overall);
			
			
			rec_byte_number = 0;
			rec_overall = 0;
			curImagePointer = rightImage;
			printf("bytes to receive %d\n", header.zeilen * header.spalten * sizeof(char));
			/*while((rec_byte_number = recv(new_fd, curImagePointer, (header.zeilen * header.spalten * sizeof(char)) - rec_overall, 0)) > 0 && ((header.zeilen * header.spalten * sizeof(char)) - rec_overall) > 0) {
				curImagePointer=curImagePointer + rec_byte_number;
				rec_overall = rec_overall + rec_byte_number;
				printf("Got %d\n", rec_byte_number);
			}*/			
			//while((((header.zeilen * header.spalten * sizeof(char)) - rec_overall) > 0) && (rec_byte_number = recv(new_fd, curImagePointer, (header.zeilen * header.spalten * sizeof(char)) - rec_overall, 0)) > 0 ) {
			while((rec_byte_number = recv(new_fd, curImagePointer, (header.zeilen * header.spalten * sizeof(char)) - rec_overall, 0)) > 0 ) {
				curImagePointer=curImagePointer + rec_byte_number;
				rec_overall = rec_overall + rec_byte_number;
				printf("Got %d\n", rec_byte_number);
			}
			printf("Got right image %d\n", rec_overall);
				
			printf("GOT EVERYTHING YAY \n");
			
			doCUDACalc((signed char*)leftImage, (signed char*)rightImage, header.zeilen, header.spalten,header.tau, profile, (unsigned char*)valid, header.fenster_breite, header.fenster_hoehe, 0, 0, 0);
			
			/*if (send(new_fd, &profile, sizeof(header.zeilen * header.spalten) * sizeof(int), 0) == -1) {
				perror("send");
				exit(1);
			}
			printf("send profile \n");
			
			if (send(new_fd, &valid, sizeof(header.zeilen * header.spalten) * sizeof(char), 0) == -1) {
				perror("send");
				exit(1);
			}
			printf("send valid \n");*/
			
			int send_byte_number = 0;
			char* curDataPointer = (char*)profile;
			printf("bytes to send %d\n", header.zeilen * header.spalten * sizeof(int));
			while(send_byte_number < header.zeilen * header.spalten * sizeof(int)) 
			{
				send_byte_number = send_byte_number + send(new_fd, (curDataPointer + send_byte_number), header.zeilen * header.spalten * sizeof(int) - send_byte_number, 0);
				if(send_byte_number == -1) perror("send");
				printf("send %d\n", send_byte_number);
			}
			printf("send profile image successfully\n", send_byte_number);
			
			send_byte_number = 0;
			printf("bytes to send %d\n", header.zeilen * header.spalten * sizeof(char));
			while(send_byte_number < header.zeilen * header.spalten * sizeof(char)) 
			{
				send_byte_number = send_byte_number + send(new_fd, (valid + send_byte_number), header.zeilen * header.spalten * sizeof(char) - send_byte_number, 0);
				printf("send %d\n", send_byte_number);
			}
			printf("send valid image successfully\n", send_byte_number);
					
			close(new_fd);
			exit(0);
		}	
		close(new_fd);  // parent doesn't need this
	}

	return 0;
}

#include "soapH.h"
#include "StereoLabSoap12Binding.nsmap"
//~ #include "kernel.h"

//~ // TCP Port number
//~ #define PORT 10000

//~ int main(int argc, char **argv)
//~ {
  //~ SOAP_SOCKET m, s; /* master and slave sockets */
  //~ struct soap soap;
  //~ soap_init(&soap);

  //~ m = soap_bind(&soap, NULL, PORT, 100);
    //~ if (!soap_valid_socket(m))
    //~ {
      //~ soap_print_fault(&soap, stderr);
      //~ exit(-1);
    //~ }
    //~ fprintf(stderr, "Socket connection successful: master socket = %d\n", m);
    //~ for ( ; ; )
    //~ {
      //~ fprintf(stdout, "Waiting for client\n");
      //~ s = soap_accept(&soap);
      //~ fprintf(stderr, "Socket connection successful: slave socket = %d\n", s);
      //~ if (!soap_valid_socket(s))
      //~ {
        //~ soap_print_fault(&soap, stderr);
        //~ exit(-1);
      //~ } 
      //~ soap_serve(&soap);
      //~ soap_end(&soap);
    //~ }

  //~ return 0;
//~ } 

int __ns1__CalcImage(struct soap *soap, struct ns1__CalcImageParams *p, struct ns1__CalcImageResult *r)
{
    printf("Called CalcImage with spalten=%i, zeilen=%i, tauMax=%i, b=%i, h=%i, useS=%i, useF=%i, s=%i\n",
		p->columns, p->rows, p->tauMax, p->b,p->h,p->useS,p->useF,p->s);

    // some sanity checks to avoid surprises
    if (p->__sizeleftImage != p->rows * p->columns) {
	fprintf(stderr, "Bad size of array leftImage (%i instead of %i)\n", r->__sizeprofile, p->rows * p->columns);
	return SOAP_ERR;
    }	

    if (p->__sizerightImage != p->rows * p->columns) {
	fprintf(stderr, "Bad size of array rightImage (%i instead of %i)\n", r->__sizeprofile, p->rows * p->columns);
	return SOAP_ERR;
    }	

    // create return message (we assume gsoap will free this later :)    
    r->__sizeprofile = p->rows * p->columns;
    r->profile = (int *)calloc(p->rows * p->columns, sizeof(int));
    r->__sizevalid = p->rows * p->columns;
    r->valid = (bool *)calloc(p->rows * p->columns, sizeof(bool));
            
    // do calculation
    doCUDACalc((signed char*)p->leftImage, (signed char*)p->rightImage, p->rows, p->columns,p->tauMax, r->profile, (unsigned char*)r->valid, p->b, p->h, p->useS, p->useF, p->s);
    
    return SOAP_OK;
}

