package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
class SideBarLeft(private[this] var controlSection: ControlSection) {
  private[this] var isCollapsedValue = false

  private[this] val longSelector: Div = div(width := 168, marginLeft := "auto", marginRight := "auto", controlSection.outputLongSelector).render

  lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => Controller.collapseSideBarLeft() },
    span(cls := "pull-right glyphicon glyphicon-minus"),
    marginLeft := 14.px, "Moves"
  ).render

  lazy val titleCollapsed: Heading = h4(
    cls := "sidebar-heading",
    display := display.none.v,
    onclick := { () => Controller.expandSideBarLeft() },
    span(cls := "pull-right glyphicon glyphicon-plus")
  ).render

  lazy val output: Div = div(
    cls := "hidden-xs sidebar sidebar-left",
    width := SideBarLeft.EXPANDED_WIDTH,
    titleCollapsed,
    titleExpanded,
    longSelector
  ).render

  def collapseSideBar(): Unit = if (!isCollapsedValue) {
    output.style.width = SideBarLeft.COLLAPSED_WIDTH.px
    titleExpanded.style.display = display.none.v
    titleCollapsed.style.display = display.block.v
    longSelector.style.display = display.none.v
    isCollapsedValue = true
  }

  def expandSideBar(): Unit = if (isCollapsedValue) {
    output.style.width = SideBarLeft.EXPANDED_WIDTH.px
    titleCollapsed.style.display = display.none.v
    titleExpanded.style.display = display.block.v
    longSelector.style.display = display.block.v
    isCollapsedValue = false
  }

  def isCollapsed: Boolean = isCollapsedValue

  def currentWidth: Int = isCollapsed.fold(SideBarLeft.COLLAPSED_WIDTH, SideBarLeft.EXPANDED_WIDTH)

  def updateControlSection(cs: ControlSection): Unit = {
    longSelector.innerHTML = ""
    longSelector.appendChild(cs.outputLongSelector)
    controlSection = cs
  }
}

object SideBarLeft {

  val EXPANDED_WIDTH: Int = 240

  val COLLAPSED_WIDTH: Int = 60

}