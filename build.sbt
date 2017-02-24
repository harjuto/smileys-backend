import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := """smileys-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(DockerPlugin)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.pauldijou" %% "jwt-core" % "0.9.2",
  "com.typesafe.play" %% "anorm" % "2.5.0"
)

maintainer in Docker := "Tomi Harju"
dockerExposedPorts := Seq(9000)
packageSummary in Docker := "Smileys - backend service"
packageDescription := "Backend microservice"
//mappings in Docker += file("aws/Dockerrun.aws.json") -> "Dockerrun.aws.json"

dockerEntrypoint := Seq()

// use filterNot to return all items that do NOT meet the criteria
dockerCommands := dockerCommands.value.filterNot {
  // ExecCmd is a case class, and args is a varargs variable, so you need to bind it with @
  case ExecCmd("ENTRYPOINT", args @ _*) => args.isEmpty

  // don't filter the rest; don't filter out anything that doesn't match a pattern
  case cmd                       => false
}

dockerCommands ++= Seq(
  // setting the run script executable
  ExecCmd("RUN", "chmod", "u+x", s"${(defaultLinuxInstallLocation in Docker).value}/bin/${executableScriptName.value}"),
  // setting a daemon user
  Cmd("USER", "daemon"),
  // we want to use the exec form of ENTRYPOINT to get environment variables evaluated
  Cmd("ENTRYPOINT",
    s"exec bin/${executableScriptName.value}" +
      " -Dpidfile.path=/dev/null"
  )
)


/**
  * This task creates a zip file for use in AWS Beanstalk.
  * This depends on docker:stage to create the base files, then zips them up.
  * The zip file will be in the target/aws directory with a name like:
  * aws_build_<name>_<version>_currentTimeMillis.zip
  */
lazy val packageAWS = taskKey[File]("Create Package for AWS Beanstalk.")

packageAWS := {
  val dependsOn = (stage in config("docker")).value

  // we are going to place the zip output file in the target directly
  val targetDirectory = (baseDirectory in Compile).value / "target" / "aws"
  // get all the files and subdirectories of the stage task from docker
  val inputs = Path.allSubpaths((stage in config("docker")).value)
  val zipName = Seq("aws_build", name.value, version.value, System.currentTimeMillis()).mkString("_")
  val output: File = targetDirectory / (zipName + ".zip")
  IO.zip(inputs, output)
  println("Package is located here: " + "'" + output.toPath + "'")
  output
}