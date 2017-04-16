package com.esri

import breeze.linalg.DenseVector
import org.apache.spark.sql.SparkSession

import scala.annotation.tailrec

object ColorSpark extends App {

  val spark = SparkSession
    .builder()
    .appName("SOM Color")
    .master("local[*]")
    .config("spark.ui.enabled", "false")
    .getOrCreate()

  try {
    val somSize = 7
    val nodes = for {
      q <- 0 until somSize
      r <- 0 until somSize
    } yield Node.rand(q, r, 3)

    val rnd = new java.security.SecureRandom()
    val colors = for (_ <- 0 until 10000)
      yield DenseVector[Double](rnd.nextDouble, rnd.nextDouble, rnd.nextDouble)

    val numParts = 1024
    val rdd = spark.sparkContext
      .parallelize(colors)
      .repartition(numParts)
      .cache()

    val epochMax = 40
    val radInit = somSize / 2
    val alphaInit = 0.2
    val radDecay = ExpDecay(radInit, 1, epochMax)
    val alphaDecay = ExpDecay(alphaInit, 0.001, epochMax)

    @tailrec
    def _train(epoch: Int, som: SOM): SOM = {
      if (epoch == epochMax) {
        som
      }
      else {
        val alpha = alphaDecay.value(epoch)
        val rad = radDecay.value(epoch)
        val bc = spark.sparkContext.broadcast(som)
        val newSOM = rdd
          .mapPartitions(iter => {
            val localSOM = iter.foldLeft(bc.value)((prevSOM, vec) => {
              prevSOM.train(vec, alpha, rad)
            })
            Some(localSOM).iterator
          })
          .reduce(_ + _) / numParts.toDouble
        _train(epoch + 1, newSOM)
      }
    }

    _train(0, SOM(nodes))
      .saveAsPNG("/tmp/colors.png", 20)
  }
  finally {
    spark.stop()
  }

}