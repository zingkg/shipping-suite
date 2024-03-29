package com.zingkg.shippinglabelcreator

object Latex {
  def sanitizeInput(str: String): String =
    str.replace("#", "\\#")
      .replace("$", "\\$")
      .replace("%", "\\%")
      .replace("&", "\\&")
      .replace("�", "'")

  def headerPage: Seq[String] =
    Seq(
      "\\documentclass{article}",
      "\\usepackage{tabularx}",
      "\\usepackage[top=0.1cm, bottom=0.1cm, left=0.2cm, right=0.2cm]{geometry}",
      "\\setlength{\\tabcolsep}{15pt}",
      "\\begin{document}"
    )

  def headerFourBySix: Seq[String] =
    Seq(
      "\\documentclass{article}",
      "\\usepackage{tabularx}",
      "\\usepackage[paperheight=4in,paperwidth=6in,margin=0.25in,heightrounded]{geometry}",
      "\\pdfpagewidth 6in",
      "\\pdfpageheight 4in",
      "\\setlength{\\tabcolsep}{15pt}",
      "\\begin{document}"
    )

  def endDocument: String =
    "\\end{document}"

  def buildLatex(
    shippingLabels: Seq[ShippingLabel]
  ): Seq[String] =
    shippingLabels
      .flatMap { label =>
        shippingLabelStrings(label).flatten
      }

  private def fontSize(string: String, fontSize: Int): String = {
    s"{\\fontsize{$fontSize}{$fontSize} \\selectfont $string}"
  }

  private def shippingLabelStrings(shippingLabel: ShippingLabel): Seq[Seq[String]] = {
    val header = Seq(
      s"{\\fontsize{${shippingLabel.bodyFontSize}}{${shippingLabel.bodyFontSize}}",
      "\\selectfont"
    )

    val body = (1 to shippingLabel.itemBoxes).map { i =>
      val sourceCityStateZip = s"${shippingLabel.sourceCity}, ${shippingLabel.sourceState} ${shippingLabel.sourceZipCode}"
      val destinationCityStateZip = s"${shippingLabel.destinationCity}, ${shippingLabel.destinationState} ${shippingLabel.destinationZipCode}"
      val addressSeq = Seq(
        "\\vbox{",
        "\\begin{center}",
        "\\begin{tabularx} {\\textwidth} {X l}",
        s"${fontSize(shippingLabel.sourceName, shippingLabel.addressFontSize)} & ${fontSize(shippingLabel.destinationName, shippingLabel.addressFontSize)} \\\\",
        s"\\multicolumn{1}{l}{${fontSize(shippingLabel.sourceAddress, shippingLabel.addressFontSize)}} & \\multicolumn{1}{l}{${fontSize(shippingLabel.destinationAddress, shippingLabel.addressFontSize)}} \\\\",
        s"${fontSize(sourceCityStateZip, shippingLabel.addressFontSize)} & ${fontSize(destinationCityStateZip, shippingLabel.addressFontSize)} \\\\",
        "\\vspace{1pc} \\\\",
        s"Item \\#${shippingLabel.itemNumber} & \\multicolumn{1}{c}{${shippingLabel.casePack}} \\\\"
      )
      val itemNotesSeq = shippingLabel.maybeTotalPieces.map { totalPieces =>
        Seq(s"& \\multicolumn{1}{c}{$totalPieces} \\\\")
      }.getOrElse(Seq.empty)
      val destHeader = Seq(
        "\\end{tabularx}",
        "",
        "\\vspace{0.2pc}"
      )
      val poSeq = shippingLabel.maybeDestinationPO.map { po =>
        Seq(s"P/O: $po \\\\")
      }.getOrElse(Seq.empty)
      val deptSeq = shippingLabel.maybeDestinationDept.map { dept =>
        Seq(s"Dept \\#$dept \\\\")
      }.getOrElse(Seq.empty)
      val poOrDeptEnd =
        if (poSeq.nonEmpty || deptSeq.nonEmpty)
          Seq("\\vspace{0.2pc}")
        else
          Seq.empty
      val end = Seq(
        s"{\\fontsize{${shippingLabel.boxFontSize}}{${shippingLabel.boxFontSize}}\\selectfont Box \\hspace{4pc} $i\\hspace{4pc} OF \\hspace{4pc} ${shippingLabel.itemBoxes}}",
        "\\end{center}",
        "\\vspace{1pc}",
        "}",
        ""
      )

      addressSeq ++ itemNotesSeq ++ destHeader ++ poSeq ++ deptSeq ++ poOrDeptEnd ++ end
    }

    Seq(header) ++ body ++ Seq(Seq("}"))
  }
}

enum PageFormat {
  case FourBySix, Full
}

case class ShippingLabel(
  destinationName: String,
  destinationAddress: String,
  destinationCity: String,
  destinationState: String,
  destinationZipCode: String,
  maybeDestinationPO: Option[String],
  maybeDestinationDept: Option[String],
  itemNumber: String,
  casePack: String,
  maybeTotalPieces: Option[String],
  itemBoxes: Int,
  sourceName: String,
  sourceAddress: String,
  sourceCity: String,
  sourceState: String,
  sourceZipCode: String,
  addressFontSize: Int,
  bodyFontSize: Int,
  boxFontSize: Int
) {
  assert(addressFontSize > 0, "Address font size must be positive")
  assert(bodyFontSize > 0, "Body font size must be positive")
  assert(boxFontSize > 0, "Box font size must be positive")
  assert(itemBoxes > 0, "Item boxes must be positive")
}

object ShippingLabel {
  def fromTokens(tokens: Seq[String]): ShippingLabel =
    ShippingLabel(
      destinationName = Latex.sanitizeInput(tokens(0)),
      destinationAddress = Latex.sanitizeInput(tokens(1)),
      destinationCity = Latex.sanitizeInput(tokens(2)),
      destinationState = Latex.sanitizeInput(tokens(3)),
      destinationZipCode = Latex.sanitizeInput(tokens(4)),
      maybeDestinationPO = parseOptional(tokens, 5),
      maybeDestinationDept = parseOptional(tokens, 6),
      itemNumber = Latex.sanitizeInput(tokens(7)),
      casePack = Latex.sanitizeInput(tokens(8)),
      maybeTotalPieces = parseOptional(tokens, 9),
      itemBoxes = Latex.sanitizeInput(tokens(10)).toInt,
      sourceName = Latex.sanitizeInput(tokens(11)),
      sourceAddress = Latex.sanitizeInput(tokens(12)),
      sourceCity = Latex.sanitizeInput(tokens(13)),
      sourceState = Latex.sanitizeInput(tokens(14)),
      sourceZipCode = Latex.sanitizeInput(tokens(15)),
      addressFontSize = Latex.sanitizeInput(tokens(16)).toInt,
      bodyFontSize = Latex.sanitizeInput(tokens(17)).toInt,
      boxFontSize = Latex.sanitizeInput(tokens(18)).toInt
    )

  private def parseOptional(tokens: Seq[String], position: Int): Option[String] =
    if (tokens.length <= position || tokens(position).isEmpty)
      None
    else
      Some(Latex.sanitizeInput(tokens(position)))
}
