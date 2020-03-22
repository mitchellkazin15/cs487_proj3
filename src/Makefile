all: java CMDImpl

java:
	javac *.java

CMDImpl:
	gcc -o libCMDImpl.so -lc -shared -I/usr/lib/jvm/java-1.8.0-openjdk-amd64/include -I/usr/lib/jvm/java-1.8.0-openjdk-amd64/include/linux CMDImpl.c -fPIC
	export LD_LIBRARY_PATH=.

clean:
	rm -rf *.class *.o *.so
