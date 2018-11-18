package com.zingkg.packingslipcreator

import org.scalacheck.Gen
import org.scalatest.{ MustMatchers, PrivateMethodTester, WordSpec }
import org.scalatest.prop.PropertyChecks

class LatexSpec extends WordSpec with MustMatchers with PrivateMethodTester with PropertyChecks {
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

  "Latex.generateLatex" should {
  }

  "Latex.endDocument" should {
    "return an end document string" in {
      Latex.endDocument mustBe "\\end{document}"
    }
  }

  "Latex.packingSlipString" should {
    def packingSlipStrings(
      packingSlipKey: PackingSlipKey,
      packingSlips: Seq[PackingSlip],
    ): Seq[String] =
      Latex.invokePrivate(PrivateMethod[Seq[String]](Symbol("packingSlipStrings"))(packingSlipKey, packingSlips))
  }

  "Latex.packingSlipItem" should {
    def packingSlipItem(packingSlip: PackingSlip): String =
      Latex.invokePrivate(PrivateMethod[String](Symbol("packingSlipItem"))(packingSlip))
  }

  "Latex.latexCenter" should {
    def latexCenter(string: String, size: String): String =
      Latex.invokePrivate(PrivateMethod[String](Symbol("latexCenter"))(string, size))

    "return the center in latex string" in {
      latexCenter("123", "456") mustBe s"456 \\begin{center}123\\end{center}}"
    }
  }

  "Latex.shippingAddress" should {
    def shippingAddress(packingSlipKey: PackingSlipKey): Seq[String] =
      Latex.invokePrivate(PrivateMethod[Seq[String]](Symbol("packingSlipItem"))(packingSlipKey))
  }
}
