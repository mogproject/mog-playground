package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
trait SideBarLike {
  def EXPANDED_WIDTH: Int

  val COLLAPSED_WIDTH: Int = 60

  protected def outputClass: String

  private[this] var isCollapsedValue = false

  def content: Div

  def titleExpanded: Heading

  def titleCollapsed: Heading

  lazy val output: Div = div(cls := "sidebar " + outputClass,
    width := EXPANDED_WIDTH,
    div(
      titleExpanded,
      titleCollapsed,
      content
    )
  ).render

  def collapseSideBar(): Unit = if (!isCollapsedValue) {
    output.style.width = COLLAPSED_WIDTH.px
    titleExpanded.style.display = display.none.v
    titleCollapsed.style.display = display.block.v
    isCollapsedValue = true
  }

  def expandSideBar(): Unit = if (isCollapsedValue) {
    output.style.width = EXPANDED_WIDTH.px
    titleCollapsed.style.display = display.none.v
    titleExpanded.style.display = display.block.v
    isCollapsedValue = false
  }

  def isCollapsed: Boolean = isCollapsedValue

  def currentWidth: Int = isCollapsed.fold(COLLAPSED_WIDTH, EXPANDED_WIDTH)
}
