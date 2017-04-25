package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.{BranchNo, Game}
import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.parts.control.{CommentButton, ControlBar}
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  *
  */
case class ControlSection(canvasWidth: Int, isMobile: Boolean) extends Section {

  private[this] lazy val controlBar = ControlBar(canvasWidth)
  private[this] lazy val commentButton = CommentButton(isMobile)


  override def initialize(): Unit = {
    controlBar.initialize()
  }

  override val output: Div = div(
    paddingTop := "5px",
    controlBar.output,
    commentButton.output
  ).render

  def outputLongSelector: HTMLSelectElement = controlBar.outputLongSelector

  //
  // action facades
  //
  def updateLabels(backwardEnabled: Boolean, forwardEnabled: Boolean): Unit = controlBar.updateLabels(backwardEnabled, forwardEnabled)

  def getMaxRecordIndex: Int = controlBar.getMaxRecordIndex

  def getSelectedIndex: Int = controlBar.getSelectedIndex

  def getRecordIndex(index: Int): Int = controlBar.getRecordIndex(index)

  def updateRecordIndex(index: Int): Unit = controlBar.updateRecordIndex(index)

  def updateRecordContent(game: Game, branchNo: BranchNo, lng: Language): Unit = controlBar.updateRecordContent(game, branchNo, lng)

  def updateComment(text: String): Unit = commentButton.updateComment(text)

  override def show(): Unit = {
    output.style.display = display.block.v
    outputLongSelector.style.visibility = visibility.visible.v
  }

  override def hide(): Unit = {
    output.style.display = display.none.v
    outputLongSelector.style.visibility = visibility.hidden.v
  }
}
