GSOAP_HOME := $(HOME)/cuda/gsoap
JAVA_HOME := /usr/lib/jvm/default-java

export JAVA_HOME

help:
	@echo
	@echo Anleitung:
	@echo
	@echo make kernel	Kernel compilieren
	@echo make libnative	JNI-Bibliothek compilieren \(lokal\)
	@echo make libdist	JNI-Bibliothek compilieren \(Web Service\)
	@echo make server	Webservice Server compilieren
	@echo make gui		GUI-Klassen compilieren \(Java\)
	@echo make run-gui	GUI starten
	@echo make run-server	Web-Service-Server starten
	@echo
	@echo make clean	Object files löschen
	@echo

.PHONY: clean kernel libnative libdist server gui run-gui run-server


kernel:
	$(MAKE) -C kernel

libnative: kernel
	$(MAKE) -C libnative

libdist: kernel
	$(MAKE) -C libdist

server:
	$(MAKE) -C server all

gui: gui/stereolab/*.java
	javac gui/stereolab/*.java

run-gui: libnative libdist gui
	java -Djava.library.path=lib -classpath gui stereolab.StereoLab

run-server: libnative server
	server/server

clean:
	$(MAKE) -C kernel clean
	$(MAKE) -C libnative clean
	$(MAKE) -C libdist clean
	$(MAKE) -C server clean
	$(MAKE) -C gui clean
