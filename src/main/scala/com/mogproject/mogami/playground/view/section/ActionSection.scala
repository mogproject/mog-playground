package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.parts.action.ResignButton
import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object ActionSection extends Section {

  override def initialize(): Unit = {
    super.initialize()
  }

  override val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Action",
      "Action",
      isExpanded = false,
      isVisible = false,
      div(cls := "row",
        div(cls := "col-xs-6 col-lg-4",
          ResignButton.output
        )
      )
    )
  )

  def update(lang: Language, canResign: Boolean): Unit = {
    ResignButton.update(lang, canResign)
  }
}
