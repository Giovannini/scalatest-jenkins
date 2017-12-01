#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

apiLink="https://api.github.com/repos/${ghprbGhRepository}/pulls/${ghprbPullId}/comments"
apiLinkIssue="https://api.github.com/repos/${ghprbGhRepository}/issues/${ghprbPullId}/comments"

logging "Running tests"
sbt test \
  || (
    logging "Got errors in some tests. Analyzing that..."
    git diff --name-only origin/master > ./modifiedFiles
    git ls-files > ./allfiles
    ls
    sbt test2jenkins
    exit 2
  )
