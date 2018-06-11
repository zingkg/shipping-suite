package com.zingkg.packingslipcreator

import com.github.tototoshi.csv.CSVReader

object Main extends App {
  case class Config(inputFile: String = "", maybeOutputFile: Option[String] = None)

  val parser = new scopt.OptionParser[Config]("packingslipcreator") {
    opt[String]('i', "input-file")
      .required()
      .text("input file to parse")
      .action { (inputFile, config) =>
        config.copy(inputFile = inputFile)
      }

    opt[String]('o', "output-file")
      .text("output file to save to")
      .action { (outputFile, config) =>
        config.copy(maybeOutputFile = Some(outputFile))
      }
  }

  parser.parse(args, Config()).foreach { config =>
    val lines = readFile(config.inputFile)
    val latex = processLines(lines)
    writeFile(latex, config.maybeOutputFile.getOrElse("a.tex"))
  }

  private def readFile(filename: String): Seq[Seq[String]] = {
    val reader = CSVReader.open(new java.io.File(filename))
    val lines = reader.all().drop(1)
    reader.close()
    lines
  }

  private def processLines(lines: Seq[Seq[String]]): Seq[String] =
    Latex.header ++
      Latex.generateLatex(
        lines.map(PackingSlip.fromTokens)
          .sliding(2, 2)
          .map { window =>
            val left = window.head
            val maybeRight = if (window.size > 1) Some(window.last) else None
            (left, maybeRight)
          }
      ) ++
      Seq(Latex.endDocument)

  private def writeFile(latex: Seq[String], output: String): Unit = {
    val writer = new java.io.BufferedWriter(new java.io.FileWriter(output))
    latex.foreach { line =>
      writer.write(line + '\n')
    }
    writer.close()
  }
}
