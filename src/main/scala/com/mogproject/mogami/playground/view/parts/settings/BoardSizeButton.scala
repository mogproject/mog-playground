package com.mogproject.mogami.playground.view.parts.settings

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.common.DropdownMenu
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object BoardSizeButton {

  /**
    * definitions of board sizes
    */
  sealed abstract class PresetBoardSize(val width: Int, val label: String) {
    override def toString: String = s"${width} - ${label}"
  }

  case object LandscapeIPhone5 extends PresetBoardSize(194, "iPhone 5 (Landscape)")

  case object LandscapeIPhone6 extends PresetBoardSize(232, "iPhone 6 (Landscape)")

  case object Small extends PresetBoardSize(240, "Small")

  case object LandscapeIPhone6Plus extends PresetBoardSize(259, "iPhone 6 Plus (Landscape)")

  case object PortraitIPhone5 extends PresetBoardSize(310, "iPhone 5 (Portrait)")

  case object Medium extends PresetBoardSize(320, "Medium")

  case object PortraitIPhone6 extends PresetBoardSize(365, "iPhone 6 (Portrait)")

  case object Large extends PresetBoardSize(400, "Large")

  case object ExtraLarge extends PresetBoardSize(480, "Extra Large")


  private[this] lazy val sizeButton = DropdownMenu(
    Vector(LandscapeIPhone5, LandscapeIPhone6, Small, LandscapeIPhone6Plus, PortraitIPhone5, Medium, PortraitIPhone6, Large, ExtraLarge),
    7, "Board Size", _ => (), "btn-group"
  )

  private[this] val setButton = button(
    cls := "btn btn-default",
    onclick := { () => Controller.changeBoardSize(sizeButton.getValue) },
    "Set"
  ).render

  lazy val output: Div = div(
    cls := "btn-group", role := "group", sizeButton.output, setButton
  ).render

}
