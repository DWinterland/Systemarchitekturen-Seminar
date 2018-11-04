#include <stdlib.h>
#include <jni.h>
#include "cuda-jni.h"
#include "soapH.h" // obtain the generated stub
#include "StereoLabSoap12Binding.nsmap"
#include "server-list.h"
#include <pthread.h>

//#define DEBUG

#define STD_PORT "5000"

struct callParams {
    //struct soap soap;
    const char *server_address;
    ns1__CalcImageParams params;
    ns1__CalcImageResult ret;
    bool *valid;
    int *profile;
};
                                
struct img_header {
	int zeilen;
	int spalten;
	int tau;
	int fenster_breite;
	int fenster_hoehe;
};

/**
 * Copy an image matrix (byte) from java mem to cuda mem
 */
void copyImageFromJNI(JNIEnv *env, signed char *dest, jobjectArray src, int zeilen, int spalten)
{
	for (int i = 0; i < zeilen; i++) {
//		printf("Getting row %i\n", i);
		jobject zeile = env->GetObjectArrayElement(src, i);
//		printf("Copying row %i\n", i);
		env->GetByteArrayRegion((_jbyteArray*)zeile, 0, spalten, dest+i*spalten);
	}

}

/**
 * Copy a boolean matrix from cuda mem to java mem
 */
void copyBooleanMatrixToJNI(JNIEnv *env, jobjectArray dest, bool *src, int zeilen, int spalten, int start_zeile)
{

    for (int i = 0; i < zeilen; i++) {
#ifdef DEBUG
	printf("V-Zeile %i: ",i);
	for (int j = 0; j < spalten; j++) {
	    printf(" %i",*(src+i*spalten+j));
        }
	printf("\n");
#endif
            //printf("zeile\n");
	jobject zeile = env->GetObjectArrayElement(dest, i+start_zeile);
            //printf("boolean\n");
	env->SetBooleanArrayRegion((_jbooleanArray*)zeile, 0, spalten, (jboolean*)((src+i*spalten)));
    }
}

/**
 * Copy a long matrix from cuda mem to java mem
 */
void copyIntMatrixToJNI(JNIEnv *env, jobjectArray dest, jint *src, int zeilen, int spalten, int start_zeile)
{

    for (int i = 0; i < zeilen; i++) {
#ifdef DEBUG
	printf("P-Zeile %i: ",i);
	for (int j = 0; j < spalten; j++) {
	    printf(" %i",*(src+i*spalten+j));
        }
	printf("\n");
#endif
	jobject zeile = env->GetObjectArrayElement(dest, i+start_zeile);
	env->SetIntArrayRegion((_jintArray*)zeile, 0, spalten, (src+i*spalten));
    }
}

void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}

	return &(((struct sockaddr_in6*)sa)->sin6_addr);
    
}

void sendAllTCP(int socket, char* dataBuf, int size) {
	
	int send_bytes = 0;
	//printf("bytes to send %d\n", size);
	while(send_bytes < size) 
	{
		send_bytes += send(socket, &dataBuf[send_bytes], size - send_bytes, 0);
		//printf("send %d\n", send_byte_number);
	}
	//printf("send successfully\n", send_byte_number);
}	

void receiveAll(int socket, char* dataBuf, int size) {
	
	int rec_bytes = 0;
	//printf("bytes to send %d\n", header.zeilen * header.spalten * sizeof(char));
	while(rec_bytes < size) 
	{
		rec_bytes += recv(socket, &dataBuf[rec_bytes], size - rec_bytes, 0);
		//printf("send %d\n", send_byte_number);
	}
	//printf("send successfully\n", send_byte_number);
}	

void callServer(struct callParams *params) {
	
	int sockfd, numbytes;  
	struct addrinfo hints, *servinfo, *p;
	int rv;
	char s[INET6_ADDRSTRLEN];
        
	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;

	printf("%s\n", params->server_address);
	if ((rv = getaddrinfo(params->server_address, STD_PORT, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return ;
	}

	// loop through all the results and connect to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("client: socket");
			continue;
		}

		if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			perror("client: connect");
			close(sockfd);
			continue;
		}

		break;
	}

	if (p == NULL) {
		fprintf(stderr, "client: failed to connect\n");
		return ;
	}

	inet_ntop(p->ai_family, get_in_addr((struct sockaddr *)p->ai_addr),
			s, sizeof s);
	printf("client: connecting to %s\n", s);

	freeaddrinfo(servinfo); // all done with this structure
    
    img_header header;	
    header.zeilen = params->params.rows;
	header.spalten = params->params.columns;
	header.tau = params->params.tauMax;
	header.fenster_breite = params->params.b;
	header.fenster_hoehe = params->params.h;
    
	send(sockfd, &header, sizeof(header), 0);
    
    sendAllTCP(sockfd, params->params.leftImage, header.zeilen*header.spalten*sizeof(char));
    sendAllTCP(sockfd, params->params.rightImage, header.zeilen*header.spalten*sizeof(char));
    
    receiveAll(sockfd, (char*)params->profile, header.zeilen*header.spalten*sizeof(int));
    receiveAll(sockfd, (char*)params->valid, header.zeilen*header.spalten*sizeof(char));
	close(sockfd);
}


/**
 * This is the entry method. It will be called by Java when StereoLab wants to perform a computation
 *
 */
JNIEXPORT void JNICALL Java_stereolab_CUDADistribDiff_doCalculationNative
  (JNIEnv *env, jclass clazz, jobjectArray lbJ, jobjectArray rbJ, jint b, jint h, jint tauMax,
	jboolean useS, jboolean useF, jint s, jobjectArray profileJ, jobjectArray validJ)
{

	int zeilen = env->GetArrayLength(lbJ);

	jobject zeile1 = env->GetObjectArrayElement(lbJ, 0);
	int spalten = env->GetArrayLength((_jarray*)zeile1);

	printf("Performing distributed CUDA with spalten=%i, zeilen=%i, tauMax=%i, b=%i, h=%i, useS=%i, useF=%i, s=%i\n",
			spalten, zeilen, tauMax, b,h,useS,useF,s);

	signed char *lb = (signed char *)calloc(zeilen*spalten,sizeof(char));
	signed char *rb = (signed char *)calloc(zeilen*spalten,sizeof(char));

	printf("Copying left image from Java to C\n");
	copyImageFromJNI(env, lb, lbJ, zeilen, spalten);
	printf("Copying right image from Java to C\n");
	copyImageFromJNI(env, rb, rbJ, zeilen, spalten);

	printf("Starting %i threads\n", server_count);
	pthread_t tid[server_count]; 
        struct callParams params[server_count];
	for (int i=0; i<server_count; i++) {

	    int start_zeile = (i * zeilen - 1) / server_count - h; // 153
	    int stop_zeile = ((i+1) * zeilen - 1) / server_count  + h;
	    
	    if (start_zeile<0)
		start_zeile = 0;
		
	    if (stop_zeile>=zeilen)
		stop_zeile = zeilen-1;
		
	    int z = stop_zeile - start_zeile;

	    params[i].valid = (bool *)calloc(z*spalten,sizeof(bool));
	    params[i].profile = (int *)calloc(z*spalten,sizeof(int));

	    printf("Configuring params for %i\n", i);
            params[i].params.__sizeleftImage = spalten*z;
            params[i].params.leftImage = (char *)lb+start_zeile*spalten;
	    params[i].params.__sizerightImage = spalten*z;
	    params[i].params.rightImage = (char *)rb+start_zeile*spalten;
	    params[i].params.columns = spalten;
	    params[i].params.rows = z;
	    params[i].params.b = b;
	    params[i].params.h = h;
	    params[i].params.tauMax = tauMax;
	    params[i].params.useS = useS;
	    params[i].params.useF = useF;
            params[i].params.s = s;
        
	    params[i].server_address = servers[i];
	    
	    printf("Starting thread %i\n", i);
        pthread_create(&tid[i], NULL, (void*(*)(void*))callServer, (void*)&params[i]); 
        
	}

	for (int i=0; i<server_count; i++) {
            printf("Waiting for thread %i to finish\n", i);

	    // waiting for call to finish
	    pthread_join(tid[i], NULL);

	    int start_zeile = (i * zeilen -1) / server_count;
	    int stop_zeile = ((i+1) * zeilen -1) / server_count;
	    
	    if (start_zeile<0)
		start_zeile = 0;
	    
	    if (stop_zeile>=zeilen)
		stop_zeile = zeilen-1;
		
	    int z = stop_zeile - start_zeile;
	    
	    int skip = start_zeile == 0 ? 0 : spalten*h;

            printf("Copying results back to Java\n");
    	    copyBooleanMatrixToJNI(env, validJ, params[i].valid + skip, z, spalten, start_zeile);
	    copyIntMatrixToJNI(env, profileJ, params[i].profile + skip, z, spalten, start_zeile);
	    
	    free(params[i].valid);
	    free(params[i].profile);

	}

	// clean up
        free(lb);
        free(rb);

}


