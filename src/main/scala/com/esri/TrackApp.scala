package com.esri

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{max, min}

object TrackApp extends App {

  val spark = SparkSession
    .builder()
    .appName("Path SOM")
    .master("local[*]")
    .config("spark.ui.enabled", "false")
    .getOrCreate()

  import spark.implicits._

  try {

    val df = spark
      .read
      .json("Paths")
      .as[TrackCells]
      .cache()

    val qrAgg = df
      .flatMap(_.cells)
      .distinct()
      .agg(min("q").as("qmin"), max("q").alias("qmax"), min("r").alias("rmin"), max("r").alias("rmax"))
      .as[QRMinMax]
      .head

    val qrMin = Cell(qrAgg.qmin, qrAgg.rmin)
    val qrMax = Cell(qrAgg.qmax, qrAgg.rmax)
    val qrDel = (qrMax - qrMin) + 1
    val qrSize = qrDel size

    val trainingArr = df
      .rdd
      .map(trackCells => trackCells.toBreeze(qrMin, qrDel, qrSize))
      .collect()

    val rnd = new java.security.SecureRandom()
    val somSize = 3
    val nodes = for {
      q <- 0 until somSize
      r <- 0 until somSize
    } yield Node(q, r, trainingArr(rnd.nextInt(trainingArr.length)))


    val som = SOM(nodes)
    val epochMax = trainingArr.length * 400
    implicit val progressBar = TerminalProgressBar(epochMax)
    som.train(trainingArr, epochMax, 2.5, initialAlpha = 0.4)
    som.saveAsFig("/tmp/fig.png", qrDel)

  } finally {
    spark.stop()
  }
}
