package com.zingkg.deduplicate

import org.scalatest.WordSpec
import org.scalatest.MustMatchers._

class CommonSpec extends WordSpec {
  "Common.accumulateItems" should {
    "generate an empty map with no items" in {
      Common.accumulateItems(Seq.empty) mustBe Map.empty
    }

    "generate a map with a single tuple" in {
      val actual = Common.accumulateItems(
        Seq(
          ("1", 5)
        )
      )
      actual mustBe Map("1" -> 5)
    }

    "de-duplicate entries" in {
      val actual = Common.accumulateItems(
        Seq(
          ("1", 5),
          ("1", 3),
          ("2", 2)
        )
      )
      actual mustBe Map("1" -> 8, "2" -> 2)
    }
  }
}
