#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}


logging "Compiling the project"
sbt compilationReport
sbt -v $SBT_OPTS test:compile \
  || (
    logging "Aborting the build: failed to compile the project"
    exit 1
  )
