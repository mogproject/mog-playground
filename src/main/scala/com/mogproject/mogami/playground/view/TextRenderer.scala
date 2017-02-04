package com.mogproject.mogami.playground.view

import org.scalajs.dom.CanvasRenderingContext2D
import com.mogproject.mogami.util.Implicits._

/**
  * Draw text on a canvas
  */
trait TextRenderer {
  def layout: Layout

  private[this] def drawText(ctx: CanvasRenderingContext2D,
                             text: String,
                             left: Int,
                             top: Int,
                             font: String,
                             color: String,
                             rotated: Boolean = false,
                             measuredSize: Option[(Int, Int)]
                            ): Unit = {
    ctx.font = font
    ctx.fillStyle = color

    if (rotated) {
      val m = measuredSize.getOrElse(measureText(ctx, text, font))
      ctx.save()
      ctx.rotate(math.Pi)
      ctx.fillText(text, -left - m._1, -top + m._2)
      ctx.restore()
    } else {
      ctx.fillText(text, left, top)
    }
  }

  private[this] def measureText(ctx: CanvasRenderingContext2D,
                                text: String,
                                font: String
                               ): (Int, Int) = {
    ctx.font = font
    (ctx.measureText(text).width.toInt, font.takeWhile(_.isDigit).toInt)
  }

  private[this] def adjustText(ctx: CanvasRenderingContext2D,
                               text: String,
                               font: String,
                               color: String,
                               rotated: Boolean = false,
                               xOffset: Int = 0,
                               yOffset: Int = 0)
                              (f: ((Int, Int)) => (Int, Int)): Unit = {
    val sign = rotated.fold(-1, 1)
    val m = measureText(ctx, text, font)
    val (left, top) = f(m)
    drawText(ctx, text, left + sign * xOffset, top + sign * yOffset, font, color, rotated, Some(m))
  }

  def drawTextCenter(ctx: CanvasRenderingContext2D,
                     text: String,
                     left: Int,
                     top: Int,
                     width: Int,
                     height: Int,
                     font: String,
                     color: String,
                     rotated: Boolean = false,
                     xOffset: Int = 0,
                     yOffset: Int = 0
                    ): Unit = adjustText(ctx, text, font, color, rotated, xOffset, yOffset) { case (x, y) =>
    (left + (width - x) / 2, top + height - (height - y) / 2)
  }

  def drawTextBottomRight(ctx: CanvasRenderingContext2D,
                          text: String,
                          left: Int,
                          top: Int,
                          width: Int,
                          height: Int,
                          font: String,
                          color: String,
                          rotated: Boolean = false,
                          xOffset: Int = 0,
                          yOffset: Int = 0): Unit = adjustText(ctx, text, font, color, rotated, xOffset, yOffset) { case (x, y) =>
    rotated.fold((left, top + y + 1), (left + width - x, top + height))
  }

}
