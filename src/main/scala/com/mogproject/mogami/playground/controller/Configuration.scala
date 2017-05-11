package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.api.MobileScreen
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import com.mogproject.mogami.playground.view.section.{SideBarLeft, SideBarRight}
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  *
  */
case class Configuration(baseUrl: String = Configuration.defaultBaseUrl,
                         isMobile: Boolean = Configuration.defaultIsMobile,
                         isLandscape: Boolean = Configuration.getIsLandscape,
                         canvasWidth: Int = Configuration.getDefaultCanvasWidth,
                         messageLang: Language = Configuration.browserLanguage,
                         recordLang: Language = Configuration.browserLanguage,
                         pieceLang: Language = Japanese,
                         flip: FlipType = FlipDisabled
                        ) {

  def toQueryParameters: List[String] = {
    type Parser = List[String] => List[String]

    // @note do not add lang parameters anymore

    val parseFlip: Parser = xs => flip match {
      case FlipEnabled => "flip=true" :: xs
      case _ => xs
    }

    parseFlip(List.empty)
  }

  def updateScreenSize(): Configuration = {
    this.copy(isLandscape = Configuration.getIsLandscape, canvasWidth = Configuration.getDefaultCanvasWidth)
  }

  def collapseByDefault: Boolean = !isMobile && Configuration.getClientWidth < canvasWidth + SideBarLeft.EXPANDED_WIDTH + SideBarRight.EXPANDED_WIDTH

  def loadLocalStorage(): Configuration = {
    val ls = LocalStorage.load()
    this.copy(
      canvasWidth = ls.canvasSize.getOrElse(canvasWidth),
      flip = ls.doubleBoardMode.contains(true).fold(DoubleBoard, flip),
      messageLang = ls.messageLang.getOrElse(messageLang),
      recordLang = ls.recordLang.getOrElse(recordLang),
      pieceLang = ls.pieceLang.getOrElse(pieceLang)
    )
  }
}

object Configuration {
  lazy val browserLanguage: Language = {
    def f(n: UndefOr[String]): Option[String] = n.toOption.flatMap(Option.apply)

    val nav = dom.window.navigator.asInstanceOf[com.mogproject.mogami.playground.api.Navigator]
    val firstLang = nav.languages.toOption.flatMap(_.headOption)
    val lang: Option[String] = (firstLang ++ f(nav.language) ++ f(nav.userLanguage) ++ f(nav.browserLanguage)).headOption

    lang.map(_.slice(0, 2).toLowerCase) match {
      case Some("ja") => Japanese
      case _ => English
    }
  }

  lazy val defaultBaseUrl = s"${dom.window.location.protocol}//${dom.window.location.host}${dom.window.location.pathname}"

  lazy val defaultIsMobile: Boolean = dom.window.screen.width < 768

  def getIsLandscape: Boolean = MobileScreen.isLandscape

  def getClientWidth: Double = dom.window.innerWidth

  def getClientHeight: Double = dom.window.innerHeight

  def getDefaultCanvasWidth: Int = getDefaultCanvasWidth(getClientWidth, getClientHeight, getIsLandscape)

  def getDefaultCanvasWidth(clientWidth: Double, clientHeight: Double, isLandscape: Boolean): Int = {
    math.max(100, math.min(math.min(clientWidth - 10, (clientHeight - isLandscape.fold(76, 60)) * 400 / 576).toInt, 400))
  }
}