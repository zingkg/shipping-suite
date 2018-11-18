package com.zingkg.packingslipcreator;

object Latex {
  def sanitizeInput(str: String): String =
    str.replace("#", "\\#")
      .replace("$", "\\$")
      .replace("%", "\\%")
      .replace("&", "\\&")

  def header: Seq[String] =
    Seq(
      "\\documentclass{article}",
      "\\usepackage[letterpaper, landscape, top=0.1cm, bottom=0.1cm, left=0.2cm, " +
        "right=0.2cm]{geometry}",
      "\\begin{document}"
    )

  def generateLatex(
    packingSlipPairs: Iterator[((PackingSlipKey, Seq[PackingSlip]), (Option[(PackingSlipKey, Seq[PackingSlip])]))]
  ): Seq[String] =
    packingSlipPairs.flatMap {
      case ((leftKey, leftSlips), maybeRight) =>
        val leftString = packingSlipStrings(leftKey, leftSlips)
        val rightStrings = maybeRight.map {
          case (rightKey, rightSlips) =>
            Seq(
              Seq("\\hfill"),
              packingSlipStrings(rightKey, rightSlips)
            ).flatten
        }.getOrElse(Seq.empty)
        Seq("\\vbox{%") ++ leftString ++ rightStrings ++ Seq("}", "\\vspace{3em}")
    }.toList

  def endDocument: String =
    "\\end{document}"

  private def packingSlipStrings(
    packingSlipKey: PackingSlipKey,
    packingSlips: Seq[PackingSlip],
  ): Seq[String] = {
    val size = "{\\Large"
    val header = Seq(
      "\\begin{minipage}{0.45\\textwidth}",
      s"$size \\begin{flushright}${packingSlipKey.company}\\end{flushright}}",
      latexCenter(packingSlipKey.poId, size),
      latexCenter(packingSlipKey.shipToName, size)
    )
    val emSpaceBetweenSlips =
      if (packingSlips.size > 1)
        "\\vspace{2em}"
      else
        "\\vspace{8em}"
    val shipSpeed = packingSlipKey.maybeShipSpeed.map(latexCenter(_, size)).toSeq :+ emSpaceBetweenSlips

    val packingSlipItems = packingSlips.map(packingSlipItem(_)).mkString("\\\\")

    header ++ shippingAddress(packingSlipKey) ++ shipSpeed ++ Seq(packingSlipItems, "\\end{minipage}%")
  }

  private def packingSlipItem(packingSlip: PackingSlip): String = {
    val size = "{\\Large"
    val baseItemRow = s"$size ${packingSlip.itemId} \\hfill ${packingSlip.quantity}"
    val itemRow = packingSlip.maybeCost.map { cost =>
      baseItemRow + s" \\hfill \\$cost}"
    }.getOrElse(baseItemRow + "}")
    itemRow
  }
  
  private def latexCenter(string: String, size: String): String =
    s"$size \\begin{center}$string\\end{center}}"

  private def shippingAddress(packingSlipKey: PackingSlipKey): Seq[String] = {
    val size = "{\\small"
    Seq(
      packingSlipKey.maybeShipToAddress.map { address =>
        latexCenter(s"$address ${packingSlipKey.maybeShipToAddress2.getOrElse("")}", size)
      },
      packingSlipKey.maybeShipToCity.map { city =>
        latexCenter(
          s"$city, ${packingSlipKey.maybeShipToState.getOrElse("")} ${packingSlipKey.maybeShipToZip.getOrElse("")}",
          size
        )
      },
      packingSlipKey.maybeShipToPhone.map(latexCenter(_, size))
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

case class PackingSlipKey(
  company: String,
  poId: String,
  shipToName: String,
  maybeShipToAddress: Option[String],
  maybeShipToAddress2: Option[String],
  maybeShipToCity: Option[String],
  maybeShipToState: Option[String],
  maybeShipToZip: Option[String],
  maybeShipToPhone: Option[String],
  maybeShipSpeed: Option[String]
)

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
) {
  def key: PackingSlipKey =
    PackingSlipKey(
      company = company,
      poId = poId,
      shipToName = shipToName,
      maybeShipToAddress = maybeShipToAddress,
      maybeShipToAddress2 = maybeShipToAddress2,
      maybeShipToCity = maybeShipToCity,
      maybeShipToState = maybeShipToState,
      maybeShipToZip = maybeShipToZip,
      maybeShipToPhone = maybeShipToPhone,
      maybeShipSpeed = maybeShipSpeed
    )
}

object PackingSlip {
  def fromTokens(tokens: Seq[String]): PackingSlip = {
    val company = Latex.sanitizeInput(tokens.head)
    val poId = Latex.sanitizeInput(tokens(1))
    val shipToName = Latex.sanitizeInput(tokens(2))
    val maybeShipToAddress = parseOptional(tokens, position = 3).map(Latex.sanitizeInput)
    val maybeShipToAddress2 = parseOptional(tokens, position = 4).map(Latex.sanitizeInput)
    val maybeShipToCity = parseOptional(tokens, position = 5).map(Latex.sanitizeInput)
    val maybeShipToState = parseOptional(tokens, position = 6).map(Latex.sanitizeInput)
    val maybeShipToZip = parseOptional(tokens, position = 7).map(Latex.sanitizeInput)
    val maybeShipToPhone = parseOptional(tokens, position = 8).map(Latex.sanitizeInput)
    val itemId = Latex.sanitizeInput(tokens(9))
    val quantity = tokens(10).toInt
    val maybeCost = parseOptional(tokens, position = 11).map { cost =>
      MonetaryAmount.fromString(cost.trim)
    }
    val maybeShipSpeed = parseOptional(tokens, position = 12).map(Latex.sanitizeInput)

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
