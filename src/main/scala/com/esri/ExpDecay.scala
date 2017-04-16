package com.esri

import org.apache.commons.math3.util.FastMath

/**
  * Ripoff from commons math ExponentialDecayFunction - this is Serializable
  *
  * @param initValue      the initial value of the decay.
  * @param valueAtNumStep the final value of the decay.
  * @param numStep        the decay range.
  */
class ExpDecay(initValue: Double, valueAtNumStep: Double, numStep: Double) extends Decay {

  private val decay = -FastMath.log(valueAtNumStep / initValue) / numStep

  /**
    * The value of the decay for a given step in the range.
    *
    * @param step the step in the range.
    * @return the decay value at the step.
    */
  override def value(step: Double) = initValue * FastMath.exp(-step * decay)
}

/**
  * ExpDecay companion object.
  */
object ExpDecay extends Serializable {
  def apply(initValue: Double, valueAtNumCall: Double, numCall: Double): ExpDecay = new ExpDecay(initValue, valueAtNumCall, numCall)
}
