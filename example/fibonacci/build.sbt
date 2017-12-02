name := "fibonacci"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

lazy val fibonacci = application("project", ".")

def application(id: String, path: String) = {
  Project(
    id = id,
    base = file(path)
  ).enablePlugins(BuildInfoPlugin, JavaAppPackaging)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](
        name,
        version,
        scalaVersion
      ),
      buildInfoPackage := "sbt",
      buildInfoOptions += BuildInfoOption.BuildTime,
      javaOptions in Universal += "-Dfile.encoding=UTF-8",
      scalacOptions ++= Seq("-deprecation")
    )
}
