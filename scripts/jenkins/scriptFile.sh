#!/usr/bin/env bash

mkdir -p target/test-reports
cat > target/test-reports/branchPolicy.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="BranchPolicy" tests="1" errors="0" failures="1" time="1.0">
  <testcase name="The pull request should only contain modification valid on the target branch" time="1.0">
    <failure message="The pull request contained modifications illegal on a project-specific branch" type="BuildError" >
  The pull request contained modifications illegal on a project-specific branch. Those modifications should first be merged into develop, then ported into the project-specific branch:

$ILLEGAL_FILES
    </failure>
  </testcase>
</testsuite>