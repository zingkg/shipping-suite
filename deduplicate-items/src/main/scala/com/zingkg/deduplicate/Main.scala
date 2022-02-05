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
      val unprocessedLines = deduplicated.map { case (_, row) => Common.unprocessLine(row) }.toSeq
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

case class Row(itemId: String, count: Int, price: String)

object Common {
  def accumulateItems(rows: Seq[Row]): Map[(String, String), Row] =
    rows.foldLeft(Map.empty[(String, String), Row]) {
      case (mapSoFar, Row(key, count, price)) =>
        val entry = mapSoFar.getOrElse((key, price), Row(key, 0, price))
        mapSoFar + ((key, price) -> entry.copy(count = entry.count + count))
    }

  def processLine(line: Seq[String]): Row =
    Row(line(0), line(1).toInt, line(2))

  def unprocessLine(row: Row): Seq[String] =
    Seq(row.itemId, row.count.toString, row.price)
}
