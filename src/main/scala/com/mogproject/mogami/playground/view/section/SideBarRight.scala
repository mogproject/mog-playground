package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
object SideBarRight extends SideBarLike {

  val EXPANDED_WIDTH: Int = 460

  override protected val outputClass: String = "sidebar-right"

  override lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => Controller.collapseSideBarRight() },
    span(cls := "glyphicon glyphicon-minus"),
    span(paddingLeft := 14.px, "Menu")
  ).render

  override lazy val titleCollapsed: Heading = h4(
    cls := "sidebar-heading",
    display := display.none.v,
    onclick := { () => Controller.expandSideBarRight() },
    span(cls := "glyphicon glyphicon-plus")
  ).render

  override def content: Div = div(
    titleExpanded,
    titleCollapsed,
    MenuPane.output
  ).render

  override def collapseSideBar(): Unit = if (!isCollapsed) {
    super.collapseSideBar()
    MenuPane.collapseMenu()
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    MenuPane.expandMenu()
  }
}
