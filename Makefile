SBT = sbt
OPEN = open
APP_NAME = mog-playground
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
DEV_PORT = 8000
COPY_PROD = cp -f target/scala-2.12/${APP_NAME}-opt.js ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/

build:
	${SBT} fastOptJS

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:${DEV_PORT}/index-dev.html?debug=true

local_prod:
	${OPEN} http://localhost:${DEV_PORT}/

server:
	python -m 'http.server' ${DEV_PORT}

server_prod:
	cd docs && python -m 'http.server' ${DEV_PORT}

publish: clean test
	sbt fullOptJS && ${COPY_PROD}

publish_assets:
	${COPY_PROD}

.PHONY: build test console clean local local_prod server server_prod publish publish_assets

