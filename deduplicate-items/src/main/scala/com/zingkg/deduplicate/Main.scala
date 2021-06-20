package com.zingkg.deduplicate

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.CSVWriter
import com.github.tototoshi.csv.defaultCSVFormat

object Main {
  case class Config(inputFile: String = "", outputFile: String = "a.tex")

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("deduplicate") {
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
      val lines = readFile(config.inputFile)
      val processedLines = lines.map(Common.processLine)
      val deduplicated = Common.accumulateItems(processedLines)
      val unprocessedLines = deduplicated.map {
        case (key, count) => Common.unprocessLine(key, count)
      }.toSeq
      writeFile(config.outputFile, unprocessedLines)
    }
  }

  def readFile(filename: String): Seq[Seq[String]] = {
    val reader = CSVReader.open(new java.io.File(filename))
    val lines = reader.all()
    reader.close()
    lines
  }

  def writeFile(filename: String, untypedLines: Seq[Seq[String]]): Unit = {
    val writer = CSVWriter.open(new java.io.File(filename))
    writer.writeAll(untypedLines)
    writer.close()
  }
}

object Common {
  def accumulateItems(tuples: Seq[(String, Int)]): Map[String, Int] =
    tuples.foldLeft(Map.empty[String, Int]) {
      case (mapSoFar, (key, count)) =>
        mapSoFar + (key -> (count + mapSoFar.getOrElse(key, 0)))
    }

  def processLine(line: Seq[String]): (String, Int) =
    (line(1), line(2).toInt)

  def unprocessLine(key: String, count: Int): Seq[String] =
    Seq(key, count.toString)
}
