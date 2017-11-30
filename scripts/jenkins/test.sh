#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

logging "Running tests"
sbt test \
  || (
    logging "Got errors in some tests. Analyzing that..."
    git diff --name-only master > ./modifiedFiles
    sbt "scalaTestPlugin/run src/project/target/test-reports"
    cat ./testMessages.json
    exit 2
  )
