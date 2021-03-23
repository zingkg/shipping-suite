package com.zingkg.stickylabelcreator

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
    val headerFont = Latex.fontSizes.last
    val bodyFont = Latex.fontSizes.dropRight(1).last
    "build the latex string correctly" in {
      val actual = Latex.buildLatex(
        Seq(
          StickyLabel(
            recipient = "ASDF",
            po = "1234",
            cartons = 1,
            centerHeader = "faire"
          )
        )
      )
      val expected = Seq(
        Seq(
          s"{$headerFont \\begin{center}\\textbf{faire}\\end{center}",
          "",
          s"{$bodyFont",
          s"Recipient: ASDF",
          "",
          s"PO: 1234",
          "",
          "Carton:",
          "",
          "Date:",
          "",
          "From: Cheungs}",
          "\\newpage"
        )
      ).flatten
      actual mustBe expected
    }

    "build the latex string with multiple labels" in {
      val actual = Latex.buildLatex(
        Seq(
          StickyLabel(
            recipient = "ASDF",
            po = "1234",
            cartons = 1,
            "faire"
          ),
          StickyLabel(
            recipient = "FDSA",
            po = "0987",
            cartons = 2,
            "vaire"
          )
        )
      )
      val expected = Seq(
        Seq(
          s"{$headerFont \\begin{center}\\textbf{faire}\\end{center}",
          "",
          s"{$bodyFont",
          s"Recipient: ASDF",
          "",
          s"PO: 1234",
          "",
          "Carton:",
          "",
          "Date:",
          "",
          "From: Cheungs}",
          "\\newpage"
        ),
        Seq(
          s"{$headerFont \\begin{center}\\textbf{vaire}\\end{center}",
          "",
          s"{$bodyFont",
          s"Recipient: FDSA",
          "",
          s"PO: 0987",
          "",
          "Carton:",
          "",
          "Date:",
          "",
          "From: Cheungs}",
          "\\newpage"
        ),
        Seq(
          s"{$headerFont \\begin{center}\\textbf{vaire}\\end{center}",
          "",
          s"{$bodyFont",
          s"Recipient: FDSA",
          "",
          s"PO: 0987",
          "",
          "Carton:",
          "",
          "Date:",
          "",
          "From: Cheungs}",
          "\\newpage"
        )
      ).flatten
      actual mustBe expected
    }
  }

  "Latex.endDocument" should {
    "return an end document string" in {
      Latex.endDocument mustBe "\\end{document}"
    }
  }
}
