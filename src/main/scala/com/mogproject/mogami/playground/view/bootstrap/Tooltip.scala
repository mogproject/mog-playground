package com.mogproject.mogami.playground.view.bootstrap

import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery.{JQuery, jQuery}

import scala.scalajs.js

/**
  *
  */
object Tooltip {
  def enableHoverToolTip(isMobile: Boolean): Unit = enableHoverToolTip(jQuery("""[data-toggle="tooltip"]"""), isMobile)

  def enableHoverToolTip(elems: Seq[HTMLElement]): Unit = elems.foreach(e => enableHoverToolTip(jQuery(e)))

  def enableHoverToolTip(jQuery: JQuery, isMobile: Boolean = false): Unit = {
    jQuery.asInstanceOf[BootstrapJQuery].tooltip {
      val r = js.Dynamic.literal()
      r.trigger = isMobile.fold("focus", "hover")
      r.asInstanceOf[TooltipOptions]
    }
  }

  def hideAllToolTip(): Unit = {
    jQuery("""[data-toggle="tooltip"]""").asInstanceOf[BootstrapJQuery].tooltip("hide")
  }

  def display(elem: Element, message: String, displayTime: Int = 1000): Unit = {
    jQuery(elem).attr("data-original-title", message).asInstanceOf[BootstrapJQuery].tooltip("show")
    val f = () => jQuery(elem).asInstanceOf[BootstrapJQuery].tooltip("hide").attr("data-original-title", "")
    dom.window.setTimeout(f, displayTime)
  }
}