package com.esri

import breeze.linalg.{DenseVector, Vector}

/**
  * A node in the SOM network.
  *
  * @param q   the column position in the SOM grid.
  * @param r   the row position in the SOM grid.
  * @param vec the weight vector associated with that node.
  */
case class Node(q: Int, r: Int, var vec: Vector[Double]) {

  /**
    * Add in place the vector of this node to the vector of a given node.
    *
    * @param that the given node.
    * @return this instance with updated vector values.
    */
  def +(that: Node) = {
    vec += that.vec
    this
  }

  /**
    * Divide in place the vector nodes with a given divisor value.
    *
    * @param divisor the divisor value.
    */
  def /(divisor: Double): Unit = {
    vec /= divisor
  }

  /**
    * Update in place the vector of this nodes to be "close" to the training vector.
    *
    * @param train the training vector.
    * @param fact  the update factor.
    */
  def update(train: Vector[Double], fact: Double): Unit = {
    vec = vec + fact * (train - vec)
  }

  /**
    * Calculate the squared distance to a given node.  This is based on position on the grid.
    *
    * @param that the given node.
    * @return sqrt(pow(dq,2)+pow(dr,2))
    */
  def distSqr(that: Node) = {
    val dq = this.q - that.q
    val dr = this.r - that.r
    dq * dq + dr * dr
  }

  /**
    * Calculate the squared distance to a given vector.
    *
    * @param otherVec the other vector.
    * @return the dot product of the vector difference.
    */
  def dot(otherVec: Vector[Double]): Double = {
    val d = vec - otherVec
    d dot d
  }

  /**
    * Calculate the squared distance based on the vector of another node.
    *
    * @param otherNode the other node.
    * @return the dot product of the vector difference.
    */
  def dot(otherNode: Node): Double = {
    this dot otherNode.vec
  }
}

/**
  * Node companion object.
  */
object Node extends Serializable {
  /**
    * Create node at a given column, row with a random dense vector of a give size.
    *
    * @param q    the column in the grid.
    * @param r    the row in the grid.
    * @param size the size of the random vector.
    * @return a new Node instance.
    */
  def rand(q: Int, r: Int, size: Int): Node = {
    Node(q, r, DenseVector.rand[Double](size))
  }
}
