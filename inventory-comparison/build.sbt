lazy val root = project
  .in(file("."))
  .settings(
    name := "inventory-comparison",
    description := "Inventory Comparator",
    version := "0.5",
    scalaVersion := "3.0.0",
    Compile / mainClass := Some("com.zingkg.comparison.Main"),
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.0.1",
      "com.github.tototoshi" %% "scala-csv" % "1.3.8",
      "org.scalactic" %% "scalactic" % "3.2.9" % Test,
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % "test"
    )
  )
