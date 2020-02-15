package com.zingkg.packingslipcreator

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

  def groupSlips(slips: Seq[PackingSlip]): (PackingSlip, Option[PackingSlip]) =
    slips match {
      case Seq(head) =>
        head -> None

      case Seq(head, last) =>
        head -> Some(last)
    }

  def buildLatex(
    packingSlipPairs: Iterator[(PackingSlip, Option[PackingSlip])]
  ): Seq[String] =
    packingSlipPairs
      .map {
        case (leftSlip, maybeRight) =>
          val leftString = packingSlipStrings(leftSlip)
          val rightString = maybeRight.map { rightSlip =>
            Seq(
              Seq("\\hfill"),
              packingSlipStrings(rightSlip)
            ).flatten
          }.getOrElse(Seq.empty)
          Seq("\\vbox{%") ++ leftString ++ rightString ++ Seq("}", "\\vspace{3em}")
      }
      .grouped(2)
      .flatMap {
        case Seq(head) =>
          head

        case group =>
          Seq("\\vspace*{\\fill}") ++
            group.flatten ++
            Seq("\\vspace*{\\fill}", "\\newpage")
      }
      .toList

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
    val shipSpeed = packingSlip.maybeShipSpeed.map(latexCenter(_, size)).toSeq :+ "\\vspace{6em}"

    header ++
      shippingAddress(packingSlip) ++
      shipSpeed ++
      Seq(packingSlipItem(packingSlip), "\\end{minipage}%")
  }

  private def packingSlipItem(packingSlip: PackingSlip): String = {
    val size = "\\huge"
    val baseItemRow = s"$size{${packingSlip.itemId} \\quad ${packingSlip.quantity}}"
    val itemRow = packingSlip.maybeCost.map { cost =>
      baseItemRow + s" \\hfill \\Large{\\$cost}"
    }.getOrElse(baseItemRow)
    s"""\\hspace*{\\fill}%
      |$itemRow
      |\\hspace*{\\fill}%""".stripMargin
  }

  private[packingslipcreator] def latexCenter(string: String, size: String): String =
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
    f"$$${cents / 100}%2d.${cents % 100}%02d"
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

  private[packingslipcreator] def parseOptional(tokens: Seq[String], position: Int): Option[String] =
    if (tokens.length <= position || tokens(position).isEmpty)
      None
    else
      Some(tokens(position))
}
