package com.esri

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class SOMSpec extends FlatSpec with Matchers {
  it should "train the SOM" in {
    val nodes = for (q <- 0 until 10) yield {
      Node(q, 0, new DenseVector[Double](Array(q, 0.0)))
    }
    nodes.length shouldBe 10
    val som = SOM(nodes)
    som.train(new DenseVector[Double](Array(5.0, 0.0)), 1.0, 0.1)
  }
}
