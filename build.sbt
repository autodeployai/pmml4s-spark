name := "pmml4s-spark"

version := "0.9.1"

organization := "org.pmml4s"

organizationHomepage := Some(new URL("http://pmml4s.org"))

description := "A Spark transformer takes in PMML"

homepage := Some(new URL("https://github.com/autodeployai/pmml4s-spark"))

startYear := Some(2017)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalacOptions := Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions in(Compile, doc) := Seq("-no-link-warnings")

libraryDependencies ++= {
  Seq(
    "org.pmml4s" %% "pmml4s" % "0.9.1",
    "org.apache.spark" %% "spark-mllib" % "2.4.3" % "provided",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}

crossScalaVersions := Seq("2.11.12", "2.12.8")

scalaVersion := "2.12.8"

// publishing

updateOptions := updateOptions.value.withGigahorse(false)

crossScalaVersions := Seq("2.12.8", "2.11.12")

publishMavenStyle := true

useGpg := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git://github.com/autodeployai/pmml4s-spark.git</url>
    <connection>scm:git:git@github.com:autodeployai/pmml4s-spark.git</connection>
  </scm>
    <developers>
      <developer>
        <id>scorebot</id>
        <name>Score Bot</name>
      </developer>
    </developers>
