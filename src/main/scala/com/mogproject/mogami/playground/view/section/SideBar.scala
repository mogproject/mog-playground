package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
object SideBar {
  private[this] var isCollapsed = false

  private[this] val EXPANDED_WIDTH = 460.px

  private[this] val COLLAPSED_WIDTH = 60.px

  val titleExpanded: Heading = h4(
    a(href := "#", onclick := { () => collapseSideBar() }, span(cls := "glyphicon glyphicon-minus")),
    span(paddingLeft := 14.px, "Menu")
  ).render

  val titleCollapsed: Heading = h4(
    display := display.none.v,
    a(href := "#", onclick := { () => expandSideBar() }, span(cls := "glyphicon glyphicon-plus"))
  ).render


  val output: Div = div(cls := "hidden-xs side-bar-col",
    float := float.right.v,
    width := EXPANDED_WIDTH,
    div(
      titleExpanded,
      titleCollapsed,
      MenuPane.output
    )
  ).render

  def collapseSideBar(): Unit = if (!isCollapsed) {
    output.style.width = COLLAPSED_WIDTH
    titleExpanded.style.display = display.none.v
    titleCollapsed.style.display = display.block.v
    MenuPane.collapseMenu()
    isCollapsed = true
  }

  def expandSideBar(): Unit = if (isCollapsed) {
    output.style.width = EXPANDED_WIDTH
    titleCollapsed.style.display = display.none.v
    titleExpanded.style.display = display.block.v
    MenuPane.expandMenu()
    isCollapsed = false
  }
}
