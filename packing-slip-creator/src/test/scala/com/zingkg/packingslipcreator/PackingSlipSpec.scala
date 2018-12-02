package com.zingkg.packingslipcreator

import org.scalacheck.Gen
import org.scalatest.{ MustMatchers, WordSpec }
import org.scalatest.prop.PropertyChecks

class PackingSlipSpec
  extends WordSpec
  with MustMatchers
  with PropertyChecks {
  "PackingSlip.key" should {
    "return a key based on select data members" in forAll(PackingSlipSpec.gen) { slip =>
      val expected = PackingSlipKey(
        company = slip.company,
        poId = slip.poId,
        shipToName = slip.shipToName,
        maybeShipToAddress = slip.maybeShipToAddress,
        maybeShipToAddress2 = slip.maybeShipToAddress2,
        maybeShipToCity = slip.maybeShipToCity,
        maybeShipToState = slip.maybeShipToState,
        maybeShipToZip = slip.maybeShipToZip,
        maybeShipToPhone = slip.maybeShipToPhone,
        maybeShipSpeed = slip.maybeShipSpeed
      )
      slip.key mustBe expected
    }
  }

  "PackingSlip.fromTokens" should {
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

object PackingSlipSpec {
  def gen: Gen[PackingSlip] =
    for {
      company <- Gen.asciiStr
      poId <- Gen.numStr
      shipToName <- Gen.asciiStr
      maybeShipToAddress <- Gen.option(Gen.alphaStr)
      maybeShipToAddress2 <- Gen.option(Gen.alphaStr)
      maybeShipToCity <- Gen.option(Gen.alphaStr)
      maybeShipToState <- Gen.option(Gen.alphaStr)
      maybeShipToZip <- Gen.option(Gen.numStr)
      maybeShipToPhone <- Gen.option(Gen.numStr)
      itemId <- Gen.asciiStr
      quantity <- Gen.chooseNum(0, 100)
      maybeCost <- Gen.option(MonetaryAmountSpec.gen)
      maybeShipSpeed <- Gen.option(Gen.alphaStr)
    } yield
      PackingSlip(
        company = company,
        poId = poId,
        shipToName = shipToName,
        maybeShipToAddress = maybeShipToAddress,
        maybeShipToAddress2 = maybeShipToAddress2,
        maybeShipToCity = maybeShipToCity,
        maybeShipToState = maybeShipToState,
        maybeShipToZip = maybeShipToZip,
        maybeShipToPhone = maybeShipToPhone,
        itemId = itemId,
        quantity = quantity,
        maybeCost = maybeCost,
        maybeShipSpeed = maybeShipSpeed)
}
