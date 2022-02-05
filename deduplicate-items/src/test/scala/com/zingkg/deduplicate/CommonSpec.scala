package com.zingkg.deduplicate

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

class CommonSpec extends AnyWordSpec with Matchers {
  "Common.accumulateItems" should {
    "generate an empty map with no items" in {
      Common.accumulateItems(Seq.empty) mustBe Map.empty
    }

    "generate a map with a single tuple" in {
      val actual = Common.accumulateItems(
        Seq(
          Row("1", 5, "9.5")
        )
      )
      actual mustBe Map(("1", "9.5") -> Row("1", 5, "9.5"))
    }

    "de-duplicate entries" in {
      val actual = Common.accumulateItems(
        Seq(
          Row("16S004", 1, "10"),
          Row("16S004", 2, "10"),
          Row("16S004", 1, "7.5")
        )
      )
      actual mustBe Map(
        ("16S004", "10") -> Row("16S004", 3, "10"),
        ("16S004", "7.5") -> Row("16S004", 1, "7.5"))
    }
  }
}
