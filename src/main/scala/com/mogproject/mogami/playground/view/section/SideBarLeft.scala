package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
class SideBarLeft(private[this] var controlSection: ControlSection) extends SideBarLike {

  val EXPANDED_WIDTH: Int = SideBarLeft.EXPANDED_WIDTH

  override protected val outputClass: String = "sidebar-left"

  private[this] val longSelector: Div = div(cls := "long-select", controlSection.outputLongSelector).render

  override def content: Div = longSelector

  override lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => Controller.collapseSideBarLeft() },
    span(cls := "pull-right glyphicon glyphicon-minus"),
    marginLeft := 14.px, "Moves"
  ).render

  override lazy val titleCollapsed: Heading = h4(
    cls := "sidebar-heading",
    display := display.none.v,
    onclick := { () => Controller.expandSideBarLeft() },
    span(cls := "pull-right glyphicon glyphicon-plus"),
    raw("&nbsp;")
  ).render

  override def collapseSideBar(): Unit = if (!isCollapsed) {
    super.collapseSideBar()
    longSelector.style.marginLeft = (-EXPANDED_WIDTH).px
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    longSelector.style.marginLeft = "calc(50% - 84px)"
  }

  def updateControlSection(cs: ControlSection): Unit = {
    longSelector.innerHTML = ""
    longSelector.appendChild(cs.outputLongSelector)
    controlSection = cs
  }
}

object SideBarLeft {

  val EXPANDED_WIDTH: Int = 240

}