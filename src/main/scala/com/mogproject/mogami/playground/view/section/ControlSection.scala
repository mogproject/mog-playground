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
case class ControlSection(canvasWidth: Int, isMobile: Boolean, isSmall: Boolean) extends Section {

  private[this] val sectionWidth = math.max(300, canvasWidth)

  private[this] lazy val controlBar = ControlBar(sectionWidth, isSmall = isSmall)
  private[this] lazy val commentButton = CommentButton(isDisplayOnly = isMobile, isModal = false)

  override def initialize(): Unit = {
    controlBar.initialize()
  }

  override lazy val output: Div = div(
    width := sectionWidth,
    paddingTop := "5px",
    controlBar.output,
    commentButton.output
  ).render

  lazy val outputControlBar: Div = div(
    cls := "center-block",
    width := sectionWidth,
    paddingTop := "5px",
    controlBar.output
  ).render

  def outputComment: Div = commentButton.output

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

  def getComment: String = commentButton.getComment

  override def show(): Unit = {
    output.style.display = display.block.v
    outputLongSelector.style.visibility = visibility.visible.v
  }

  override def hide(): Unit = {
    output.style.display = display.none.v
    outputLongSelector.style.visibility = visibility.hidden.v
  }
}
