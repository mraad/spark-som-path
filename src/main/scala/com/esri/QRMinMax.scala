package com.esri

/**
  * Helper class to enable the accumulation of q and r min and max values.
  *
  * @param qmin the min q value.
  * @param qmax the max q value.
  * @param rmin the min r value.
  * @param rmax the max r value.
  */
case class QRMinMax(qmin: Int, qmax: Int, rmin: Int, rmax: Int)
