#!/usr/bin/env bash

logging() {
  echo "#>>>" `date +"%H:%M:%S.%3N"` $1
}

apiLink="https://api.github.com/repos/${ghprbGhRepository}/pulls/${ghprbPullId}/comments"
apiLinkIssue="https://api.github.com/repos/${ghprbGhRepository}/issues/${ghprbPullId}/comments"

export SBT_OPTS="-Dcompile-doc=true -Dsbt.log.noformat=true -Dtest.scale-factor=5"
export JAVA_OPTS="-Xmx2048m"


logging "Compiling the project"
sbt compilationReport
sbt -v $SBT_OPTS test:compile \
  || (
    logging "Aborting the build: failed to compile the project"
    exit 1
  )
