package com.mogproject.mogami.playground.view.parts.common

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.section.SideBar
import org.scalajs.dom.html.Div
import com.mogproject.mogami.util.Implicits._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery


/**
  *
  */
case class AccordionMenu(ident: String, title: String, icon: String, isExpanded: Boolean, isVisible: Boolean, content: TypedTag[Div]) {

  private[this] val panelCls = Map(false -> "panel-default", true -> "panel-info")

  private[this] val glyph = span(cls := s"glyphicon glyphicon-${icon}").render

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

  private[this] val titleElem = span().render

  private[this] val titleElemHeading = h4(cls := "panel-title",
    span(
      cls := "accordion-toggle",
      glyph,
      titleElem
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
      data("parent") := "#accordion",
      titleElemHeading
    ),
    mainElem
  ).render

  def initialize(): Unit = {
    def f(b: Boolean): Unit = {
      output.classList.remove(panelCls(!b))
      output.classList.add(panelCls(b))
    }

    // set initial classes
    f(isExpanded)

    // set events
    jQuery(mainElem)
      .on("show.bs.collapse", { () => f(true); Controller.expandSideBar() })
      .on("hide.bs.collapse", () => f(false))

    expandTitle()
  }

  def collapseTitle(): Unit = {
    titleElem.style.paddingLeft = 0.px
    titleElem.innerHTML = ""
  }

  def expandTitle(): Unit = {
    titleElem.style.paddingLeft = 20.px
    titleElem.innerHTML = " " + title
  }

}
