#!/usr/bin/env bash

set -e

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

logging "Publishing plugin"
(cd plugins; sbt publishLocal)

# Compilation
./scripts/jenkins/compile.sh

# Tests
./scripts/jenkins/test.sh

logging "Finished building"
