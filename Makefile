# Author:	Abdullah Al Mamun
# File: 	Makefile

all: src/pathOramHw/Job1A.class src/pathOramHw/Job2A.class src/pathOramHw/Job3A.class src/pathOramHw/Job1B.class src/pathOramHw/Job2B.class src/pathOramHw/Job3B.class

src/pathOramHw/Job%.class: src/pathOramHw/*.java | bin
	javac src/pathOramHw/*.java
bin:
	mkdir bin

clean:
	rm src/pathOramHw/*.class