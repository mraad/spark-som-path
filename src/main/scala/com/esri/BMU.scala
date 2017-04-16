package com.esri

import org.apache.commons.math3.util.FastMath

/**
  * Best Matching Unit represents the best matching node and its associated squared distance value to a provided training vector.
  *
  * @param node    the best matching node.
  * @param distSqr the associated squared distance.
  */
case class BMU(node: Node, distSqr: Double = Double.PositiveInfinity) {
  /**
    * @return the BMU distance.
    */
  def dist() = FastMath.sqrt(distSqr)
}