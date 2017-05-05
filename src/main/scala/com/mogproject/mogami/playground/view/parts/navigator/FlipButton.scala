package com.mogproject.mogami.playground.view.parts.navigator

import com.mogproject.mogami.playground.controller.{Controller, English, Language}
import com.mogproject.mogami.playground.view.parts.common.ButtonLike
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  *
  */
object FlipButton extends ButtonLike[FlipType, Button, Div] {
  override protected val keys = Seq(FlipEnabled)

  override protected val labels: Map[Language, Seq[String]] = Map(
    English -> Seq("")
  )

  override protected def generateInput(key: FlipType): Button = button(
    tpe := "button",
    cls := "btn btn-default thin-btn",
    "Flip ",
    span(cls := s"glyphicon glyphicon-retweet", aria.hidden := true)
  ).render

  override val output: Div = div(cls := "input-group",
    inputs
  ).render

  override def updateLabel(lang: Language): Unit = ???

  override def updateValue(newValue: FlipType): Unit = newValue match {
    case FlipEnabled =>
      inputs.head.disabled = false
      inputs.head.classList.remove("btn-default")
      inputs.head.classList.add("btn-primary")
    case FlipDisabled =>
      inputs.head.disabled = false
      inputs.head.classList.remove("btn-primary")
      inputs.head.classList.add("btn-default")
    case DoubleBoard =>
      inputs.head.classList.remove("btn-primary")
      inputs.head.classList.add("btn-default")
      inputs.head.disabled = true
  }

  override protected def invoke(key: FlipType) = Controller.toggleFlip()
}
