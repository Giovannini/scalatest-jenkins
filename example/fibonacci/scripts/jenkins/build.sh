#!/usr/bin/env bash

set -e

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

export SBT_OPTS="-Dcompile-doc=true -Dsbt.log.noformat=true -Dtest.scale-factor=5"
export JAVA_OPTS="-Xmx2048m"

logging "Publishing plugin"
(cd ../.. && sbt publishLocal)

# Compilation
./scripts/jenkins/compile.sh

# Tests
./scripts/jenkins/test.sh

logging "Finished building"
