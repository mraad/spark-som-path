package com.esri

import breeze.linalg.Vector
import org.apache.commons.math3.util.FastMath

import scala.annotation.tailrec
import scala.math._

/**
  * Self Organizing Map.
  *
  * @param nodes the nodes of the map arranged in a square grid.
  */
case class SOM(nodes: Seq[Node]) {

  /**
    * The network size.
    *
    * @return the number of nodes on the square edge.
    */
  def size() = {
    sqrt(nodes.length).toInt
  }

  /**
    * Add this SOM to a given SOM.
    *
    * @param that the other SOM to add to this SOM.
    * @return a new SOM instance.
    */
  def +(that: SOM): SOM = {
    SOM(nodes.zip(that.nodes).map { case (l, r) => l + r })
  }

  /**
    * Divide in place the nodes by a given divisor.
    *
    * @param divisor the node divisor.
    * @return this SOM with updated nodes.
    */
  def /(divisor: Double): SOM = {
    nodes.foreach(_ / divisor)
    this
  }

  /**
    * Find best matching unit for a given vector.
    *
    * @param vec the vector to match.
    * @return best matching unit.
    */
  def findBMU(vec: Vector[Double]): BMU = {
    nodes
      .foldLeft(BMU(nodes.head)) {
        case (prev, node) => {
          val dist2 = node dot vec
          if (dist2 < prev.distSqr) {
            BMU(node, dist2)
          } else {
            prev
          }
        }
      }
  }

  /**
    * Find best matching node for a given vector.
    *
    * @param vec the vector to match.
    * @return best matching node.
    */
  def findBMN(vec: Vector[Double]): Node = {
    nodes.minBy(_ dot vec)
  }

  /**
    * Train the SOM for a given vector, a learning rate and neighborhood distance.
    *
    * @param vec   the training vector.
    * @param alpha the learning rate.
    * @param rad   the neighborhood distance.
    * @return this SOM instance with updated nodes.
    */
  def train(vec: Vector[Double], alpha: Double, rad: Double): SOM = {
    val radSqr = rad * rad
    val radDiv = -2.0 * radSqr
    val bmn = findBMN(vec)
    nodes
      .foreach(node => {
        val distSqr = bmn distSqr node
        if (distSqr < radSqr) {
          val theta = FastMath.exp(distSqr / radDiv)
          node.update(vec, alpha * theta)
        }
      })
    this
  }

  /**
    * Train the network for a given sequence of training vectors.
    *
    * @param trainVec      the training vectors.
    * @param epochMax      the training cycles.
    * @param initialRadius the initial neighborhood radius.
    * @param initialAlpha  the initial learning rate.
    * @param pb            an implicit progress bar. An instance of NoopProgressBar is provided as a default.
    */
  def train(trainVec: Seq[Vector[Double]],
            epochMax: Int,
            initialRadius: Double,
            initialAlpha: Double = 0.4
           )(implicit pb: ProgressBar = NoopProgressBar()): Unit = {

    val radConst = epochMax / FastMath.log(initialRadius)
    val trainLen = trainVec.length
    val rnd = new java.security.SecureRandom()

    @tailrec
    def _train(epoch: Int): Unit = {
      if (epoch < epochMax) {
        val alpha = initialAlpha * FastMath.exp(-epoch / epochMax)
        val rad = initialRadius * FastMath.exp(-epoch / radConst)
        val vec = trainVec(rnd.nextInt(trainLen))
        train(vec, alpha, rad)
        pb.progress()
        _train(epoch + 1)
      }
    }

    _train(0)

    pb.finish()
  }
}
