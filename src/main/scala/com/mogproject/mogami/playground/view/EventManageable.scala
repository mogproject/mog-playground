package com.mogproject.mogami.playground.view

import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, TouchEvent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.HTMLElement

/**
  *
  */
trait EventManageable {

  def hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  def setClickEvent(elem: HTMLElement, f: () => Unit): Unit = {
    val t = hasTouchEvent.fold("touchstart", "mousedown")
    val g = if (hasTouchEvent) {
      evt: TouchEvent => {
        if (elem.disabled.forall(_ != true) && evt.changedTouches.length == 1) {
          evt.preventDefault()
          f()
        }
      }
    } else { evt: MouseEvent => if (evt.button == 0) f() }
    elem.addEventListener(t, g, useCapture = false)
  }

}
