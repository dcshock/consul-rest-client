organization := "consul"

name := "rest-client"

version := "0.1"

scalaVersion := "2.10.0"

// With this enabled, compiled jars are easier to debug in other projects
// variable names are visible.
javacOptions ++= Seq("-g:lines,vars,source")

javacOptions in doc := Seq()

conflictManager := ConflictManager.strict

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-n", "-a")

libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11-SOFI" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "com.mashape.unirest" % "unirest-java" % "1.3.20",
  "com.google.code.gson" % "gson" % "2.2.3",
)

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

crossPaths := false
