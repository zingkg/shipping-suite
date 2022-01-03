package com.zingkg.shippinglabelcreator

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat
import scala.util.{Failure, Success, Try}

object Main {
  class ParsingException(exception: Throwable, line: Long) extends Exception {
    override def toString: String =
      s"$exception occured on line $line"
  }

  case class Config(inputFile: String = "", maybeOutputFile: Option[String] = None, format: PageFormat = PageFormat.Full)

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("shippinglabelcreator") {
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

      opt[String]("format")
        .text("page format, 4x6 or full")
        .validate { format =>
          if (Seq("4x6", "full").contains(format))
            success
          else
            failure("format must be 4x6 or full")
        }
        .action { (format, config) =>
          if (format == "4x6")
            config.copy(format = PageFormat.FourBySix)
          else if (format == "full")
            config.copy(format = PageFormat.Full)
          else
            config
        }
    }

    parser.parse(args, Config()).foreach { config =>
      val lines = readFile(config.inputFile)
      val processedLines = lines.map(line => Try(ShippingLabel.fromTokens(line))).zipWithIndex
      val failures = processedLines.collect {
        case (Failure(e), x) =>
          Failure(new ParsingException(e, x + 1))
      }
      if (failures.nonEmpty) {
        val failuresString = failures.map(_.toString).mkString("\n")
        throw new RuntimeException(s"Failed to parse lines:\n$failuresString")
      }

      val successes = processedLines.collect {
        case (Success(x), _) =>
          x
      }
      val latex = processLines(successes, config.format)
      writeFile(latex, config.maybeOutputFile.getOrElse("a.tex"))
    }
  }

  private def readFile(filename: String): Seq[Seq[String]] = {
    val reader = CSVReader.open(new java.io.File(filename))
    val lines = reader.all().drop(1)
    reader.close()
    lines
  }

  private def processLines(labels: Seq[ShippingLabel], pageFormat: PageFormat): Seq[String] = {
    val header = pageFormat match {
      case PageFormat.FourBySix => Latex.headerFourBySix
      case PageFormat.Full => Latex.headerPage
    }
    header ++
      Latex.buildLatex(labels) ++
      Seq(Latex.endDocument)
  }

  private def writeFile(latex: Seq[String], output: String): Unit = {
    val writer = new java.io.BufferedWriter(new java.io.FileWriter(output))
    latex.foreach { line =>
      writer.write(line + '\n')
    }
    writer.close()
  }
}
