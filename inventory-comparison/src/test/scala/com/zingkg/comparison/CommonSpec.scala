package com.zingkg.comparison

import org.scalatest.WordSpec
import org.scalatest.MustMatchers._

class CommonSpec extends WordSpec {
  "Common.Item.extraColumns" should {
    "return a sequence of extra columns if at least one of them are filled in" in {
      val actual = Common.Item(itemId = "a", quantity = 2, maybeColumn1 = Some("a")).extraColumns
      val expected = "a" +: (0 until 9).map(_ => "")
      actual mustBe expected
    }
  }

  "Common.accumulateItems" should {
    "build two inventories" in {
      val actual = Common.accumulateItems(
        Seq(
          Common.Line(
            maybeItem1 = Some(Common.Item("i1", 1)),
            maybeItem2 = Some(Common.Item("i1", 2))
          ),
          Common.Line(maybeItem1 = Some(Common.Item("i2", 3)), maybeItem2 = None)
        )
      )
      val expected = (
        Map("i1" -> Common.Item("i1", 1), "i2" -> Common.Item("i2", 3)),
        Map("i1" -> Common.Item("i1", 2))
      )
      actual mustBe expected
    }
  }

  "Common.processLine" should {
    "return a line normally" in {
      val actual = Common.processLine(Seq("i1", "2", "i3", "1"))
      val expected = Common.Line(
        maybeItem1 = Some(Common.Item("i1", 2)),
        maybeItem2 = Some(Common.Item("i3", 1))
      )
      actual mustBe expected
    }

    "return a line with a missing first item" in {
      val actual = Common.processLine(Seq("", "", "i5", "1"))
      actual mustBe Common.Line(maybeItem1 = None, maybeItem2 = Some(Common.Item("i5", 1)))
    }

    "return a line with extra columns" in {
      val actual = Common.processLine(Seq("i1", "2", "i3", "1", "extra1", "extra2", "extra3"))
      val expected = Common.Line(
        maybeItem1 = Some(Common.Item("i1", 2)),
        maybeItem2 = Some(
          Common.Item(
            "i3",
            1,
            maybeColumn1 = Some("extra1"),
            maybeColumn2 = Some("extra2"),
            maybeColumn3 = Some("extra3")
          )
        )
      )
      actual mustBe expected
    }
  }

  "Common.assembleMatchingLines" should {
    "assemble the correct lines" in {
      val actual = Common.assembleMatchingLines(
        inventory1 = Map("i1" -> Common.Item("i1", 2), "i3" -> Common.Item("i3", 3)),
        inventory2 = Map("i2" -> Common.Item("i2", 3), "i3" -> Common.Item("i3", 4))
      )
      val expected = Seq(
        Common.Line(maybeItem1 = Some(Common.Item("i1", 2)), maybeItem2 = None),
        Common.Line(
          maybeItem1 = Some(Common.Item("i3", 3)),
          maybeItem2 = Some(Common.Item("i3", 4))),
        Common.Line(maybeItem1 = None, maybeItem2 = Some(Common.Item("i2", 3)))
      )
      actual mustBe expected
    }
  }
}
