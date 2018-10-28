#!/usr/bin/make
#

EXECUTABLE := server

INCLUDE += -I../gsoap/import -I../gsoap -I../kernel/ -I../include

CFILES +=  server.cpp ../gsoap/stdsoap2.cpp soapC.cpp soapServer.cpp ../kernel/kernel.cu

.PHONY: all gen clean

all: $(EXECUTABLE)

gen:
	../gsoap/bin/linux386/wsdl2h -s -o stereolab.gsoap ../stereolab.wsdl
	../gsoap/bin/linux386/soapcpp2 $(INCLUDE) -w -S stereolab.gsoap

$(EXECUTABLE): gen
	/usr/local/cuda/bin/nvcc -o $(EXECUTABLE) $(INCLUDE) $(CFILES)

clean:
	rm -f *.o *.nsmap *.xml *.gsoap soapH.h soapStub.h soapC.cpp soapServer.cpp soapServerLib.cpp $(EXECUTABLE)
