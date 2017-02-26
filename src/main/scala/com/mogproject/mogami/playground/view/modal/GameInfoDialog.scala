package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.GameInfo
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.html.{Div, Input}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class GameInfoDialog(config: Configuration, gameInfo: GameInfo) {

  private[this] val title = config.messageLang match {
    case Japanese => "対局情報"
    case English => "Game Information"
  }

  private[this] val nameLabel = config.messageLang match {
    case Japanese => "対局者名"
    case English => "Player Names"
  }

  private[this] val defaultNames: Map[Player, String] = config.recordLang match {
    case Japanese => Map(BLACK -> "先手", WHITE -> "後手")
    case English => Map(BLACK -> "Black", WHITE -> "White")
  }

  private[this] val tagNames: Map[Player, Symbol] = Map(BLACK -> 'blackName, WHITE -> 'whiteName)

  private[this] val inputNames: Map[Player, Input] = List(BLACK, WHITE).map { p =>
    p -> input(
      tpe := "text",
      cls := "form-control",
      maxlength := 12,
      onfocus := { () => inputNames(p).select() },
      value := gameInfo.tags.getOrElse(tagNames(p), defaultNames(p))
    ).render
  }.toMap

  private[this] def getGameInfo: GameInfo =
    gameInfo.copy(tags = tagNames.map { case (p, t) => t -> inputNames(p).value })

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog", data("backdrop") := "static",
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", float := "left", title),
            button(tpe := "button", cls := "close", data("dismiss") := "modal", aria.label := "Close",
              span(aria.hidden := true, raw("&times;"))
            )
          ),

          form(
            // body
            div(cls := "modal-body",
              label(nameLabel),

              div(cls := "row",
                marginBottom := 3,
                div(cls := "col-xs-4 small-padding", textAlign := "right", marginTop := 6, label("☗")),
                div(cls := "col-xs-8", inputNames(BLACK))
              ),
              div(cls := "row",
                div(cls := "col-xs-4 small-padding", textAlign := "right", marginTop := 6, label("☖")),
                div(cls := "col-xs-8", inputNames(WHITE))
              )
            ),

            // footer
            div(cls := "modal-footer",
              div(cls := "row",
                div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
                  button(
                    tpe := "submit", cls := "btn btn-default btn-block", data("dismiss") := "modal",
                    onclick := { () => Controller.setGameInfo(getGameInfo) },
                    "OK"
                  )
                )
              )
            )
          )
        )
      )
    ).render

  def show(): Unit = {
    val dialog = jQuery(elem)
    dialog.on("show.bs.modal", () ⇒ {
      // todo: this doesn't work
      inputNames(BLACK).focus()
    })
    dialog.on("hidden.bs.modal", () ⇒ {
      // Remove from DOM
      dialog.remove()
    })
    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }
}
