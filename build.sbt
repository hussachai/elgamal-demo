name := "elgamal-demo"

version := "1.0-SNAPSHOT"

javacOptions ++= Seq("-source", "1.6")

javacOptions ++= Seq("-target", "1.6")

scalacOptions += "-target:jvm-1.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.bouncycastle" % "bcprov-jdk14" % "1.50"
)     

play.Project.playScalaSettings
