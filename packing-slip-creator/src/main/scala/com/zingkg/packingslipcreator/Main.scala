package com.zingkg.packingslipcreator

import java.io.BufferedWriter
import java.io.FileWriter

import scala.io.Source

object Main extends App {
  case class Config(inputFile: String = "", outputFile: String = "a.tex")

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
        config.copy(outputFile = outputFile)
      }
  }

  parser.parse(args, Config()).foreach { config =>
    val latex = readFile(config.inputFile)
    writeFile(latex, config.outputFile)
  }

  private def readFile(input: String): Seq[String] = {
    val source = Source.fromFile(input)
    val packingSlipPairs = source.getLines().drop(1)
      .map(PackingSlip.fromCSVLine)
      .sliding(2, 2)
      .map { window =>
        val left = window.head
        val maybeRight = if (window.size > 1) Some(window.last) else None
        (left, maybeRight)
      }
    val latex = Latex.generateLatex(packingSlipPairs)
    source.close()
    latex
  }

  private def writeFile(latex: Seq[String], output: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(output))
    latex.foreach { line =>
      writer.write(line + '\n')
    }
    writer.close()
  }
}
