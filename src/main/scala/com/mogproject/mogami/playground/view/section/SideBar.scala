package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
object SideBar {
  private[this] var isCollapsed = false

  val titleExpanded: Heading = h4(
    a(href := "#", onclick := { () => collapseSideBar() }, span(cls := "glyphicon glyphicon-minus")),
    " Menu"
  ).render

  val titleCollapsed: Heading = h4(
    display := display.none.v,
    a(href := "#", onclick := { () => expandSideBar() }, span(cls := "glyphicon glyphicon-plus"))
  ).render


  val output: Div = div(cls := "col-sm-5 col-sm-push-7 col-md-4 col-md-push-8 hidden-xs side-bar-col",
    div(
      titleExpanded,
      titleCollapsed,
      MenuPane.output
    )
  ).render

  def collapseSideBar(): Unit = {
    output.style.width = 80.px
    titleExpanded.style.display = display.none.v
    titleCollapsed.style.display = display.block.v
    MenuPane.collapseMenu()
    isCollapsed = true
  }

  def expandSideBar(): Unit = if (isCollapsed) {
    output.style.width = 460.px
    titleCollapsed.style.display = display.none.v
    titleExpanded.style.display = display.block.v
    MenuPane.expandMenu()
    isCollapsed = false
  }
}
