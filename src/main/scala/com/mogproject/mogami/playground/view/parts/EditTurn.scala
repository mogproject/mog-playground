package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
object EditTurn extends EventManageable {
  private[this] val anchors: Map[Player, Anchor] = Map(
    BLACK -> a(cls := "btn btn-primary active").render,
    WHITE -> a(cls := "btn btn-primary notActive").render
  )

  val element: Div = div(cls := "form-group",
    label("Turn"),
    div(cls := "row",
      div(cls := "col-sm-8 col-md-8",
        div(cls := "input-group",
          div(id := "radioBtn", cls := "btn-group btn-group-justified",
            anchors(BLACK),
            anchors(WHITE)
          )
        )
      )
    )
  ).render

  def initialize(): Unit = {
    anchors.foreach { case (t, e) => setClickEvent(e, () => Controller.setEditTurn(t)) }
  }

  def updateLabel(lang: Language): Unit = lang match {
    case Japanese =>
      anchors(BLACK).innerHTML = "先手番"
      anchors(WHITE).innerHTML = "後手番"
    case English =>
      anchors(BLACK).innerHTML = "Black"
      anchors(WHITE).innerHTML = "White"
  }

  def updateValue(newValue: Player): Unit = {
    anchors(newValue).classList.remove("notActive")
    anchors(newValue).classList.add("active")
    anchors(!newValue).classList.remove("active")
    anchors(!newValue).classList.add("notActive")
  }
}
