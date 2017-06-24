package com.mogproject.mogami.playground.view.parts.common

import com.mogproject.mogami.playground.controller.Language
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
case class RadioButton[Key](keys: Seq[Key],
                            labels: Map[Language, Seq[String]],
                            buttonClasses: Seq[String] = Seq("btn-sm"),
                            buttonGroupClasses: Seq[String] = Seq("btn-group-justified"),
                            onClick: Key => Unit = { _: Key => {} },
                            tooltip: Option[String] = None
                           ) extends ButtonLike[Key, Anchor, Div] {

  override protected def generateInput(key: Key): Anchor = a(
    cls := ("btn" :: "btn-primary" :: buttonClasses.toList).mkString(" ")
  ).render

  private[this] def inputs: Seq[Anchor] = keys.map(inputMap)

  override val output: Div = div(
    cls := "input-group",
    tooltip.map { s => Seq(data("toggle") := "tooltip", data("placement") := "bottom", data("original-title") := s) },
    div(cls := ("btn-group" :: buttonGroupClasses.toList).mkString(" "),
      inputs
    )
  ).render

  override def invoke(key: Key): Unit = {
    updateValue(key)
    onClick(key)
  }

  def initialize(defaultValue: Key, defaultLanguage: Language): Unit = {
    super.initialize()
    updateValue(defaultValue)
    updateLabel(defaultLanguage)
  }
}
