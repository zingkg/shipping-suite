name := "inventory-comparison"

version := "0.3"

scalaVersion := "2.12.6"

mainClass in Compile := Some("com.zingkg.comparison.Main")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.5",
  "org.scalactic" %% "scalactic" % "3.0.5" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
