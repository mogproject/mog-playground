package com.mogproject.mogami.playground.view

import org.scalajs.dom.CanvasRenderingContext2D

trait Shape {
  def draw(ctx: CanvasRenderingContext2D, color: String, lineWidth: Int): Unit
}

case class Rectangle(left: Int, top: Int, width: Int, height: Int) extends Shape {
  val right: Int = left + width
  val bottom: Int = top + height

  override def draw(ctx: CanvasRenderingContext2D, color: String = "black", lineWidth: Int = 1): Unit = {
    val offset = -math.min(lineWidth, 0)

    ctx.beginPath()
    ctx.rect(left + offset, top + offset, width - offset * 2, height - offset * 2)
    ctx.lineWidth = math.abs(lineWidth)
    ctx.strokeStyle = color
    ctx.stroke()
  }

  def drawFill(ctx: CanvasRenderingContext2D, color: String, margin: Double = 0): Unit = {
    ctx.fillStyle = color
    ctx.fillRect(left + margin, top + margin, width - margin * 2, height - margin * 2)
  }

  def clear(ctx: CanvasRenderingContext2D, margin: Double = 0): Unit = {
    ctx.clearRect(left + margin, top + margin, width - margin * 2, height - margin * 2)
  }

  def isInside(x: Double, y: Double): Boolean = left < x && x < right && top < y && y < bottom
}

case class Line(fromX: Double, fromY: Double, toX: Double, toY: Double) extends Shape {
  override def draw(ctx: CanvasRenderingContext2D, color: String = "black", lineWidth: Int = 1): Unit = {
    ctx.beginPath()
    ctx.moveTo(fromX, fromY)
    ctx.lineTo(toX, toY)
    ctx.lineWidth = lineWidth
    ctx.strokeStyle = color
    ctx.stroke()
  }
}

case class Circle(x: Double, y: Double, r: Double) extends Shape {
  override def draw(ctx: CanvasRenderingContext2D, color: String = "black", lineWidth: Int = 0): Unit = {
    ctx.beginPath()
    ctx.arc(x, y, r, 0, math.Pi * 2, anticlockwise = true)
    ctx.fillStyle = color
    ctx.fill()
  }
}

object Circle {
  val unit = Circle(0, 0, 1)
}