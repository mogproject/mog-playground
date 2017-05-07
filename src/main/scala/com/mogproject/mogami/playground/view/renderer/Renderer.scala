package com.mogproject.mogami.playground.view.renderer

import com.mogproject.mogami.playground.api.Clipboard.Event
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.controller.mode.Mode
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.playground.view.modal._
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.DoubleBoard
import com.mogproject.mogami.{BranchNo, _}
import org.scalajs.dom.UIEvent
import org.scalajs.dom.html.Div

// todo: don't use parts directly but use only sections
import com.mogproject.mogami.playground.view.parts.edit.EditReset
import com.mogproject.mogami.playground.view.parts.settings.{MessageLanguageSelector, PieceLanguageSelector, RecordLanguageSelector}
import com.mogproject.mogami.playground.view.parts.manage.SaveLoadButton
import com.mogproject.mogami.playground.view.parts.navigator.FlipButton
import com.mogproject.mogami.playground.view.parts.share._
import com.mogproject.mogami.playground.view.section._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
class Renderer extends BoardRenderer {
  //
  // HTML elements
  //
  private[this] var controlSection: ControlSection = ControlSection(0, false, false)

  private[this] var mainPane: Div = div().render

  private[this] def createMainPane(canvasWidth: Int, numBoards: Int, isMobile: Boolean, isLandscape: Boolean) = div(
    div(cls := "navbar",
      tag("nav")(cls := "navbar navbar-default navbar-fixed-top", NavigatorSection.output)
    ),
    div(cls := "container-fluid",
      isMobile.fold(Seq(position := position.fixed.v, width := "100%", padding := 0), ""),
      div(cls := "row no-margin",
        // sidebar
        SideBar.output,

        // long selector
        div(
          cls := "hidden-xs hidden-sm sidebar sidebar-left",
          width := 240.px,
          h4(marginLeft := 14.px, "Moves"),
          div(width := 168.px, marginLeft := "auto", marginRight := "auto", controlSection.outputLongSelector)
        ),

        // main content
        div(
          paddingTop := 5, display := display.`inline-block`.v, width := "calc(100% - 240px - 460px)",
          (isMobile, isLandscape, numBoards == 2) match {
            case (true, true, true) => createMobileLandscapeMainDouble(canvasWidth)
            case (true, true, false) => createMobileLandscapeMain(canvasWidth)
            case (true, false, _) => createMobilePortraitMain(canvasWidth)
            case _ => createPCPortraitMain(canvasWidth, numBoards)
          }
        )
      ),
      hr(),
      small(p(textAlign := "right", marginRight := 20, "Shogi Playground © 2017 ", a(href := "http://mogproject.com", target := "_blank", "mogproject")))
    )
  ).render

  // todo: create a class
  private[this] def createPCPortraitMain(canvasWidth: Int, numBoards: Int) = div(
    div(cls := "container-fluid center-block",
      // boards
      div(
        width := canvasWidth * numBoards + 30,
        overflowX := overflow.auto.v,
        marginLeft := "auto",
        marginRight := "auto",
        paddingLeft := 15.px, paddingRight := 15.px, paddingBottom := 15.px,
        if (numBoards == 2) {
          div(cls := "row",
            div(cls := "col-xs-6",
              boardRendererElement1,
              controlSection.output
            ),
            div(cls := "col-xs-6",
              boardRendererElement2
            )
          )
        } else {
          div(
            boardRendererElement1,
            controlSection.output
          )
        }
      )
    )
  )

  private[this] def createMobilePortraitMain(canvasWidth: Int) = div(
    cls := "container-fluid", width := canvasWidth, padding := 0,
    boardRendererElement1,
    controlSection.output
  ).render

  private[this] def createMobileLandscapeMain(canvasWidth: Int) = div(
    cls := "container-fluid", width := canvasWidth * 2 + 30, padding := 0,
    div(cls := "row",
      div(cls := "col-xs-6",
        boardRendererElement1
      ),
      div(cls := "col-xs-6",
        controlSection.outputComment
      )
    ),
    div(cls := "row",
      div(cls := "col-xs-12",
        controlSection.outputControlBar
      )
    )
  ).render

  private[this] def createMobileLandscapeMainDouble(canvasWidth: Int) = div(
    cls := "container-fluid", width := canvasWidth * 2 + 30, padding := 0,
    div(cls := "row",
      div(cls := "col-xs-6",
        boardRendererElement1
      ),
      div(cls := "col-xs-6",
        boardRendererElement2
      )
    ),
    div(cls := "row",
      div(cls := "col-xs-12",
        controlSection.outputControlBar
      )
    )
  ).render

  def initialize(elem: Element, config: Configuration): Unit = {
    // create elements
    controlSection = ControlSection(config.canvasWidth, config.isMobile, config.isMobile && config.isLandscape)
    mainPane = createMainPane(config.canvasWidth, (config.flip == DoubleBoard).fold(2, 1), config.isMobile, config.isLandscape)
    elem.appendChild(mainPane)

    initializeBoardRenderer(config)
    NavigatorSection.initialize()
    controlSection.initialize()

    MenuPane.initialize()

    // initialize clipboard.js
    val cp = new Clipboard(".btn")
    cp.on("success", (e: Event) => Tooltip.display(e.trigger, "Copied!"))
    cp.on("error", (e: Event) => Tooltip.display(e.trigger, "Failed!"))

    // initialize tooltips
    Tooltip.enableHoverToolTip(config.isMobile)

    // add events
    dom.window.addEventListener("orientationchange", (_: UIEvent) => Controller.changeScreenSize())

    // initialize settings
    SettingsSection.updateDoubleBoardButton(config.flip == DoubleBoard)
  }

  /**
    * Display
    */
  def showEditSection(): Unit = EditSection.show()

  def hideEditSection(): Unit = EditSection.hide()

  def showControlSection(): Unit = List(controlSection, GameMenuSection, GameHelpSection).foreach(_.show())

  def hideControlSection(): Unit = List(controlSection, GameMenuSection, GameHelpSection).foreach(_.hide())

  def askPromote(config: Configuration, piece: Piece, callbackUnpromote: () => Unit, callbackPromote: () => Unit): Unit = {
    PromotionDialog(config, getPieceRenderer, piece, callbackUnpromote, callbackPromote).show()
  }

  def askConfirm(lang: Language, callback: () => Unit): Unit = {
    val s = lang match {
      case Japanese => p("棋譜の情報が失われますが、よろしいですか?")
      case English => p("The record will be discarded. Are you sure?")
    }
    YesNoDialog(lang, s, callback).show()
  }

  def askDeleteBranch(lang: Language, branchNo: BranchNo, callback: () => Unit): Unit = {
    val s = lang match {
      case Japanese => p(s"現在の変化 (Branch#${branchNo}) が削除されます。コメントも失われますが、よろしいですか?")
      case English => p(s"Branch#${branchNo} will be deleted. Comments on this branch will also be removed. Are you sure?")
    }
    YesNoDialog(lang, s, callback).show()
  }

  def alertEditedState(msg: String, lang: Language): Unit = {
    val s = lang match {
      case Japanese => p("不正な局面です。", br, s"(${msg})")
      case English => p("Invalid state.", br, s"(${msg})")
    }
    AlertDialog(lang, s).show()
  }

  def showMenuModal(): Unit = MenuDialog.show()

  def hideMenuModal(timeout: Double): Unit = dom.window.setTimeout({ () => MenuDialog.hide() }, timeout)

  def showGameInfoModal(config: Configuration, gameInfo: GameInfo): Unit = GameInfoDialog(config, gameInfo).show()

  // share section
  def updateSnapshotUrl(url: String): Unit = SnapshotCopyButton.updateValue(url)

  def getSnapshotUrl: String = SnapshotCopyButton.getValue

  def updateSnapshotShortUrl(url: String, completed: Boolean): Unit = SnapshotShortenButton.updateValue(url, completed)

  def updateRecordUrl(url: String): Unit = RecordCopyButton.updateValue(url)

  def getRecordUrl: String = RecordCopyButton.getValue

  def updateRecordShortUrl(url: String, completed: Boolean): Unit = RecordShortenButton.updateValue(url, completed)

  def updateImageLinkUrl(url: String): Unit = ImageLinkButton.updateValue(url)

  def updateSfenString(sfen: String): Unit = SfenStringCopyButton.updateValue(sfen)

  def updateCommentOmissionWarning(displayWarning: Boolean): Unit = GameMenuSection.updateCommentOmissionWarning(displayWarning)

  // navigator section
  def updateMode(mode: Mode): Unit = NavigatorSection.updateMode(mode)

  def updateFlip(config: Configuration): Unit = FlipButton.updateValue(config.flip)

  // record
  def updateRecordContent(game: Game, branchNo: BranchNo, lng: Language): Unit = controlSection.updateRecordContent(game, branchNo, lng)

  def updateRecordIndex(index: Int): Unit = controlSection.updateRecordIndex(index)

  def getRecordIndex(index: Int): Int = controlSection.getRecordIndex(index)

  def getMaxRecordIndex: Int = controlSection.getMaxRecordIndex

  def getSelectedIndex: Int = controlSection.getSelectedIndex

  // control section
  def updateControlBar(backwardEnabled: Boolean, forwardEnabled: Boolean): Unit =
    controlSection.updateLabels(backwardEnabled: Boolean, forwardEnabled: Boolean)

  def updateEditResetLabel(lang: Language): Unit = EditReset.updateLabel(lang)

  def updateComment(text: String): Unit = controlSection.updateComment(text)

  def showCommentModal(config: Configuration): Unit = CommentDialog(config, controlSection.getComment).show()

  // languages
  def updateMessageLang(lang: Language): Unit = MessageLanguageSelector.updateValue(lang)

  def updateRecordLang(lang: Language): Unit = RecordLanguageSelector.updateValue(lang)

  def updatePieceLang(lang: Language): Unit = PieceLanguageSelector.updateValue(lang)

  // tooltip messages
  def displayFileLoadMessage(message: String): Unit = SaveLoadButton.displayFileLoadMessage(message)

  def displayFileLoadTooltip(message: String): Unit = SaveLoadButton.displayFileLoadTooltip(message)

  def displayTextLoadMessage(message: String): Unit = SaveLoadButton.displayTextLoadMessage(message)

  def displayTextLoadTooltip(message: String): Unit = SaveLoadButton.displayTextLoadTooltip(message)

  // branch section
  def updateBranchButtons(game: Game, gamePosition: GamePosition, language: Language): Unit = GameMenuSection.updateBranchButtons(game, gamePosition, language)

  def showBranchEditMenu(): Unit = GameMenuSection.showBranchEditMenu()

  def hideBranchEditMenu(): Unit = GameMenuSection.hideBranchEditMenu()

  def getIsNewBranchMode: Boolean = GameMenuSection.getIsNewBranchMode

  // action section
  def showActionSection(): Unit = ActionSection.show()

  def hideActionSection(): Unit = ActionSection.hide()

  def updateActionSection(lang: Language, canResign: Boolean): Unit = {
    // @note setTimeout is necessary for data-dismiss modal closing
    dom.window.setTimeout(() => ActionSection.update(lang, canResign), 0)
  }
}
