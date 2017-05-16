package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.analyze.{CheckmateButton, PointCountButton}
import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object AnalyzeSection extends Section {
  override val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Analyze",
      "Analyze",
      "education",
      isExpanded = false,
      isVisible = true,
      div(
        label("Analyze for Checkmate"),
        CheckmateButton.output,
        br(),
        label("Count points"),
        PointCountButton.output
      )
    )
  )

}
