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

.PHONY: build test console clean develop

