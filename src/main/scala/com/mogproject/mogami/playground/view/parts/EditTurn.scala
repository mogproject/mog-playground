package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese}
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
object EditTurn extends ButtonLike[Player, Anchor, Div] {
  override protected val keys = Seq(BLACK, WHITE)

  override protected val labels = Map(
    Japanese -> Seq("先手番", "後手番"),
    English -> Seq("Black", "White")
  )

  override protected def generateInput(key: Player): Anchor = a(cls := "btn btn-primary").render

  override protected def invoke(key: Player): Unit = Controller.setEditTurn(key)

  override val output: Div = div(cls := "form-group",
    label("Turn"),
    div(cls := "row",
      div(cls := "col-sm-8 col-md-8",
        div(cls := "input-group",
          div(cls := "btn-group btn-group-justified",
            inputs
          )
        )
      )
    )
  ).render

}
