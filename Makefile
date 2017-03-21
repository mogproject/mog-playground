SBT = sbt
OPEN = open
TEST_RSC = src/test/resources
PROD_RSC = docs
TEST_ASS = ${TEST_RSC}/assets
PROD_ASS = ${PROD_RSC}/assets
COPY_DEV = mkdir -p ${TEST_ASS}/js ${TEST_ASS}/css && cp -f target/scala-2.12/mog-playground-fastopt.js ${TEST_ASS}/js/ && cp -f css/main.css ${TEST_ASS}/css/
COPY_PROD = cp -f target/scala-2.12/mog-playground-opt.js ${PROD_ASS}/js/ && cp -f css/main.css ${PROD_ASS}/css/


build:
	${DEV_CMD}

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-core-scala && ${SBT} clean

local:
	${COPY_DEV} && ${OPEN} http://localhost:8083/test/index-dev.html

publish: test
	sbt fullOptJS && ${COPY_PROD}

.PHONY: build test console clean local publish

