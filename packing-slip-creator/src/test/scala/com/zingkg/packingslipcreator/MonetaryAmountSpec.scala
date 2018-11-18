package com.zingkg.packingslipcreator

import org.scalacheck.Gen

object MonetaryAmountSpec {
  def gen: Gen[MonetaryAmount] =
    Gen.chooseNum(0, 100000).map(MonetaryAmount(_))
}
