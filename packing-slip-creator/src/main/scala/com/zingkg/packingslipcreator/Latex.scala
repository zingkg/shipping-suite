package com.zingkg.packingslipcreator;

object Latex {
  def header: Seq[String] =
    Seq(
      "\\documentclass{article}",
      "\\usepackage[letterpaper, landscape, top=0.1cm, bottom=0.1cm, left=0.2cm, " +
        "right=0.2cm]{geometry}",
      "\\begin{document}"
    )

  def generateLatex(
    packingSlipPairs: Iterator[(PackingSlip, Option[PackingSlip])]
  ): Seq[String] =
    packingSlipPairs.flatMap {
      case (left, maybeRight) =>
        val leftString = packingSlipStrings(left)
        val rightStrings = maybeRight.map { right =>
          Seq(
            Seq("\\hfill"),
            packingSlipStrings(right)
          ).flatten
        }.getOrElse(Seq.empty)
        Seq("\\vbox{%") ++ leftString ++ rightStrings ++ Seq("}", "\\vspace{5em}")
    }.toList

  def endDocument: String =
    "\\end{document}"

  private def packingSlipStrings(packingSlip: PackingSlip): Seq[String] = {
    val size = "{\\Large"
    val header = Seq(
      "\\begin{minipage}{0.45\\textwidth}",
      s"$size \\begin{flushright}${packingSlip.company}\\end{flushright}}",
      latexCenter(packingSlip.poId, size),
      latexCenter(packingSlip.shipToName, size)
    )
    val shipSpeed = packingSlip.maybeShipSpeed.map(latexCenter(_, size)).toSeq :+ "\\vspace{10em}"

    val baseItemRow = s"$size ${packingSlip.itemId} \\hfill ${packingSlip.quantity}"
    val itemRow = packingSlip.maybeCost.map { cost =>
      baseItemRow + s" \\hfill \\$cost}"
    }.getOrElse(baseItemRow + "}")

    header ++ shippingAddress(packingSlip) ++ shipSpeed ++ Seq(itemRow, "\\end{minipage}%")
  }

  private def latexCenter(string: String, size: String): String =
    s"$size \\begin{center}$string\\end{center}}"

  private def shippingAddress(packingSlip: PackingSlip): Seq[String] = {
    val size = "{\\small"
    Seq(
      packingSlip.maybeShipToAddress.map { address =>
        latexCenter(s"$address ${packingSlip.maybeShipToAddress2.getOrElse("")}", size)
      },
      packingSlip.maybeShipToCity.map { city =>
        latexCenter(
          s"$city, ${packingSlip.maybeShipToState.getOrElse("")} ${packingSlip.maybeShipToZip.getOrElse("")}",
          size
        )
      },
      packingSlip.maybeShipToPhone.map(latexCenter(_, size))
    ).flatten
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
  shipToName: String,
  maybeShipToAddress: Option[String],
  maybeShipToAddress2: Option[String],
  maybeShipToCity: Option[String],
  maybeShipToState: Option[String],
  maybeShipToZip: Option[String],
  maybeShipToPhone: Option[String],
  itemId: String,
  quantity: Int,
  maybeCost: Option[MonetaryAmount],
  maybeShipSpeed: Option[String]
)

object PackingSlip {
  def fromTokens(tokens: Seq[String]): PackingSlip = {
    val company = tokens.head
    val poId = tokens(1)
    val shipToName = tokens(2)
    val maybeShipToAddress = parseOptional(tokens, position = 3)
    val maybeShipToAddress2 = parseOptional(tokens, position = 4)
    val maybeShipToCity = parseOptional(tokens, position = 5)
    val maybeShipToState = parseOptional(tokens, position = 6)
    val maybeShipToZip = parseOptional(tokens, position = 7)
    val maybeShipToPhone = parseOptional(tokens, position = 8)
    val itemId = tokens(9)
    val quantity = tokens(10).toInt
    val maybeCost = parseOptional(tokens, position = 11).map { cost =>
      MonetaryAmount.fromString(cost.trim)
    }
    val maybeShipSpeed = parseOptional(tokens, position = 12)

    PackingSlip(
      company,
      poId,
      shipToName,
      maybeShipToAddress,
      maybeShipToAddress2,
      maybeShipToCity,
      maybeShipToState,
      maybeShipToZip,
      maybeShipToPhone,
      itemId,
      quantity,
      maybeCost,
      maybeShipSpeed
    )
  }

  private def parseOptional(tokens: Seq[String], position: Int): Option[String] =
    if (tokens.length <= position || tokens(position).isEmpty)
      None
    else
      Some(tokens(position))
}
