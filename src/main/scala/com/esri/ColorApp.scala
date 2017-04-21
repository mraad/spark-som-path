package com.esri

import java.awt.Color

import breeze.linalg.DenseVector

/**
  * App to self organize sequence of HSB colors into an 8x8 grid.
  */
object ColorApp extends App {

  val rnd = new java.security.SecureRandom()
  val colorSeq = for (_ <- 0 until 200)
    yield {
      val r = rnd.nextInt(255)
      val g = rnd.nextInt(255)
      val b = rnd.nextInt(255)
      val hsb = Color.RGBtoHSB(r, g, b, null).map(_.toDouble)
      DenseVector[Double](hsb)
    }

  val colorLen = colorSeq.length
  val somSize = 8
  val nodes = for {
    q <- 0 until somSize
    r <- 0 until somSize
  } yield Node(q, r, colorSeq(rnd.nextInt(colorLen)))


  val epochMax = colorLen * 100
  implicit val pb = TerminalProgressBar(epochMax)
  val som = SOM(nodes)
  som.train(colorSeq, epochMax, somSize / 2, initialAlpha = 0.3)
  som.saveAsPNG("/tmp/som.png", 20)
}