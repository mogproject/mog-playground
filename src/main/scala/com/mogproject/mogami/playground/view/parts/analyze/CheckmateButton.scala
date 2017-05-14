package com.mogproject.mogami.playground.view.parts.analyze

import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.{Button, Div, Input}

import scala.util.Try
import scalatags.JsDom.all._

/**
  *
  */
object CheckmateButton {

  val DEFAULT_TIMEOUT = 5

  private[this] val timeoutInput: Input = input(
    tpe := "text",
    cls := "form-control",
    placeholder := DEFAULT_TIMEOUT,
    value := DEFAULT_TIMEOUT
  ).render

  private[this] val analyzeButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Analyze this position for checkmate",
    data("dismiss") := "modal",
    onclick := { () => disableAnalyzeButton(); Controller.analyzeCheckmate(validateTimeout()) },
    "Analyze"
  ).render


  private[this] lazy val solverMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

  private[this] def validateTimeout(): Int = {
    val n = Try(timeoutInput.value.toInt).getOrElse(DEFAULT_TIMEOUT)
    timeoutInput.value = n.toString
    n
  }

  lazy val output: Div = div(
    div(
      cls := "row",
      div(cls := "col-xs-6 col-sm-8 text-right",
        "Timeout"
      ),
      div(cls := "col-xs-6 col-sm-4",
        marginTop := -8,
        div(cls := "input-group",
          timeoutInput,
          span(cls := "input-group-addon", padding := 6, "sec")
        )
      )
    ),
    div(cls := "row",
      div(cls := "col-xs-4 col-sm-3",
        analyzeButton
      ),
      solverMessage
    )
  ).render


  //
  // messaging
  //
  def displayCheckmateMessage(message: String): Unit = {
    solverMessage.innerHTML = message.replace("\n", br().toString())
  }

  def clearCheckmateMessage(): Unit = {
    solverMessage.innerHTML = ""
  }

  def disableAnalyzeButton(): Unit = analyzeButton.disabled = true

  def enableAnalyzeButton(): Unit = analyzeButton.disabled = false
}
