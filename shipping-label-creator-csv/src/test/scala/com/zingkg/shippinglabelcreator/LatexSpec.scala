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
    }

    "build the latex string with multiple labels" in {
    }
  }

  "Latex.endDocument" should {
    "return an end document string" in {
      Latex.endDocument mustBe "\\end{document}"
    }
  }
}
