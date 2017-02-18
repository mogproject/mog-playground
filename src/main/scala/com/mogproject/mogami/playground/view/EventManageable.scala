package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, TouchEvent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery.JQuery

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
    } else { evt: MouseEvent => if (evt.button == 0 /* left click */ ) f() }
    elem.addEventListener(t, g, useCapture = false)
  }

  def setModalClickEvent(elem: HTMLElement, modal: JQuery, f: () => Unit): Unit = {
    setClickEvent(elem, () => { f(); modal.asInstanceOf[BootstrapJQuery].modal("hide") })
  }


}
