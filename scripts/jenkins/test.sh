#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

apiLink="https://api.github.com/repos/${ghprbGhRepository}/pulls/${ghprbPullId}/comments"

logging "Running tests"
sbt test \
  || (
    logging "Got errors in some tests. Analyzing that..."
    git diff --name-only origin/master > ./modifiedFiles
    sbt "scalaTestPlugin/run src/project/target/test-reports ${apiLink} ${ghprbActualCommit}"
    cat ./testMessages.json
    exit 2
  )
