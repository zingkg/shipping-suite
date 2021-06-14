package com.zingkg.shippinglabelcreator

object Latex {
  def sanitizeInput(str: String): String =
    str.replace("#", "\\#")
      .replace("$", "\\$")
      .replace("%", "\\%")
      .replace("&", "\\&")

  def header: Seq[String] =
    Seq(
      "\\documentclass{article}",
      "\\usepackage{tabularx}",
      "\\usepackage[top=0.1cm, bottom=0.1cm, left=0.2cm, right=0.2cm]{geometry}",
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

  private def shippingLabelStrings(shippingLabel: ShippingLabel): Seq[Seq[String]] = {
    val header = Seq(
      s"{\\fontsize{${shippingLabel.fontSize}}{${shippingLabel.fontSize / 4}}",
      "\\selectfont"
    )

    val body = (1 to shippingLabel.itemBoxes).map { i =>
      val addressSeq = Seq(
        "\\vbox{",
        "\\begin{center}",
        "\\begin{tabularx} {\\textwidth} {X l}",
        s"${shippingLabel.sourceName} & ${shippingLabel.destinationName} \\\\",
        s"\\multicolumn{1}{l}{${shippingLabel.sourceAddress}} & \\multicolumn{1}{l}{${shippingLabel.destinationAddress}} \\\\",
        s"${shippingLabel.sourceCity}, ${shippingLabel.sourceState} ${shippingLabel.sourceZipCode} & ${shippingLabel.destinationCity}, ${shippingLabel.destinationState} ${shippingLabel.destinationZipCode} \\\\",
        s"Item \\#${shippingLabel.itemNumber} & ${shippingLabel.itemDetails} \\\\"
      )
      val itemNotesSeq = shippingLabel.maybeItemNotes.map { itemNotes =>
        Seq(s"& $itemNotes \\\\")
      }.getOrElse(Seq.empty)
      val destHeader = Seq(
        "\\end{tabularx}",
        "",
        "\\vspace{0.5pc}",
      )
      val poSeq = shippingLabel.maybeDestinationPO.map { po =>
        Seq(s"P/O: $po \\\\")
      }.getOrElse(Seq.empty)
      val deptSeq = shippingLabel.maybeDestinationDept.map { dept =>
        Seq(s"Dept \\#$dept \\\\")
      }.getOrElse(Seq.empty)
      val poOrDeptEnd =
        if (poSeq.nonEmpty || deptSeq.nonEmpty)
          Seq("\\vspace{5pc}")
        else
          Seq.empty
      val end = Seq(
        s"Box \\hspace{4pc} $i\\hspace{4pc} OF \\hspace{4pc} ${shippingLabel.itemBoxes}",
        "\\noindent\\makebox[\\linewidth]{\\rule{\\paperwidth}{0.4pt}}",
        "\\end{center}",
        "}",
        ""
      )

      addressSeq ++ itemNotesSeq ++ destHeader ++ poSeq ++ deptSeq ++ poOrDeptEnd ++ end
    }

    Seq(header) ++ body ++ Seq(Seq("}"))
  }
}

case class ShippingLabel(
  fontSize: Int,
  sourceName: String,
  sourceAddress: String,
  sourceCity: String,
  sourceState: String,
  sourceZipCode: String,
  destinationName: String,
  destinationAddress: String,
  destinationCity: String,
  destinationState: String,
  destinationZipCode: String,
  maybeDestinationPO: Option[String],
  maybeDestinationDept: Option[String],
  itemNumber: String,
  itemDetails: String,
  maybeItemNotes: Option[String],
  itemBoxes: Int
) {
  assert(fontSize > 0, "Font size must be positive")
  assert(itemBoxes > 0, "Item boxes must be positive")
}

object ShippingLabel {
  def fromTokens(tokens: Seq[String]): ShippingLabel =
    ShippingLabel(
      fontSize = Latex.sanitizeInput(tokens(0)).toInt,
      sourceName = Latex.sanitizeInput(tokens(1)),
      sourceAddress = Latex.sanitizeInput(tokens(2)),
      sourceCity = Latex.sanitizeInput(tokens(3)),
      sourceState = Latex.sanitizeInput(tokens(4)),
      sourceZipCode = Latex.sanitizeInput(tokens(5)),
      destinationName = Latex.sanitizeInput(tokens(6)),
      destinationAddress = Latex.sanitizeInput(tokens(7)),
      destinationCity = Latex.sanitizeInput(tokens(8)),
      destinationState = Latex.sanitizeInput(tokens(9)),
      destinationZipCode = Latex.sanitizeInput(tokens(10)),
      maybeDestinationPO = parseOptional(tokens, 11),
      maybeDestinationDept = parseOptional(tokens, 12),
      itemNumber = Latex.sanitizeInput(tokens(13)),
      itemDetails = Latex.sanitizeInput(tokens(14)),
      maybeItemNotes = parseOptional(tokens, 15),
      itemBoxes = Latex.sanitizeInput(tokens(16)).toInt
    )

  private def parseOptional(tokens: Seq[String], position: Int): Option[String] =
    if (tokens.length <= position || tokens(position).isEmpty)
      None
    else
      Some(Latex.sanitizeInput(tokens(position)))
}
