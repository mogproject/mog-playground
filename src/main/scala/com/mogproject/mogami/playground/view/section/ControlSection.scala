package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{BranchNo, Game}
import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.parts.control.{CommentButton, ControlBar}
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  *
  */
case class ControlSection(canvasWidth: Int, isMobile: Boolean, isMobileLandscape: Boolean) extends Section {

  private[this] val sectionWidth = math.max(300, canvasWidth)

  private[this] lazy val controlBar = ControlBar(sectionWidth, isSmall = isMobileLandscape)
  private[this] lazy val commentButton = CommentButton(isDisplayOnly = isMobile, isModal = false)

  override def initialize(): Unit = {
    controlBar.initialize()
  }

  // @note NO output

  lazy val outputControlBar: Div = div(
    cls := "center-block",
    width := sectionWidth,
    paddingTop := "5px",
    controlBar.output
  ).render

  lazy val outputComment: Div = div(
    cls := "center-block",
    width := isMobileLandscape.fold(canvasWidth, sectionWidth),
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

  def getComment: String = commentButton.getComment

  def clearHoldEvent(): Unit = controlBar.clearHoldEvent()

  def focusLongSelector(): Unit = if (!isMobile) controlBar.focusLongSelector()

  override def show(): Unit = {
    outputControlBar.style.display = display.block.v
    outputComment.style.display = display.block.v
    outputLongSelector.style.display = display.block.v
  }

  override def hide(): Unit = {
    outputControlBar.style.display = display.none.v
    outputComment.style.display = display.none.v
    outputLongSelector.style.display = display.none.v
  }
}
