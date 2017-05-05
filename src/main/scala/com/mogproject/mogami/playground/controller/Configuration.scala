package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.api.MobileScreen
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import com.mogproject.mogami.playground.view.renderer.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  *
  */
case class Configuration(baseUrl: String = Configuration.defaultBaseUrl,
                         screenWidth: Double = Configuration.defaultScreenWidth,
                         screenHeight: Double = Configuration.defaultScreenHeight,
                         isMobile: Boolean = Configuration.defaultIsMobile,
                         isLandscape: Boolean = Configuration.defaultIsLandscape,
                         canvasWidth: Int = Configuration.defaultCanvasWidth,
                         messageLang: Language = Configuration.browserLanguage,
                         recordLang: Language = Configuration.browserLanguage,
                         pieceLang: Language = Japanese,
                         flip: FlipType = FlipDisabled
                        ) {
  def toQueryParameters: List[String] = {
    type Parser = List[String] => List[String]

    val parseMessageLang: Parser = xs => messageLang match {
      case Configuration.browserLanguage => xs
      case Japanese => "mlang=ja" :: xs
      case English => "mlang=en" :: xs
    }

    val parseRecordLang: Parser = xs => recordLang match {
      case Configuration.browserLanguage => xs
      case Japanese => "rlang=ja" :: xs
      case English => "rlang=en" :: xs
    }

    val parsePieceLang: Parser = xs => pieceLang match {
      case Japanese => xs
      case English => "plang=en" :: xs
    }

    val parseFlip: Parser = xs => flip match {
      case FlipDisabled => xs
      case FlipEnabled => "flip=true" :: xs
      case DoubleBoard => "flip=double" :: xs
    }

    (parseMessageLang andThen parseRecordLang andThen parsePieceLang andThen parseFlip) (List.empty)
  }

  def updateOrientation(newIsLandScape: Boolean): Configuration = {
    val newCanvasWidth = if (newIsLandScape) 200 else Configuration.defaultCanvasWidth
    this.copy(isLandscape = newIsLandScape, canvasWidth = newCanvasWidth)
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

  lazy val defaultScreenWidth: Double = dom.window.screen.width

  lazy val defaultScreenHeight: Double = dom.window.screen.height

  lazy val defaultIsMobile: Boolean = defaultScreenWidth < 768

  lazy val defaultIsLandscape: Boolean = MobileScreen.isLandscape

  lazy val defaultCanvasWidth: Int = defaultScreenWidth match {
    case x if x >= 1024.0 => 400
    case x if x >= 400.0 => 375
    case x if x >= 375.0 => 336
    case _ => 300
  }
}