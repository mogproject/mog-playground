SBT = sbt
OPEN = open

build:
	${SBT} compile

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	${SBT} clean

develop:
	${SBT} fastOptJS && ${OPEN} index-dev.html

link:
	ln -s ${PWD}/../mog-core-scala/shared/src/main/scala/com/mogproject/mogami/* ./src/main/scala/com/mogproject/mogami/

unlink:
	rm -f ./src/main/scala/com/mogproject/mogami/core ./src/main/scala/com/mogproject/mogami/util ./src/main/scala/com/mogproject/mogami/package.scala

.PHONY: build test console clean develop link unlink

