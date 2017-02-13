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

publish:
	${SBT} fullOptJS && cp -f target/scala-2.12/mog-playground-opt.js docs/assets/js/ && cp -f css/main.css docs/assets/css/

.PHONY: build test console clean develop publish

