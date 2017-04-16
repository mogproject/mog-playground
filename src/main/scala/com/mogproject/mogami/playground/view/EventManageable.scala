package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, TouchEvent}
import org.scalajs.dom.raw.{HTMLElement, UIEvent}
import org.scalajs.jquery.JQuery

/**
  *
  */
trait EventManageable {

  // variables
  private[this] var activeHoldEvent: Option[Int] = None

  // constants
  protected val holdInterval: Double = 1000

  def hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  def setClickEvent(elem: HTMLElement, onClick: () => Unit, onHold: Option[() => Unit] = None, checker: Option[() => Boolean] = None): Unit = {
    def onClickEvent(evt: UIEvent): Unit = {
      clearHoldEvent()
      evt.preventDefault()
      onClick()
      elem.click()
      onHold.foreach(g => registerHoldEvent(g, checker))
    }

    if (hasTouchEvent) {
      // touch
      val f = (evt: TouchEvent) => if (elem.disabled.forall(_ != true) && evt.changedTouches.length == 1) onClickEvent(evt)
      val g = (_: TouchEvent) => clearHoldEvent()

      elem.addEventListener("touchstart", f, useCapture = false)
      if (onHold.isDefined) {
        elem.addEventListener("touchend", g, useCapture = false)
        elem.addEventListener("touchcancel", g, useCapture = false)
      }
    } else {
      // mouse
      val f = (evt: MouseEvent) => if (evt.button == 0 /* left click */ ) onClickEvent(evt)
      val g = (evt: MouseEvent) => if (evt.button == 0) clearHoldEvent()

      elem.addEventListener("mousedown", f, useCapture = false)
      if (onHold.isDefined) {
        elem.addEventListener("mouseup", g, useCapture = false)
      }
    }
  }

  def setModalClickEvent(elem: HTMLElement, modal: JQuery, f: () => Unit): Unit = {
    setClickEvent(elem, () => {
      f()
      modal.asInstanceOf[BootstrapJQuery].modal("hide")
    })
  }

  //
  // mouseHoldDown
  //
  def clearHoldEvent(): Unit = activeHoldEvent.foreach { handle =>
    dom.window.clearInterval(handle)
    activeHoldEvent = None
  }

  def registerHoldEvent(f: () => Unit, checker: Option[() => Boolean] = None): Unit = {
    if (activeHoldEvent.isDefined) clearHoldEvent() // prevent double registrations
    activeHoldEvent = Some(dom.window.setInterval(() => invokeHoldEvent(f, checker), holdInterval))
  }

  /**
    * @param f       hold action
    * @param checker if it is defined and returns true, cancels all future hold events
    */
  def invokeHoldEvent(f: () => Unit, checker: Option[() => Boolean] = None): Unit = {
    if (activeHoldEvent.isDefined) {
      if (checker.exists(_ ())) {
        clearHoldEvent()
      } else {
        f()
      }
    }
  }
}
