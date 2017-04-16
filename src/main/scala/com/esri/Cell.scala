package com.esri

/**
  * A Cell instance represents a quantized grid portion of a track.
  *
  * @param q the cell grid column.
  * @param r the cell grid row.
  */
case class Cell(q: Int, r: Int) extends Ordered[Cell] {

  /**
    * Subtract a given cell from this cell.
    *
    * @param that a given cell.
    * @return a new Cell instance with component difference.
    */
  def -(that: Cell) = {
    Cell(this.q - that.q, this.r - that.r)
  }

  /**
    * Add a given cell to this cell.
    *
    * @param that a given cell.
    * @return a new Cell instance with component addition.
    */
  def +(that: Cell) = {
    Cell(this.q + that.q, this.r + that.r)
  }

  /**
    * Increment the component of this cell by a given value.
    *
    * @param i a given increment value.
    * @return a new Cell instance with incremented components.
    */
  def +(i: Int) = {
    Cell(this.q + i, this.r + i)
  }

  /**
    * @return the size of the cell as q * r.
    */
  def size() = {
    q * r
  }

  /*
  def min(that: Cell) = {
    Cell(q min that.q, r min that.r)
  }

  def max(that: Cell) = {
    Cell(q max that.q, r max that.r)
  }
  */

  /**
    * Convert this cell to a linear position on a grid.
    *
    * @param qrMin the grid min cell.
    * @param qrDel the grid width and height.
    * @return the linear position in the grid.
    */
  def toIndex(qrMin: Cell, qrDel: Cell) = {
    val dq = q - qrMin.q
    val dr = r - qrMin.r
    dq + dr * qrDel.q
  }

  /*
  def toX(origX: Double, size: Double) = {
    origX + q * size
  }

  def toY(origY: Double, size: Double) = {
    origY + r * size
  }
  */

  /**
    * Compare this cell to a given cell.
    *
    * @param that a given cell.
    * @return if this.q < that.q then -1, if this.q > that.q then 1, if this.r < that.r then -1, if this.r > that.r then 1 otherwise 0.
    */
  override def compare(that: Cell): Int = {
    this.q compare that.q match {
      case 0 => this.r compare that.r
      case c => c
    }
  }
}