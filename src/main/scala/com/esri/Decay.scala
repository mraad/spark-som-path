package com.esri

/**
  * Decay trait.
  */
trait Decay extends Serializable {

  /**
    * The value of the decay for a given step in the range.
    *
    * @param step the step in the range.
    * @return the decay value at the step.
    */
  def value(step: Double): Double
}
