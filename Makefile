SBT = sbt
OPEN = open

build:
	${SBT} compile

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-core-scala && ${SBT} clean

develop:
	${SBT} fastOptJS && ${OPEN} index-dev.html

.PHONY: build test console clean develop

