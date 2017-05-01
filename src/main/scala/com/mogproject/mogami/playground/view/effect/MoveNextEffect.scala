package com.mogproject.mogami.playground.view.effect

import com.mogproject.mogami.playground.view.{Layout, RoundRect, TextRenderer}
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D

import scala.collection.mutable

/**
  *
  */
trait MoveNextEffect {
  def layout: Layout

  private[this] val drawing: mutable.Seq[Int] = mutable.Seq(0, 0)

  protected def layer5: CanvasRenderingContext2D

  def startMoveForwardEffect(): Unit = startMoveEffectHelper(0, "▶", layout.moveForward)

  def startMoveBackwardEffect(): Unit = startMoveEffectHelper(1, "◀", layout.moveBackward)

  private[this] def startMoveEffectHelper(index: Int, text: String, roundRect: RoundRect): Unit = {
    if (drawing(index) == 0) {
      roundRect.draw(layer5, layout.color.bsPrimary, layout.moveForwardStrokeSize, layout.moveForwardAlpha)
      TextRenderer(layer5, text, layout.font.moveForward, layout.color.bsPrimary, roundRect.left, roundRect.top, roundRect.width, roundRect.height)
        .alignCenter.alignMiddle.render()
    }

    val f = () => {
      drawing(index) -= 1
      if (drawing(index) == 0) roundRect.clear(layer5)
    }

    drawing(index) += 1
    dom.window.setTimeout(f, 150)
  }
}
