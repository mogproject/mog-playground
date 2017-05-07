package com.mogproject.mogami.playground.view.parts.language

import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.parts.common.ButtonLike
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
sealed trait LanguageSelector extends ButtonLike[Language, Anchor, Div] {
  protected def labelString: String

  override protected val keys = Seq(Japanese, English)

  override protected val labels = Map(
    English -> Seq("Japanese", "English")
  )

  override protected def generateInput(key: Language): Anchor = a(cls := "btn btn-sm btn-primary").render

  override val output: Div = div(cls := "form-group",
    marginBottom := 3,
    div(cls := "row",
      div(cls := "col-xs-4 small-padding", label(marginTop := 6, labelString)),
      div(cls := "col-xs-8",
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

object MessageLanguageSelector extends LanguageSelector {
  override lazy val labelString = "Messages"

  override protected def invoke(key: Language): Unit = Controller.setMessageLanguage(key)
}

object RecordLanguageSelector extends LanguageSelector {
  override lazy val labelString = "Record"

  override protected def invoke(key: Language): Unit = Controller.setRecordLanguage(key)
}

object PieceLanguageSelector extends LanguageSelector {
  override lazy val labelString = "Pieces"

  override protected def invoke(key: Language): Unit = Controller.setPieceLanguage(key)
}