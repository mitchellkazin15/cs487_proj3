all: build

build:
	make clean -C src/
	make -C src/

manager:
	java src.RPCManager

agent:
	javac -h src/ src/CmdRegister.java
	java -Djava.library.path="src/" src.CmdRegister

clean:
	make clean -C src/
