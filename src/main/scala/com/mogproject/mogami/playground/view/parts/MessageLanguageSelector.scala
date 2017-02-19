package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
object MessageLanguageSelector extends ButtonLike[Language, Anchor, Div] {
  override protected val keys = Seq(Japanese, English)

  override protected val labels = Map(
    English -> Seq("Japanese", "English")
  )

  override protected def generateInput(key: Language): Anchor = a(cls := "btn btn-primary").render

  override protected def invoke(key: Language): Unit = Controller.setMessageLanguage(key)

  override val output: Div = div(cls := "form-group",
    label("Messages"),
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

  override def initialize(): Unit = {
    super.initialize()
    updateLabel(English)
  }
}
