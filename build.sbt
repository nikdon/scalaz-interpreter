name := "scalaz-interpreter"
version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val scalazV = "7.2.1"
  val scalaTestV = "2.2.5"
  val catsV = "0.4.1"
  val shapelessV = "2.3.0"

  Seq(
    "com.chuusai"   %% "shapeless"    % shapelessV,
    "org.scalaz"    %% "scalaz-core"  % scalazV,
    "org.typelevel" %% "cats"         % catsV,

    "org.scalatest"  %% "scalatest"  % scalaTestV,

    compilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
  )
}
