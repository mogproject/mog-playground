package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import org.scalajs.dom.html.{Anchor, LI}

import scalatags.JsDom.all._

/**
  *
  */
object LanguageSelector extends ButtonLike[Language, Anchor, LI] {

  override protected val keys = Seq(Japanese, English)

  override protected val labels = Map(
    English -> Seq("Japanese", "English")
  )

  override protected def generateInput(key: Language): Anchor = a(href := "#").render

  override protected def invoke(key: Language): Unit = Controller.setLanguage(key)

  private[this] val langLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false).render

  override val output: LI = li(cls := "dropdown pull-right",
    textAlign := "right",
    langLabel,
    ul(cls := "dropdown-menu",
      li(cls := "dropdown-header", "Language"),
      inputs.map(e => li(e))
    )
  ).render

  override def initialize(): Unit = {
    super.initialize()
    updateLabel(English)
  }

  override def updateValue(newValue: Language): Unit = {
    langLabel.innerHTML = newValue.label + span(cls := "caret").toString()
  }
}
