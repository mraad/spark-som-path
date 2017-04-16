package com.esri

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class NodeSpec extends FlatSpec with Matchers {
  it should "calculate vector squared distance" in {
    val node1 = Node(0, 0, new DenseVector[Double](Array(10.0, 20.0)))
    val node2 = Node(0, 0, new DenseVector[Double](Array(11.0, 22.0)))
    val dx = 11.0 - 10.0
    val dy = 22.0 - 20.0
    node1 dot node2 shouldBe (dx * dx + dy * dy)
  }
}
