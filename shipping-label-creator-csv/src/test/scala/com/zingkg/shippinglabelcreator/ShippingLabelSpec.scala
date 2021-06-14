package com.zingkg.shippinglabelcreator

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ShippingLabelSpec extends AnyWordSpec with Matchers {
  "ShippingLabel.fromTokens" should {
    "be correct from the tokens" in {
      val expected = ShippingLabel(
        fontSize = 22,
        sourceName = "Src name",
        sourceAddress = "Src addr",
        sourceCity = "Src city",
        sourceState = "Src state",
        sourceZipCode = "Src zip",
        destinationName = "Dest name",
        destinationAddress = "Dest addr",
        destinationCity = "Dest city",
        destinationState = "Dest state",
        destinationZipCode = "Dest zip",
        maybeDestinationPO = Some("Dest po"),
        maybeDestinationDept = Some("Dest dept"),
        itemNumber = "Item num",
        itemDetails = "Item det",
        maybeItemNotes = Some("Item notes"),
        itemBoxes = 10
      )
      val actual = ShippingLabel.fromTokens(
        Seq(
          "22",
          "Src name",
          "Src addr",
          "Src city",
          "Src state",
          "Src zip",
          "Dest name",
          "Dest addr",
          "Dest city",
          "Dest state",
          "Dest zip",
          "Dest po",
          "Dest dept",
          "Item num",
          "Item det",
          "Item notes",
          "10"
        )
      )
      actual mustBe expected
    }

    "be correct with nones from the tokens" in {
      val expected = ShippingLabel(
        fontSize = 22,
        sourceName = "Src name",
        sourceAddress = "Src addr",
        sourceCity = "Src city",
        sourceState = "Src state",
        sourceZipCode = "Src zip",
        destinationName = "Dest name",
        destinationAddress = "Dest addr",
        destinationCity = "Dest city",
        destinationState = "Dest state",
        destinationZipCode = "Dest zip",
        maybeDestinationPO = None,
        maybeDestinationDept = None,
        itemNumber = "Item num",
        itemDetails = "Item det",
        maybeItemNotes = None,
        itemBoxes = 10
      )
      val actual = ShippingLabel.fromTokens(
        Seq(
          "22",
          "Src name",
          "Src addr",
          "Src city",
          "Src state",
          "Src zip",
          "Dest name",
          "Dest addr",
          "Dest city",
          "Dest state",
          "Dest zip",
          "",
          "",
          "Item num",
          "Item det",
          "",
          "10"
        )
      )
      actual mustBe expected
    }

    "throw an exception if the tokens are not present" in {
      intercept[Exception](ShippingLabel.fromTokens(Seq.empty))
    }

    "throw an exception if font size is negative" in {
      intercept[AssertionError](
        ShippingLabel.fromTokens(
          Seq(
            "-11",
            "Src name",
            "Src addr",
            "Src city",
            "Src state",
            "Src zip",
            "Dest name",
            "Dest addr",
            "Dest city",
            "Dest state",
            "Dest zip",
            "Dest po",
            "Dest dept",
            "Item num",
            "Item det",
            "Item notes",
            "10"
          )
        )
      )
    }

    "throw an exception if item boxes are negative" in {
      intercept[AssertionError](
        ShippingLabel.fromTokens(
          Seq(
            "22",
            "Src name",
            "Src addr",
            "Src city",
            "Src state",
            "Src zip",
            "Dest name",
            "Dest addr",
            "Dest city",
            "Dest state",
            "Dest zip",
            "Dest po",
            "Dest dept",
            "Item num",
            "Item det",
            "Item notes",
            "-1"
          )
        )
      )
    }
  }
}
