#!/usr/bin/env bash

set -e

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

# Prepare build

export SBT_OPTS="-Dcompile-doc=true -Dsbt.log.noformat=true -Dtest.scale-factor=5"
export JAVA_OPTS="-Xmx2048m"

logging "Compiling the project"
sbt -v $SBT_OPTS test:compile \
  || (
    logging "Aborting the build: failed to compile the project"
    exit 1
  )

# Tests
./scripts/jenkins/test.sh

logging "Finished building"
