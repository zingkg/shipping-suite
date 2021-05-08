package com.zingkg.stickylabelcreator

import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StickyLabelSpec extends AnyWordSpec with Matchers {
  "StickyLabel.fromTokens" should {
    "be correct from the tokens" in {
      val expected = StickyLabel(
        po = "1234",
        recipient = "asdf",
        cartons = 3,
        centerHeader = "faire"
      )
      StickyLabel.fromTokens(Seq("1234", "asdf", "3", "faire")) mustBe expected
    }

    "throw an exception if the tokens are not present" in {
      intercept[Exception](StickyLabel.fromTokens(Seq.empty))
    }

    "throw an exception if cartons are negative" in {
      intercept[AssertionError](StickyLabel.fromTokens(Seq("asdf", "1234", "-2", "faire")))
    }
  }
}
