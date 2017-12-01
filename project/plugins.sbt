addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.2.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
addSbtPlugin("com.github.giovannini" % "jenkinspublisher" % "0.0.2-SNAPSHOT")
