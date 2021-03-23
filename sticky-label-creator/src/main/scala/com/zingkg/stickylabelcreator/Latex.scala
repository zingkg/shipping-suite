package com.zingkg.stickylabelcreator

object Latex {
  private[stickylabelcreator] val fontSizes: Seq[String] =
    Vector(
      "\\tiny",
      "\\scriptsize",
      "\\footnotesize",
      "\\small",
      "\\normalsize",
      "\\large",
      "\\Large",
      "\\LARGE",
      "\\huge",
      "\\Huge"
    )

  def sanitizeInput(str: String): String =
    str.replace("#", "\\#")
      .replace("$", "\\$")
      .replace("%", "\\%")
      .replace("&", "\\&")

  def header: Seq[String] =
    Seq(
      "\\documentclass[12pt]{article}",
      "\\usepackage[paperheight=4in,paperwidth=6in,margin=.25in,heightrounded]{geometry}",
      "\\pagenumbering{gobble}",
      "\\setlength{\\parindent}{0pt}",
      "\\setlength{\\parskip}{.5em}",
      "\\pdfpagewidth 6in",
      "\\pdfpageheight 4in",
      "\\begin{document}"
    )

  def buildLatex(
    stickyLabels: Seq[StickyLabel]
  ): Seq[String] =
    stickyLabels
      .flatMap { label =>
        stickyLabelStrings(label).flatten
      }

  def endDocument: String =
    "\\end{document}"

  private def stickyLabelStrings(stickyLabel: StickyLabel): Seq[Seq[String]] = {
    (0 until stickyLabel.cartons).map { _ =>
      val headerSize = fontSizes.length - 1
      val bodySize = headerSize - 1
      Seq(
        s"{${fontSizes(headerSize)} \\begin{center}\\textbf{${stickyLabel.centerHeader}}\\end{center}",
        "",
        s"{${fontSizes(bodySize)}",
        s"Recipient: ${stickyLabel.recipient}",
        "",
        s"PO: ${stickyLabel.po}",
        "",
        "Carton:",
        "",
        "Date:",
        "",
        "From: Cheungs}",
        "\\newpage"
      )
    }
  }
}

case class StickyLabel(
  recipient: String,
  po: String,
  cartons: Int,
  centerHeader: String
) {
  assert(cartons > 0, "cartons must be positive")
}

object StickyLabel {
  def fromTokens(tokens: Seq[String]): StickyLabel = {
    StickyLabel(
      recipient = Latex.sanitizeInput(tokens.head),
      po = Latex.sanitizeInput(tokens(1)),
      cartons = tokens(2).toInt,
      centerHeader = tokens(3)
    )
  }
}
