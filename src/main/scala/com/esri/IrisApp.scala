package com.esri

import java.io.File

import breeze.linalg.{DenseVector => BDV, Vector => BV}
import com.univocity.parsers.csv.{CsvParser, CsvParserSettings}

import scala.collection.JavaConversions._
import scala.math._

case class Iris(label: String, vec: BV[Double])

/**
  * App to SOM iris dataset - https://en.wikipedia.org/wiki/Iris_flower_data_set
  */
object IrisApp extends App {

  val settings = new CsvParserSettings()
  val reader = new CsvParser(settings)
  val irisArr = reader.parseAll(new File("iris.data.txt"))
    .map(row => {
      val dv = new BDV[Double](Array(row(0).toDouble, row(1).toDouble, row(2).toDouble, row(3).toDouble))
      val label = row(4)
      val vec = dv / sqrt(dv dot dv)
      Iris(label, vec)
    })

  val rnd = new java.security.SecureRandom()
  val somSize = 7
  val nodes = for {
    q <- 0 until somSize
    r <- 0 until somSize
  } yield {
    val iris = irisArr(rnd.nextInt(irisArr.length))
    Node(q, r, iris.vec)
  }

  val data = irisArr.map(_.vec)
  val epochMax = 10000
  implicit val pb = TerminalProgressBar(epochMax)
  val som = SOM(nodes)
  val aDecay = ExpDecay(0.5, epochMax)
  val rDecay = ExpDecay(4, epochMax)
  som.trainDecay(data, epochMax, aDecay, rDecay)
  som.saveIris("/tmp/iris.png", 110, irisArr)
}
