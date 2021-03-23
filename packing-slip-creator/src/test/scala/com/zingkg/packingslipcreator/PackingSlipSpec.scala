package com.zingkg.packingslipcreator

import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PackingSlipSpec
  extends AnyWordSpec
  with Matchers
  with ScalaCheckPropertyChecks {
  "PackingSlip.fromTokens" should {
    "be a correct packing slip" in {
      val company = "asdf"
      val poId = "fdsa"
      val shipToName = "Sp"
      val shipToAddress = "addr 1"
      val shipToAddress2 = ""
      val shipToCity = "fake town"
      val shipToState = "WA"
      val shipToZip = "11111"
      val shipToPhone = "11122233333"
      val itemId = "zxcv"
      val quantity = 12
      val cost = MonetaryAmount(9800)
      val shipSpeed = "fast"
      val actual = PackingSlip.fromTokens(
        Seq(
          company,
          poId,
          shipToName,
          shipToAddress,
          shipToAddress2,
          shipToCity,
          shipToState,
          shipToZip,
          shipToPhone,
          itemId,
          quantity.toString,
          cost.toString,
          shipSpeed
        )
      )
      val expected = PackingSlip(
        company = company,
        poId = poId,
        shipToName = shipToName,
        maybeShipToAddress = Some(shipToAddress),
        maybeShipToAddress2 = None,
        maybeShipToCity = Some(shipToCity),
        maybeShipToState = Some(shipToState),
        maybeShipToZip = Some(shipToZip),
        maybeShipToPhone = Some(shipToPhone),
        itemId = itemId,
        quantity = quantity,
        maybeCost = Some(cost),
        maybeShipSpeed = Some(shipSpeed)
      )
      actual mustBe expected
    }
  }

  "PackingSlip.parseOptional" should {
    "return None if the token is out of position" in {
      PackingSlip.parseOptional(Seq("1", "2", "3"), position = 3) mustBe None
    }

    "return None if the position is an empty string" in {
      PackingSlip.parseOptional(Seq("", "2", "3"), position = 0) mustBe None
    }

    "return Some if the position is present and non-empty" in {
      PackingSlip.parseOptional(Seq("", "2", "3"), position = 1) mustBe Some("2")
    }
  }
}
