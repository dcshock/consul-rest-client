organization := "com.github.dcshock"

name := "consul-rest-client"

version := "0.8"

libraryDependencies ++= Seq(
  "com.mashape.unirest" % "unirest-java" % "1.3.27",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.2",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

// With this enabled, compiled jars are easier to debug in other projects
// variable names are visible.
javacOptions ++= Seq("-g:lines,vars,source")

javacOptions in doc := Seq()

conflictManager := ConflictManager.strict

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-n", "-a")

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Inform sbt-eclipse to not add Scala nature
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Remove scala dependency for pure Java libraries
autoScalaLibrary := false

crossPaths := false

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/dcshock/consul-rest-client</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:dcshock/consul-rest-client.git</url>
    <connection>scm:git:git@github.com:dcshock/consul-rest-client.git</connection>
  </scm>
  <developers>
    <developer>
      <id>dcshock</id>
      <name>Matt Conroy</name>
      <url>http://www.mattconroy.com</url>
    </developer>
  </developers>)

usePgpKeyHex("4E8CE1EFE9D49D46")
