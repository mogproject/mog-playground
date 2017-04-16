package com.mogproject.mogami.playground.view.parts.common

import org.scalajs.dom.html.Div
import com.mogproject.mogami.util.Implicits._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery

/**
  *
  */
case class AccordionMenu(ident: String, title: String, isExpanded: Boolean, isVisible: Boolean, content: TypedTag[Div]) {

  private[this] val glyphCls = Map(false -> "glyphicon-menu-right", true -> "glyphicon-menu-down")
  private[this] val panelCls = Map(false -> "panel-default", true -> "panel-info")

  // @note do not set a group
  // data("parent") := "#accordion"

  private[this] val glyph = span(cls := "glyphicon").render

  private[this] val mainElem: Div = div(
    id := s"collapse${ident}",
    cls := "panel-collapse collapse" + isExpanded.fold(" in", ""),
    role := "tabpanel",
    aria.labelledby := s"heading${ident}",
    div(
      cls := "panel-body",
      content
    )
  ).render

  val output: Div = div(
    cls := "panel",
    if (isVisible) "" else display := display.none.v,
    div(
      cls := "panel-heading",
      id := s"heading${ident}",
      role := "button",
      data("toggle") := "collapse",
      data("target") := s"#collapse${ident}",
      h4(cls := "panel-title",
        span(
          cls := "accordion-toggle",
          glyph,
          " ",
          title
        )
      )
    ),
    mainElem
  ).render

  def initialize(): Unit = {
    def f(b: Boolean): Unit = {
      output.classList.remove(panelCls(!b))
      output.classList.add(panelCls(b))
      glyph.classList.remove(glyphCls(!b))
      glyph.classList.add(glyphCls(b))
    }

    // set initial classes
    f(isExpanded)

    // set events
    jQuery(mainElem)
      .on("show.bs.collapse", () => f(true))
      .on("hide.bs.collapse", () => f(false))
  }
}
