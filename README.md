# jenkinspublisher

This SBT plugin goal is to interact with Jenkins plugin
[GitHub pull request builder plugin](https://wiki.jenkins.io/display/JENKINS/GitHub+pull+request+builder+plugin)
and publish comments on Pull Requests that contains errors or warnings.

## Installation

To add the plugin to your project, you need to add those lines into your
`project/plugins.sbt`:
```
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
addSbtPlugin("com.github.giovannini" % "jenkinspublisher" % "0.0.2-SNAPSHOT")
```

You will then have access to new SBT commands:
 * `test2jenkins`
 * `compilationReport`


## Usage

On your Jenkins, install the [GitHub pull request builder plugin](https://wiki.jenkins.io/display/JENKINS/GitHub+pull+request+builder+plugin)

You can create a simple bash script running your tests:
```
#!/usr/bin/env bash

sbt test \
  || ( # sbt test results with some error found
    sbt test2jenkins # <- WOW ->
    exit 1
  )
```

The `sbt test2jenkins` task will get the results of the previously ran `sbt
test` task and publish them directly to the PR these errors occured.

## Contributors

 * See the [contributor page](https://github.com/Giovannini/scalatest-jenkins/graphs/contributors)
