SBT = sbt
OPEN = open
APP_NAME = mog-playground
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
COPY_PROD = cp -f target/scala-2.12/${APP_NAME}-opt.js ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/

build:
	${DEV_CMD}

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:8000/index-dev.html?debug=true

server:
	python -m 'http.server'

publish: test
	sbt fullOptJS && ${COPY_PROD}

publish_assets:
	${COPY_PROD}

.PHONY: build test console clean local server publish publish_assets

