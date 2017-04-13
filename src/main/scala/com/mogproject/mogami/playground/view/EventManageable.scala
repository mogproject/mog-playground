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
    // todo: refactor

    if (hasTouchEvent) {
      // touch
      def f(evt: TouchEvent): Unit = if (elem.disabled.forall(_ != true) && evt.changedTouches.length == 1) {
        clearHoldEvent()
        evt.preventDefault()
        onClick()
        onHold.foreach(g => registerHoldEvent(g, checker))
      }

      elem.addEventListener("touchstart", f, useCapture = false)
      if (onHold.isDefined) {
        elem.addEventListener("touchend", { _: UIEvent => clearHoldEvent() }, useCapture = false)
        elem.addEventListener("touchcancel", { _: UIEvent => clearHoldEvent() }, useCapture = false)
      }
    } else {
      // mouse
      def f(evt: MouseEvent): Unit = {
        if (evt.button == 0 /* left click */ ) {
          clearHoldEvent()
          evt.preventDefault()
          onClick()
          onHold.foreach(g => registerHoldEvent(g, checker))
        }
      }

      elem.addEventListener("mousedown", f, useCapture = false)
      if (onHold.isDefined) {
        elem.addEventListener("mouseup", { evt: MouseEvent =>
          if (evt.button == 0) clearHoldEvent()
        }, useCapture = false)
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
