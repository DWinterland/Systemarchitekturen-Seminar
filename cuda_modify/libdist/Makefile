#!/usr/bin/make
#

BIN := ../lib/libcudadistribstereo.so

INCLUDE += -I../gsoap/import -I../gsoap -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

CPPFILES += cuda-jni.cpp ../gsoap/stdsoap2.cpp soapClient.cpp soapC.cpp

.PHONY: all gen clean

all: $(BIN)

gen:
	../gsoap/bin/linux386/wsdl2h -s -o stereolab.gsoap ../stereolab.wsdl
	../gsoap/bin/linux386/soapcpp2 $(INCLUDE) -w -C stereolab.gsoap

$(BIN): server-list.h gen
	g++ $(INCLUDE) -shared -fPIC -Wno-vla -Wno-format-truncation $(CPPFILES) -o $(BIN)
clean:
	rm -f *.o *.nsmap *.xml *.gsoap soapH.h soapStub.h soapC.cpp soapClient.cpp soapClientLib.cpp $(BIN)
