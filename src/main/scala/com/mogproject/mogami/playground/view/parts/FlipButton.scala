package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{Controller, English, Language}
import org.scalajs.dom.html.{Button, Div, LI}

import scalatags.JsDom.all._

/**
  *
  */
object FlipButton extends ButtonLike[Boolean, Button, Div] {
  override protected val keys = Seq(true)

  override protected val labels: Map[Language, Seq[String]] = Map(
    English -> Seq("")
  )

  override protected def generateInput(key: Boolean): Button = button(
    tpe := "button",
    cls := "btn btn-default thin-btn",
    "Flip ",
    span(cls := s"glyphicon glyphicon-retweet", aria.hidden := true)
  ).render

  override val output: Div = div(cls := "input-group",
    inputs
  ).render

  override def updateLabel(lang: Language): Unit = ???

  override def updateValue(newValue: Boolean): Unit = {
    if (newValue) {
      inputs.head.classList.remove("btn-default")
      inputs.head.classList.add("btn-primary")
    } else {
      inputs.head.classList.remove("btn-primary")
      inputs.head.classList.add("btn-default")
    }
  }

  override protected def invoke(key: Boolean) = Controller.toggleFlip()
}
