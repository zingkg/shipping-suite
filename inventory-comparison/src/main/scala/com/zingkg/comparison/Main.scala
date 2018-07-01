package com.zingkg.comparison

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.CSVWriter
import scala.util.Try

object Main extends App {
  case class Config(inputFile: String = "", maybeOutputFile: Option[String] = None)

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
        config.copy(maybeOutputFile = Some(outputFile))
      }
  }

  def readFile(filename: String): Seq[Seq[String]] = {
    val reader = CSVReader.open(new java.io.File(filename))
    val lines = reader.all().drop(1)
    reader.close()
    lines
  }

  def writeFile(filename: String, untypedLines: Seq[Seq[String]]): Unit = {
    val writer = CSVWriter.open(new java.io.File(filename))
    writer.writeAll(untypedLines)
    writer.close()
  }

  parser.parse(args, Config()).foreach { config =>
    val lines = readFile(config.inputFile)
    val processedLines = lines.map(Common.processLine)
    val (inventory1, inventory2) = Common.accumulateItems(processedLines)
    val matchingLines = Common.assembleMatchingLines(inventory1, inventory2).sortBy(
      _.maybeItem1.map(_.itemId).getOrElse("~~~~~")
    )
    val unprocessedLines = matchingLines.map(Common.unprocessLine)
    writeFile(config.maybeOutputFile.getOrElse("a.csv"), unprocessedLines)
  }
}

object Common {
  case class Item(
    itemId: String,
    quantity: Int,
    maybeColumn1: Option[String] = None,
    maybeColumn2: Option[String] = None,
    maybeColumn3: Option[String] = None,
    maybeColumn4: Option[String] = None,
    maybeColumn5: Option[String] = None,
    maybeColumn6: Option[String] = None,
    maybeColumn7: Option[String] = None,
    maybeColumn8: Option[String] = None,
    maybeColumn9: Option[String] = None,
    maybeColumn10: Option[String] = None,
  ) {
    def extraColumns: Seq[String] = {
      val extras = Seq(
        maybeColumn1,
        maybeColumn2,
        maybeColumn3,
        maybeColumn4,
        maybeColumn5,
        maybeColumn6,
        maybeColumn7,
        maybeColumn8,
        maybeColumn9,
        maybeColumn10
      )
      if (extras.exists(_.isDefined))
        extras.map(_.getOrElse(""))
      else
        Seq.empty
    }
  }
  case class Line(maybeItem1: Option[Item], maybeItem2: Option[Item])

  def accumulateItems(lines: Seq[Line]): (Map[String, Item], Map[String, Item]) =
    lines.foldLeft((Map.empty[String, Item], Map.empty[String, Item])) {
      case ((inventory1, inventory2), line) =>
        val updatedInventory1 = updateInventory(inventory1, line.maybeItem1)
        val updatedInventory2 = updateInventory(inventory2, line.maybeItem2)
        (updatedInventory1, updatedInventory2)
    }

  private def updateInventory(
    inventory: Map[String, Item],
    maybeItem: Option[Item]): Map[String, Item] =
    maybeItem.map { item =>
      val updatedItem = inventory.get(item.itemId)
        .map(_.copy(quantity = item.quantity))
        .getOrElse(item)
      inventory + (item.itemId -> updatedItem)
    }.getOrElse(inventory)

  def processLine(line: Seq[String]): Line =
    Line(
      processItem(line.head, line(1)),
      processItem(
        line(2),
        line(3),
        maybeColumn1 = parseOptional(line, 4),
        maybeColumn2 = parseOptional(line, 5),
        maybeColumn3 = parseOptional(line, 6),
        maybeColumn4 = parseOptional(line, 7),
        maybeColumn5 = parseOptional(line, 8),
        maybeColumn6 = parseOptional(line, 9),
        maybeColumn7 = parseOptional(line, 10),
        maybeColumn8 = parseOptional(line, 11),
        maybeColumn9 = parseOptional(line, 12),
        maybeColumn10 = parseOptional(line, 13)
      )
    )

  private def parseOptional(tokens: Seq[String], pos: Int): Option[String] =
    if (tokens.length <= pos || tokens(pos).isEmpty)
      None
    else
      Some(tokens(pos))

  def processItem(
    itemId: String,
    quantity: String,
    maybeColumn1: Option[String] = None,
    maybeColumn2: Option[String] = None,
    maybeColumn3: Option[String] = None,
    maybeColumn4: Option[String] = None,
    maybeColumn5: Option[String] = None,
    maybeColumn6: Option[String] = None,
    maybeColumn7: Option[String] = None,
    maybeColumn8: Option[String] = None,
    maybeColumn9: Option[String] = None,
    maybeColumn10: Option[String] = None
  ): Option[Item] =
    if (itemId.nonEmpty) {
      Some(
        Item(
          itemId,
          Try(quantity.toInt).getOrElse(0),
          maybeColumn1,
          maybeColumn2,
          maybeColumn3,
          maybeColumn4,
          maybeColumn5,
          maybeColumn6,
          maybeColumn7,
          maybeColumn8,
          maybeColumn9,
          maybeColumn10
        )
      )
    } else {
      None
    }

  def unprocessLine(line: Line): Seq[String] = {
    val item1Extras = line.maybeItem1.map(_.extraColumns).getOrElse(Seq.empty)
    val item2Extras = line.maybeItem2.map(_.extraColumns).getOrElse(Seq.empty)
    val extras = if (item1Extras.nonEmpty) item1Extras else item2Extras
    unprocessItem(line.maybeItem1) ++ unprocessItem(line.maybeItem2) ++ extras
  }

  def unprocessItem(maybeItem: Option[Item]): Seq[String] =
    maybeItem.map(item => Seq(item.itemId, item.quantity.toString)).getOrElse(Seq("", ""))

  def assembleMatchingLines(
    inventory1: Map[String, Item],
    inventory2: Map[String, Item]
  ): Seq[Line] =
    if (inventory1.size >= inventory2.size) {
      val keysMissingInInventory = inventory2.keySet -- inventory1.keySet
      val matchingLines = inventory1.mapValues { item =>
        Line(maybeItem1 = Some(item), maybeItem2 = inventory2.get(item.itemId))
      }.values.toSeq
      matchingLines ++
        inventory2.filterKeys(keysMissingInInventory.contains(_)).mapValues { item =>
          Line(maybeItem1 = None, maybeItem2 = Some(item))
        }.values.toSeq
    } else {
      val keysMissingInInventory = inventory1.keySet -- inventory2.keySet
      val matchingLines = inventory2.mapValues { item =>
        Line(maybeItem1 = inventory1.get(item.itemId), maybeItem2 = Some(item))
      }.values.toSeq
      matchingLines ++
        inventory1.filterKeys(keysMissingInInventory.contains(_)).mapValues { item =>
          Line(maybeItem1 = Some(item), maybeItem2 = None)
        }.values.toSeq
    }
}
