package com.mogproject.mogami.playground.view.bootstrap

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.jquery.jQuery

import scala.scalajs.js

/**
  *
  */
object Tooltip {
  def enableHoverToolTip(layout: Layout): Unit = {
    jQuery("""[data-toggle="tooltip"]""").asInstanceOf[BootstrapJQuery].tooltip {
      val r = js.Dynamic.literal()
      r.trigger = layout.isMobile.fold("focus", "hover")
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
