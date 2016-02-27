name := "scalaz-interpreter"
version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val scalazV = "7.2.0"
  val scalaTestV = "2.2.5"
  val catsV = "0.4.1"

  Seq(
    "org.scalaz" %% "scalaz-core" % scalazV,
    "org.typelevel" %% "cats" % catsV,

    "org.scalatest"  %% "scalatest"  % scalaTestV,

    compilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
  )
}
