name := "consul-rest-client"
organization := "com.github.dcshock"
version := "1.6"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.1",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.2",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)
conflictManager := ConflictManager.strict

// With this enabled, compiled jars are easier to debug in other projects
// variable names are visible.
javacOptions ++= Seq("-g:lines,vars,source")
javacOptions in doc := Seq()
javacOptions in doc += "-Xdoclint:none"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-n", "-a")
parallelExecution in Test := false

// Inform sbt-eclipse to not add Scala nature
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Remove scala dependency for pure Java libraries, remove scala version from the generated/published artifact, and publish properly
autoScalaLibrary := false
crossPaths := false
publishMavenStyle := true

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("SONATYPE_USER", ""),
  sys.env.getOrElse("SONATYPE_PASS", "")
)

useGpg := false
usePgpKeyHex("E46770E4F1ED27F3")
pgpPublicRing := baseDirectory.value / "project" / ".gnupg" / "pubring.gpg"
pgpSecretRing := baseDirectory.value / "project" / ".gnupg" / "secring.gpg"
pgpPassphrase := sys.env.get("GPG_PASS").map(_.toArray)

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
