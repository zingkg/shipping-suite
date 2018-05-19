package com.zingkg.packingslipcreator;

object Latex {
  def generateLatex(
    packingSlipPairs: Iterator[(PackingSlip, Option[PackingSlip])]
  ): Seq[String] = {
    val header = Seq(
      "\\documentclass{article}",
      "\\usepackage[letterpaper, landscape, top=0.1cm, bottom=0.1cm, left=0.2cm, " +
        "right=0.2cm]{geometry}",
      "\\begin{document}"
    )
    val packingSlips = packingSlipPairs.flatMap {
      case (left, maybeRight) =>
        val leftString = packingSlipStrings(left)
        val rightStrings = maybeRight.map { right =>
          Seq(
            Seq("\\hfill"),
            packingSlipStrings(right)
          ).flatten
        }.getOrElse(Seq.empty)
        leftString ++ rightStrings ++ Seq("\\newpage")
    }
    header ++ packingSlips ++ Seq("\\end{document}")
  }

  private def packingSlipStrings(packingSlip: PackingSlip): Seq[String] = {
    val header = Seq(
      "\\begin{minipage}{0.45\\textwidth}",
      "{\\huge",
      s"\\begin{flushright}${packingSlip.company}\\end{flushright}",
      s"\\begin{center}${packingSlip.poId}\\end{center}",
      s"\\begin{center}${packingSlip.shipTo}\\end{center}"
    )
    val shipSpeed = packingSlip.maybeShipSpeed.map { shipSpeed =>
      s"\\begin{center}$shipSpeed\\end{center}"
    }.toSeq ++ Seq("\\vspace{10em}")

    val baseItemRow = s"${packingSlip.itemId} \\hfill ${packingSlip.quantity}"
    val itemRow = packingSlip.maybeCost.map { cost =>
      baseItemRow + s" \\hfill \\$cost"
    }.getOrElse(baseItemRow)

    header ++ shipSpeed ++ Seq(itemRow, "}", "\\end{minipage}%")
  }
}

case class MonetaryAmount(cents: Long) {
  override def toString(): String = {
    "$" + cents / 100 + "." + cents % 100
  }
}

object MonetaryAmount {
  def fromString(amount: String): MonetaryAmount = {
    val tokens = amount.replaceAll("\\$", "").split("\\.")
    if (amount.contains("$")) {
      val dollarAmount = tokens.head.toLong * 100
      val centsAmount = if (tokens.length >= 2)
        tokens(1).toInt
      else
        0

      MonetaryAmount(dollarAmount + centsAmount)
    } else {
      MonetaryAmount(tokens.head.toInt)
    }
  }
}

case class PackingSlip(
  company: String,
  poId: String,
  shipTo: String,
  itemId: String,
  quantity: Int,
  maybeCost: Option[MonetaryAmount],
  maybeShipSpeed: Option[String]
)

object PackingSlip {
  def fromCSVLine(line: String): PackingSlip = {
    val tokens = line.split(",")
    val company = tokens.head
    val poId = tokens(1)
    val shipTo = tokens(2)
    val itemId = tokens(3)
    val quantity = tokens(4).toInt
    val maybeCost = if (tokens.length < 6 || tokens(5).isEmpty())
      None
    else
      Some(MonetaryAmount.fromString(tokens(5).trim()))

    val maybeShipSpeed = if (tokens.length < 7 || tokens(6).isEmpty())
      None
    else
      Some(tokens(6))

    PackingSlip(company, poId, shipTo, itemId, quantity, maybeCost, maybeShipSpeed)
  }
}
