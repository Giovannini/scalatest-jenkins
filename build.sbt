name := "bacasable"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

lazy val realProject = application("project", "src/project")

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
