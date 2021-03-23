name := "packing-slip-creator"

version := "0.10"

scalaVersion := "2.13.5"

mainClass in Compile := Some("com.zingkg.packingslipcreator.Main")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "4.0.1",
  "com.github.tototoshi" %% "scala-csv" % "1.3.7",
  "org.scalactic" %% "scalactic" % "3.2.5" % Test,
  "org.scalatest" %% "scalatest" % "3.2.5" % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.5.0" % "test"
)
