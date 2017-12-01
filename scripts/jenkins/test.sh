#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

logging "Running tests"
sbt test \
  || (
    logging "Got errors in some tests. Analyzing that..."
    sbt test2jenkins
    exit 2
  )
