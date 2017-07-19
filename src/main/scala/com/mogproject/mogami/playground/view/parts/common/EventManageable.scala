package com.mogproject.mogami.playground.view.parts.common

import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLElement, UIEvent}
import org.scalajs.dom.{MouseEvent, TouchEvent}
import org.scalajs.jquery.JQuery

/**
  *
  */
trait EventManageable {

  // constants
  protected val holdInterval: Double = 1000
  private[this] val touchEndInterval: Double = 350 // ms

  // variables
  private[this] var activeHoldEvent: Option[Int] = None
  private[this] var lastTouchEnd: Double = -touchEndInterval

  lazy val hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  private[this] def withCommonWrapper[E <: UIEvent](preventDefault: Boolean = true, check: E => Boolean = { _: E => true })
                                                   (f: E => Unit): E => Unit = { evt: E =>
    if (check(evt)) {
      clearHoldEvent()
      if (preventDefault) evt.preventDefault
      f(evt)
    }
  }

  private[this] def isValidMouseEvent(evt: MouseEvent): Boolean = evt.button == 0 && lastTouchEnd < evt.timeStamp - touchEndInterval

  private[this] def nullEvent = withCommonWrapper(preventDefault = false) { _ => }

  /**
    * Register 'touchstart' event
    */
  def registerTouchStart(elem: HTMLElement, f: TouchEvent => Unit): Unit = if (hasTouchEvent) {
    val wrapper = withCommonWrapper[TouchEvent](check = _.changedTouches.length == 1)(f)
    elem.addEventListener("touchstart", wrapper, useCapture = false)
  }

  /**
    * Register 'touchend' event
    */
  def registerTouchEnd(elem: HTMLElement, f: TouchEvent => Unit): Unit = if (hasTouchEvent) {
    val wrapper = withCommonWrapper[TouchEvent]() { evt => lastTouchEnd = evt.timeStamp; f(evt) }
    elem.addEventListener("touchend", wrapper, useCapture = false)
  }

  /**
    * Register 'touchcancel' event
    */
  def registerTouchCancel(elem: HTMLElement): Unit = if (hasTouchEvent) {
    elem.addEventListener("touchcancel", nullEvent, useCapture = false)
  }

  /**
    * Register 'mousedown' event
    *
    * @note Reacts only to the left click. (button == 0)
    */
  def registerMouseDown(elem: HTMLElement, f: MouseEvent => Unit): Unit = {
    elem.addEventListener("mousedown", withCommonWrapper(check = isValidMouseEvent)(f), useCapture = false)
  }

  /**
    * Register 'mouseup' event
    *
    * @note Reacts only to the left click. (button == 0)
    */
  def registerMouseUp(elem: HTMLElement, f: MouseEvent => Unit): Unit = {
    elem.addEventListener("mouseup", withCommonWrapper(check = isValidMouseEvent)(f), useCapture = false)
  }

  /**
    * Register 'mouseout' event
    *
    * @note Reacts only to the left click. (button == 0)
    */
  def registerMouseOut(elem: HTMLElement): Unit = {
    elem.addEventListener("mouseout", nullEvent, useCapture = false)
  }

  /**
    * Register 'mousemove' event
    */
  def registerMouseMove(elem: HTMLElement, f: MouseEvent => Unit): Unit = {
    elem.addEventListener("mousemove", f, useCapture = false)
  }


  def setClickEvent(elem: HTMLElement, onClick: () => Unit, onHold: Option[() => Unit] = None, checker: Option[() => Boolean] = None): Unit = {
    def onClickEvent(evt: UIEvent): Unit = {
      onClick()
      onHold.foreach(g => registerHoldEvent(g, checker))
    }

    registerTouchStart(elem, onClickEvent)
    registerMouseDown(elem, onClickEvent)

    if (onHold.isDefined) {
      registerTouchEnd(elem, { _ => })
      registerTouchCancel(elem)
      registerMouseUp(elem, { _ => })
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
