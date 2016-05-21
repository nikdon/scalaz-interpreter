lazy val buildSettings = Seq(
  name := "scalaz-interpreter",
  version := "1.0",
  organization := "com.github.nikdon",
  scalaVersion := "2.11.8"
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions ++ Seq(
    "-Ywarn-unused-import"
  ),
  testOptions in Test += Tests.Argument("-oF"),
  scalacOptions in(Compile, console) := compilerOptions,
  scalacOptions in(Compile, test) := compilerOptions,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

buildSettings ++ baseSettings

libraryDependencies ++= {
  val scalazV = "7.2.3"
  val scalaTestV = "2.2.6"
  val catsV = "0.6.0"
  val shapelessV = "2.3.1"

  Seq(
    "com.chuusai"   %% "shapeless"    % shapelessV,
    "org.scalaz"    %% "scalaz-core"  % scalazV,
    "org.typelevel" %% "cats"         % catsV,

    "org.scalatest"  %% "scalatest"  % scalaTestV,

    compilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
  )
}
