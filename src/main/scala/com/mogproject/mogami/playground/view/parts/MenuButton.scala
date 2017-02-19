package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{Controller, English, Language}
import org.scalajs.dom.html.{Button, LI}

import scalatags.JsDom.all._

/**
  *
  */
object MenuButton extends ButtonLike[Boolean, Button, LI] {
  override protected val keys = Seq(true)

  override protected val labels: Map[Language, Seq[String]] = Map(
    English -> Seq("")
  )

  override protected def generateInput(key: Boolean): Button = button(
    tpe := "button",
    cls := "btn btn-default thin-btn",
    "Menu ",
    span(cls := s"glyphicon glyphicon-menu-hamburger", aria.hidden := true)
  ).render

  override val output: LI = li(
    cls := "pull-right visible-xs visible-sm",
    div(cls := "input-group",
      inputs
    )
  ).render

  override def updateLabel(lang: Language): Unit = ???

  override def updateValue(newValue: Boolean): Unit = ???

  override protected def invoke(key: Boolean) = Controller.showMenu()
}
