package com.mogproject.mogami.playground.view

import org.scalajs.dom.CanvasRenderingContext2D

/**
  * Draw text on a canvas
  */
case class TextRenderer(ctx: CanvasRenderingContext2D,
                        text: String,
                        font: String,
                        color: String,
                        areaLeft: Int, // x0
                        areaTop: Int, // y0
                        areaWidth: Int, // w
                        areaHeight: Int, // h
                        textBottomLeft: Int = 0, // x
                        textBottomTop: Int = 0, // y
                        effector: TextRenderer => Unit = _ => Unit, // Note: Do not set directly.
                        rotated: Boolean = false, // Note: Do not set directly. Use `withRotate`.
                        restoreRequired: Boolean = false, // Note: Do not set directly.
                        textWidthHint: Option[Int] = None, // a
                        textHeightHint: Option[Int] = None // b
                       ) {

  lazy val textWidth: Int = textWidthHint.getOrElse {
    ctx.font = font
    ctx.measureText(text).width.toInt
  }
  lazy val textHeight: Int = textHeightHint.getOrElse(font.takeWhile(_.isDigit).toInt)

  def shift(x: Int, y: Int) = this.copy(textBottomLeft = textBottomLeft + x, textBottomTop = textBottomTop + y)

  /**
    * x := x0 + (w - a) / 2
    */
  def alignCenter: TextRenderer = this.copy(
    textBottomLeft = areaLeft + (areaWidth - textWidth) / 2,
    textWidthHint = Some(textWidth)
  )

  /**
    * y := x0 + h - (h - b) / 2
    */
  def alignMiddle: TextRenderer = this.copy(
    textBottomTop = (areaTop + areaHeight - (areaHeight - textHeight) / 2.0).toInt,
    textHeightHint = Some(textHeight)
  )

  /**
    * x := x0 + w - a
    */
  def alignRight: TextRenderer = this.copy(
    textBottomLeft = areaLeft + areaWidth - textWidth,
    textWidthHint = Some(textWidth)
  )

  /**
    * y := y0 + h
    */
  def alignBottom: TextRenderer = this.copy(
    textBottomTop = areaTop + areaHeight
  )

  /**
    * x' = x0 + w - (x - x0) - a
    * y' = y0 + (h - (y - y0))
    * x := -x' - a = -2 * x0 - w + x
    * y := -y' + b = -2 * y0 - h + y
    */
  def withRotate(rotate: Boolean): TextRenderer = if (rotate) {
    val x = -2 * areaLeft - areaWidth + textBottomLeft
    val y = -2 * areaTop - areaHeight + textBottomTop

    this.copy(
      textBottomLeft = x,
      textBottomTop = y,
      rotated = true,
      effector = r => {
        effector(r)
        r.ctx.rotate(math.Pi)
      },
      restoreRequired = true
    )
  } else this

  def withStroke(strokeColor: String, strokeWidth: Int): TextRenderer = this.copy(
    effector = r => {
      effector(r)
      r.ctx.lineWidth = strokeWidth
      r.ctx.strokeStyle = strokeColor
      r.ctx.strokeText(r.text, r.textBottomLeft, r.textBottomTop)
    },
    restoreRequired = true
  )

  def withShadow(shadowColor: String, shadowOffsetX: Int, shadowOffsetY: Int, shadowBlur: Int): TextRenderer = {
    this.copy(
      effector = r => {
        effector(r)
        r.ctx.shadowColor = shadowColor
        r.ctx.shadowOffsetX = shadowOffsetX
        r.ctx.shadowOffsetX = shadowOffsetY
        r.ctx.shadowBlur = shadowBlur
      },
      restoreRequired = true
    )
  }

  def render(): Unit = {
    ctx.font = font
    ctx.fillStyle = color
    ctx.textBaseline = "alphabetic"
    if (restoreRequired) ctx.save()
    effector(this)
    ctx.fillText(text, textBottomLeft, textBottomTop)
    if (restoreRequired) ctx.restore()
  }

}
