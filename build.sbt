name := "scalaz-interpreter"
version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val scalazV = "7.2.0"
  val scalaTestV = "2.2.5"
  val catsV = "0.4.1"

  Seq(
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",    // Resolves conflicts, delete it if sbt is 0.13.10
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,  // Resolves conflicts, delete it if sbt is 0.13.10

    "org.scalaz" %% "scalaz-core" % scalazV,
    "org.typelevel" %% "cats" % catsV,

    "org.scalatest"  %% "scalatest"  % scalaTestV,

    compilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
  )
}
