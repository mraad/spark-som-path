package com

import java.awt.image.BufferedImage
import java.awt.{Color, LinearGradientPaint, _}
import java.io.FileOutputStream
import javax.imageio.ImageIO

import org.apache.commons.math.stat.descriptive.SummaryStatistics
import resource._

import scala.collection.JavaConversions._
import scalaxy.loops._

package object esri {

  implicit class IntImplicits(i: Int) {
    @inline def toCell(qrMin: Cell, qrDel: Cell) = {
      Cell(qrMin.q + i % qrDel.q, qrMin.r + i / qrDel.q)
    }
  }

  implicit class SOMImplicits(som: SOM) {

    def createColorMap(colors: Color*) = {
      val steps = 1.0F / (colors.length - 1)
      val fractions = (0 until colors.length).map(i => (i * steps) min 1.0F).toArray
      val gradient = new LinearGradientPaint(0, 0, 256, 1, fractions, colors.toArray, MultipleGradientPaint.CycleMethod.REPEAT)
      val bi = new BufferedImage(256, 1, BufferedImage.TYPE_INT_ARGB)
      val g = bi.createGraphics
      try {
        g.setPaint(gradient)
        g.fillRect(0, 0, 256, 1)
      } finally g.dispose()
      (0 until 256).map(w => new Color(bi.getRGB(w, 0))).toArray
    }

    def saveAsFig(outputPath: String, qrDel: Cell): Unit = {

      val colors = createColorMap(Color.LIGHT_GRAY, Color.RED)

      val figSize = 180
      val somSize = som.size
      val imgSize = somSize * figSize
      val bi = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
      val g = bi.createGraphics()
      try {
        g.setRenderingHints(Map(
          RenderingHints.KEY_ANTIALIASING -> RenderingHints.VALUE_ANTIALIAS_ON,
          RenderingHints.KEY_RENDERING -> RenderingHints.VALUE_RENDER_QUALITY
        ))
        g.setBackground(Color.WHITE)
        som.nodes
          .zipWithIndex
          .foreach {
            case (node, i) => {
              val q = i % somSize
              val r = i / somSize
              val xmin = q * figSize
              val ymin = r * figSize
              g.setColor(Color.WHITE)
              g.drawRect(xmin, imgSize - ymin - figSize, figSize, figSize)
              node.vec.toArray
                .zipWithIndex
                .filter {
                  case (d, _) => d > 0.05
                }
                .sortBy {
                  case (d, _) => d
                }
                .foreach {
                  case (d, j) => {
                    val x = figSize * (j % qrDel.q) / qrDel.q
                    val y = figSize * (j / qrDel.q) / qrDel.r
                    val cx = xmin + x
                    val cy = imgSize - ymin - y - 1
                    val cc = colors((d * 255).toInt)
                    // bi.setRGB(cx, cy, cc)
                    g.setColor(cc)
                    g.fillRect(cx - 1, cy - 1, 3, 3)
                  }
                }
            }
          }
      } finally {
        g.dispose()
      }
      for (outputStream <- managed(new FileOutputStream(outputPath)))
        ImageIO.write(bi, "png", outputStream)
    }

    def saveAsPNG(outputPath: String, cellSize: Int): Unit = {
      val somSize = som.size
      val imgSize = somSize * cellSize
      val bi = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
      som.nodes.foreach(node => {
        var sx = node.q * cellSize
        for (_ <- 0 until cellSize optimized) {
          var sy = node.r * cellSize
          for (_ <- 0 until cellSize optimized) {
            val r = (255.0 * node.vec(0)).toInt
            val g = (255.0 * node.vec(1)).toInt
            val b = (255.0 * node.vec(2)).toInt
            val rgb = (r << 16) | (g << 8) | b
            bi.setRGB(sx, sy, rgb)
            sy += 1
          }
          sx += 1
        }
      })
      for (outputStream <- managed(new FileOutputStream(outputPath)))
        ImageIO.write(bi, "png", outputStream)
    }

    def saveIris(outputPath: String, cellSize: Int, irisData: Seq[Iris]): Unit = {

      val colorArr = createColorMap(
        Color.LIGHT_GRAY,
        Color.DARK_GRAY,
        Color.BLACK
      )

      case class NodeStats(sum: Double, n: Double = 1, setosa: Int = 0, versicolor: Int = 0, virginica: Int = 0) {
        def addSetosa(dist: Double) = {
          NodeStats(sum + dist, n + 1, setosa + 1, versicolor, virginica)
        }

        def addVersicolor(dist: Double) = {
          NodeStats(sum + dist, n + 1, setosa, versicolor + 1, virginica)
        }

        def addVirginica(dist: Double) = {
          NodeStats(sum + dist, n + 1, setosa, versicolor, virginica + 1)
        }

        def avg() = sum / n
      }

      case class SomStats(sumStats: SummaryStatistics = new SummaryStatistics(),
                          nodes: Map[Node, NodeStats] = Map.empty
                         )

      val somStats = irisData
        .foldLeft(SomStats()) {
          case (somStats, iris) => {
            val bmu = som.findBMU(iris.vec)
            val dist = bmu.dist

            somStats.sumStats.addValue(dist)

            val nodeStats = somStats.nodes.get(bmu.node) match {
              case Some(prev) => iris.label match {
                case "Iris-setosa" => prev.addSetosa(dist)
                case "Iris-versicolor" => prev.addVersicolor(dist)
                case _ => prev.addVirginica(dist)
              }
              case _ => iris.label match {
                case "Iris-setosa" => NodeStats(dist, setosa = 1)
                case "Iris-versicolor" => NodeStats(dist, versicolor = 1)
                case _ => NodeStats(dist, virginica = 1)
              }
            }
            SomStats(somStats.sumStats, somStats.nodes + (bmu.node -> nodeStats))
          }
        }

      val del = somStats.sumStats.getMax - somStats.sumStats.getMin
      val nodeColor = somStats.nodes.map {
        case (node, nodeStats) => node -> colorArr((255 * (nodeStats.avg - somStats.sumStats.getMin) / del).toInt)
      }

      // val name2color = Map("Iris-setosa" -> Color.RED, "Iris-versicolor" -> Color.GREEN, "Iris-virginica" -> Color.BLUE)
      // val cellHalf = cellSize / 2
      val somSize = som.size
      val imgSize = somSize * cellSize
      val bi = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
      val g = bi.createGraphics()
      try {
        g.setRenderingHints(Map(
          RenderingHints.KEY_ANTIALIASING -> RenderingHints.VALUE_ANTIALIAS_ON,
          RenderingHints.KEY_RENDERING -> RenderingHints.VALUE_RENDER_QUALITY,
          RenderingHints.KEY_TEXT_ANTIALIASING -> RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ))
        g.setBackground(Color.WHITE)
        g.fillRect(0, 0, imgSize, imgSize)
        som.nodes.foreach(node => {
          val sx = node.q * cellSize
          val sy = node.r * cellSize
          g.setColor(nodeColor.getOrElse(node, Color.WHITE))
          g.fillRect(sx, sy, cellSize, cellSize)
          somStats.nodes.get(node).foreach(nodeStats => {
            var x = sx + 2
            var y = sy + 2
            g.setColor(Color.RED)
            for (_ <- 0 until nodeStats.setosa optimized) {
              g.fillRect(x, y, 10, 10)
              x += 11
            }
            y += 12
            x = sx + 2
            g.setColor(Color.GREEN)
            for (_ <- 0 until nodeStats.versicolor optimized) {
              g.fillRect(x, y, 10, 10)
              x += 11
            }
            y += 12
            x = sx + 2
            g.setColor(Color.BLUE)
            for (_ <- 0 until nodeStats.virginica optimized) {
              g.fillRect(x, y, 10, 10)
              x += 11
            }
          })
        })
      } finally {
        g.dispose()
      }
      for (outputStream <- managed(new FileOutputStream(outputPath)))
        ImageIO.write(bi, "png", outputStream)
    }
  }

}
