package com.esri

import breeze.linalg.{DenseVector => BDV, SparseVector => BSV, Vector => BV}

/**
  * Class to represent a quantized track on a grid.
  *
  * @param track the track identifier.
  * @param cells the quantized cells forming the track.
  */
case class TrackCells(track: String, cells: Seq[Cell]) {

  /**
    * Convert the cells to a Breeze DenseVector with binary values.
    *
    * @param qrMin   the grid min cell location.
    * @param qrDel   the grid width and height.
    * @param vecSize the dense vector size.
    * @return a Breeze DenseVector instance.
    */
  def toBreeze(qrMin: Cell, qrDel: Cell, vecSize: Int) = {
    val data = cells
      .foldLeft(new Array[Double](vecSize))((data, cell) => {
        data(cell.toIndex(qrMin, qrDel)) = 1.0
        data
      })
    new BDV[Double](data)
  }

}