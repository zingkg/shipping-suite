package com.zingkg.shippinglabelcreator

import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class LatexSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {
  "Latex.sanitizeInput" should {
    "replace the '#' character with '\\#'" in {
      Latex.sanitizeInput("#") mustBe "\\#"
    }

    "replace the '$' character with '\\$'" in {
      Latex.sanitizeInput("$") mustBe "\\$"
    }

    "replace the '%' character with '\\%'" in {
      Latex.sanitizeInput("%") mustBe "\\%"
    }

    "replace the '&' character with '\\&'" in {
      Latex.sanitizeInput("&") mustBe "\\&"
    }

    "return the same string if there are no special characters" in forAll(Gen.alphaStr) { str =>
      Latex.sanitizeInput(str) mustBe str
    }
  }

  "Latex.buildLatex" should {
    "build the latex string correctly" in {
      val actual = Latex.buildLatex(
        Seq(
          ShippingLabel(
            destinationName = "dest",
            destinationAddress = "dest addr",
            destinationCity = "dest city",
            destinationState = "dest state",
            destinationZipCode = "dest zip",
            maybeDestinationPO = Some("PO 1"),
            maybeDestinationDept = Some("Dept 1"),
            itemNumber = "A12345",
            casePack = "case 1",
            maybeTotalPieces = Some("12 pcs"),
            itemBoxes = 1,
            sourceName = "src",
            sourceAddress = "src addr",
            sourceCity = "src city",
            sourceState = "src state",
            sourceZipCode = "src zip",
            fontSize = 18
          )
        )
      )
      val expected = Seq(
        "{\\fontsize{18}{18}",
        "\\selectfont",
        "\\vbox{",
        "\\begin{center}",
        "\\begin{tabularx} {\\textwidth} {X l}",
        "src & dest \\\\",
        "\\multicolumn{1}{l}{src addr} & \\multicolumn{1}{l}{dest addr} \\\\",
        "src city, src state src zip & dest city, dest state dest zip \\\\",
        "\\vspace{1pc} \\\\",
        "Item \\#A12345 & \\multicolumn{1}{c}{case 1} \\\\",
        "& \\multicolumn{1}{c}{12 pcs} \\\\",
        "\\end{tabularx}",
        "",
        "\\vspace{0.2pc}",
        "P/O: PO 1 \\\\",
        "Dept \\#Dept 1 \\\\",
        "\\vspace{0.2pc}",
        "{\\fontsize{26}{26}\\selectfont Box \\hspace{4pc} 1\\hspace{4pc} OF \\hspace{4pc} 1}",
        "\\end{center}",
        "\\vspace{1pc}",
        "}",
        "",
        "}"
      )
      actual mustBe expected
    }

    "build the latex string with multiple labels" in {
      val actual = Latex.buildLatex(
        Seq(
          ShippingLabel(
            destinationName = "dest",
            destinationAddress = "dest addr",
            destinationCity = "dest city",
            destinationState = "dest state",
            destinationZipCode = "dest zip",
            maybeDestinationPO = Some("PO 1"),
            maybeDestinationDept = Some("Dept 1"),
            itemNumber = "A12345",
            casePack = "case 1",
            maybeTotalPieces = Some("12 pcs"),
            itemBoxes = 2,
            sourceName = "src",
            sourceAddress = "src addr",
            sourceCity = "src city",
            sourceState = "src state",
            sourceZipCode = "src zip",
            fontSize = 18
          )
        )
      )
      val expected = Seq(
        "{\\fontsize{18}{18}",
        "\\selectfont",
        "\\vbox{",
        "\\begin{center}",
        "\\begin{tabularx} {\\textwidth} {X l}",
        "src & dest \\\\",
        "\\multicolumn{1}{l}{src addr} & \\multicolumn{1}{l}{dest addr} \\\\",
        "src city, src state src zip & dest city, dest state dest zip \\\\",
        "\\vspace{1pc} \\\\",
        "Item \\#A12345 & \\multicolumn{1}{c}{case 1} \\\\",
        "& \\multicolumn{1}{c}{12 pcs} \\\\",
        "\\end{tabularx}",
        "",
        "\\vspace{0.2pc}",
        "P/O: PO 1 \\\\",
        "Dept \\#Dept 1 \\\\",
        "\\vspace{0.2pc}",
        "{\\fontsize{26}{26}\\selectfont Box \\hspace{4pc} 1\\hspace{4pc} OF \\hspace{4pc} 2}",
        "\\end{center}",
        "\\vspace{1pc}",
        "}",
        "",
        "\\vbox{",
        "\\begin{center}",
        "\\begin{tabularx} {\\textwidth} {X l}",
        "src & dest \\\\",
        "\\multicolumn{1}{l}{src addr} & \\multicolumn{1}{l}{dest addr} \\\\",
        "src city, src state src zip & dest city, dest state dest zip \\\\",
        "\\vspace{1pc} \\\\",
        "Item \\#A12345 & \\multicolumn{1}{c}{case 1} \\\\",
        "& \\multicolumn{1}{c}{12 pcs} \\\\",
        "\\end{tabularx}",
        "",
        "\\vspace{0.2pc}",
        "P/O: PO 1 \\\\",
        "Dept \\#Dept 1 \\\\",
        "\\vspace{0.2pc}",
        "{\\fontsize{26}{26}\\selectfont Box \\hspace{4pc} 2\\hspace{4pc} OF \\hspace{4pc} 2}",
        "\\end{center}",
        "\\vspace{1pc}",
        "}",
        "",
        "}"
      )
      actual mustBe expected
    }
  }

  "Latex.endDocument" should {
    "return an end document string" in {
      Latex.endDocument mustBe "\\end{document}"
    }
  }
}
