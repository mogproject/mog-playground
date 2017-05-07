package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.{Controller, English}
import com.mogproject.mogami.playground.view.parts.common.{AccordionMenu, RadioButton}
import com.mogproject.mogami.playground.view.parts.settings.{MessageLanguageSelector, PieceLanguageSelector, RecordLanguageSelector}

import scalatags.JsDom.all._

/**
  *
  */
object SettingsSection extends Section {
  lazy val doubleBoardButton = RadioButton(Seq(false, true), Map(English -> Seq("Off", "On")), onClick = Controller.setDoubleBoard)

  override def initialize(): Unit = {
    super.initialize()
    doubleBoardButton.initialize()
    doubleBoardButton.updateValue(false)
    doubleBoardButton.updateLabel(English)

    MessageLanguageSelector.initialize()
    RecordLanguageSelector.initialize()
    PieceLanguageSelector.initialize()
  }

  override val accordions: Seq[AccordionMenu] = Seq(AccordionMenu(
    "Settings",
    "Settings",
    "wrench",
    false,
    true,
    div(
      div(cls := "row",
        marginBottom := 10.px,
        div(cls := "col-xs-8 col-lg-9 small-padding", label(marginTop := 6, "Double Board Mode")),
        div(cls := "col-xs-4 col-lg-3", doubleBoardButton.output)
      ),
      MessageLanguageSelector.output,
      RecordLanguageSelector.output,
      PieceLanguageSelector.output
    )
  ))

  def updateDoubleBoardButton(isDoubleBoard: Boolean): Unit = doubleBoardButton.updateValue(isDoubleBoard)
}
