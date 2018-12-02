package com.zingkg.packingslipcreator

import org.scalacheck.Gen
import org.scalatest.{ MustMatchers, WordSpec }
import org.scalatest.prop.PropertyChecks

class LatexSpec extends WordSpec with MustMatchers with PropertyChecks {
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
  }

  "Latex.packingSlipItem" should {
  }

  "Latex.latexCenter" should {
    "return the center in latex string" in {
      Latex.latexCenter("123", "456") mustBe s"456 \\begin{center}123\\end{center}}"
    }
  }

  "Latex.shippingAddress" should {
  }
}
