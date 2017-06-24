package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.{Controller, English}
import com.mogproject.mogami.playground.view.parts.common.{AccordionMenu, RadioButton}
import com.mogproject.mogami.playground.view.parts.settings.{BoardSizeButton, MessageLanguageSelector, PieceLanguageSelector, RecordLanguageSelector}

import scalatags.JsDom.all._

/**
  *
  */
object SettingsSection extends Section {
  private[this] lazy val doubleBoardButton: RadioButton[Boolean] = RadioButton(Seq(false, true), Map(English -> Seq("Off", "On")), onClick = Controller.setDoubleBoard)

  override def initialize(): Unit = {
    super.initialize()

    doubleBoardButton.initialize(false, English)

    MessageLanguageSelector.initialize()
    RecordLanguageSelector.initialize()
    PieceLanguageSelector.initialize()
  }

  override lazy val accordions: Seq[AccordionMenu] = Seq(AccordionMenu(
    "Settings",
    "Settings",
    "wrench",
    isExpanded = false,
    isVisible = true,
    div(
      div(
        marginBottom := 10.px,
        BoardSizeButton.output,
        label("Board Size")
      ),
      div(cls := "row",
        marginLeft := (-10).px,
        marginBottom := 10.px,
        div(cls := "col-xs-7 col-sm-9 small-padding", label(marginTop := 6, "Double Board Mode")),
        div(cls := "col-xs-5 col-sm-3", doubleBoardButton.output)
      ),
      MessageLanguageSelector.output,
      RecordLanguageSelector.output,
      PieceLanguageSelector.output,
      div(
        cls := "alert alert-success setting-alert",
        "These settings will be saved for your browser."
      )
    )
  ))

  def updateDoubleBoardButton(isDoubleBoard: Boolean): Unit = doubleBoardButton.updateValue(isDoubleBoard)
}