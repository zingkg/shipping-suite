package com.zingkg.packingslipcreator

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

  "Latex.header" should {
    "return a latex header" in {
      val expected = Seq(
        "\\documentclass{article}",
        "\\usepackage[letterpaper, landscape, top=0.1cm, bottom=0.1cm, left=0.2cm, " +
          "right=0.2cm]{geometry}",
        "\\begin{document}"
      )
      Latex.header mustBe expected
    }
  }

  "Latex.groupSlips" should {
    val empty = PackingSlip(
      company = "",
      poId = "",
      shipToName = "",
      maybeShipToAddress = None,
      maybeShipToAddress2 = None,
      maybeShipToCity = None,
      maybeShipToState = None,
      maybeShipToZip = None,
      maybeShipToPhone = None,
      itemId = "",
      quantity = 0,
      maybeCost = None,
      maybeShipSpeed = None
    )

    "make a tuple with 1 element with a Seq of 1 element" in {
      Latex.groupSlips(Seq(empty)) mustBe empty -> None
    }

    "make a tuple with 2 elements with a Seq of 2 elements" in {
      Latex.groupSlips(Seq(empty, empty)) mustBe empty -> Some(empty)
    }

    "fail with 3 or more elements" in forAll(Gen.chooseNum(3, 100)) { num =>
      intercept[MatchError](Latex.groupSlips(Seq.fill(num)(empty)))
    }
  }

  "Latex.buildLatex" should {
    "build the latex string correctly" in {
      val actual = Latex.buildLatex(
        Iterator(
          (
            PackingSlip(
              company = "est 1917",
              poId = "1234",
              shipToName = "Bob",
              maybeShipToAddress = Some("addr1"),
              maybeShipToAddress2 = Some("addr2"),
              maybeShipToCity = Some("city"),
              maybeShipToState = Some("denial"),
              maybeShipToZip = Some("99999"),
              maybeShipToPhone = Some("1234567890"),
              itemId = "0987",
              quantity = 1,
              maybeCost = Some(MonetaryAmount(1000)),
              maybeShipSpeed = Some("economy")
            ),
            None
          )
        )
      )
      val expected = Seq(
        "\\vbox{%",
        "\\begin{minipage}{0.45\\textwidth}",
        "{\\Large \\begin{flushright}est 1917\\end{flushright}}",
        "{\\Large \\begin{center}1234\\end{center}}",
        "{\\Large \\begin{center}Bob\\end{center}}",
        "{\\small \\begin{center}addr1 addr2\\end{center}}",
        "{\\small \\begin{center}city, denial 99999\\end{center}}",
        "{\\small \\begin{center}1234567890\\end{center}}",
        "{\\Large \\begin{center}economy\\end{center}}",
        "\\vspace{6em}",
        "\\hspace*{\\fill}%\n\\huge{0987 \\quad 1} \\hfill \\Large{\\$10.00}\n\\hspace*{\\fill}%", 
        "\\end{minipage}%",
        "}",
        "\\vspace{3em}"
      )
      actual mustBe expected
    }
  }

  "Latex.endDocument" should {
    "return an end document string" in {
      Latex.endDocument mustBe "\\end{document}"
    }
  }

  "Latex.latexCenter" should {
    "return the center in latex string" in {
      Latex.latexCenter("123", "456") mustBe s"456 \\begin{center}123\\end{center}}"
    }
  }
}
