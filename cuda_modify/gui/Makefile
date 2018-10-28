all: stereolab/StereoLab.class

stereolab/StereoLab.class: stereolab/*.java
	javac stereolab/*.java

.PHONY: clean run
clean:
	rm stereolab/*.class 
run:
	java stereolab.StereoLab
