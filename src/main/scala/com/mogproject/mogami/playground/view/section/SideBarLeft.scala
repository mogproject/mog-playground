package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.branch.BranchButton
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
class SideBarLeft(private[this] var controlSection: ControlSection) extends SideBarLike {

  override val EXPANDED_WIDTH: Int = SideBarLeft.EXPANDED_WIDTH

  override protected val outputClass: String = "sidebar-left"

  private[this] val longSelector: Div = div(cls := "long-select", controlSection.outputLongSelector).render

  override lazy val content: Div = div(
    marginLeft := SideBarLeft.EXPANDED_MARGIN,
    cls := "sidebar-left-content",
    longSelector,
    br(),
    BranchButton.outputCompact
  ).render

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
    content.style.marginLeft = (-EXPANDED_WIDTH).px
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    content.style.marginLeft = SideBarLeft.EXPANDED_MARGIN
  }

  def updateControlSection(cs: ControlSection): Unit = {
    longSelector.innerHTML = ""
    longSelector.appendChild(cs.outputLongSelector)
    controlSection = cs
  }
}

object SideBarLeft {

  val EXPANDED_WIDTH: Int = 240

  val EXPANDED_MARGIN: String = "calc(50% - 98px)"
}