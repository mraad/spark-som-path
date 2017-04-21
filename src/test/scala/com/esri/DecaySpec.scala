package com.esri

import org.scalatest.{FlatSpec, Matchers}

class DecaySpec extends FlatSpec with Matchers {
  it should "decay" in {
    val decay = ExpLogDecay(100, 1, 10)
    decay(0) shouldBe 100.0 +- 0.0000001
    decay(10) shouldBe 1.0 +- 0.0000001
  }
}
