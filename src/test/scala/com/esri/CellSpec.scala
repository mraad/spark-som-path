package com.esri

import breeze.linalg.{DenseVector => BDV, HashVector => BHV, SparseVector => BSV}
import org.apache.spark.mllib.linalg.{Vector => OldVector, Vectors => OldVectors}
import org.scalatest.{FlatSpec, Matchers}

class CellSpec extends FlatSpec with Matchers {

  it should "check index calculation" in {

    val arr = Seq(
      Cell(10, 20),
      Cell(11, 21),
      Cell(12, 22),
      Cell(10, 22),
      Cell(12, 20)
    )

    val qrMin = arr.min
    val qrMax = arr.max
    val qrDel = (qrMax - qrMin) + 1
    val size = qrDel size

    size shouldBe 9

    Cell(10, 20) toIndex(qrMin, qrDel) shouldBe 0
    Cell(12, 20) toIndex(qrMin, qrDel) shouldBe 2
    Cell(10, 22) toIndex(qrMin, qrDel) shouldBe 6
    Cell(12, 22) toIndex(qrMin, qrDel) shouldBe 8

    0 toCell(qrMin, qrDel) shouldBe Cell(10, 20)
    2 toCell(qrMin, qrDel) shouldBe Cell(12, 20)
    6 toCell(qrMin, qrDel) shouldBe Cell(10, 22)
    8 toCell(qrMin, qrDel) shouldBe Cell(12, 22)
  }
}
