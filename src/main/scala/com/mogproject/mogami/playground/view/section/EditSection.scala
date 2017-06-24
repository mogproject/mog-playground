package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.common.AccordionMenu
import com.mogproject.mogami.playground.view.parts.edit.EditReset

import scalatags.JsDom.all._

/**
  *
  */
object EditSection extends Section {
  override def initialize(): Unit = {
    super.initialize()
    EditReset.initialize()
  }

  override lazy val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Reset",
      "Reset",
      "erase",
      isExpanded = false,
      isVisible = false,
      div(EditReset.output)
    )
  )

}
