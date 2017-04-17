package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  *
  */
case class Configuration(screenWidth: Double = 375.0,
                         layoutSize: Int = 0,
                         messageLang: Language = Configuration.browserLanguage,
                         recordLang: Language = Japanese,
                         pieceLang: Language = Japanese,
                         flip: Boolean = false,
                         baseUrl: String = ""
                        ) {
  val layout: Layout = {
    val isMobile: Boolean = screenWidth < 768

    if (layoutSize > 0)
      Layout(layoutSize, isMobile)
    else
      screenWidth match {
        case x if x >= 1024.0 => Layout(400, isMobile)
        case x if x >= 400.0 => Layout(375, isMobile)
        case x if x >= 375.0 => Layout(336, isMobile)
        case _ => Layout(300, isMobile)
      }
  }

  lazy val pieceRenderer: PieceRenderer = pieceLang match {
    case Japanese => SimpleJapanesePieceRenderer(layout)
    case English => EnglishPieceRenderer(layout)
  }

  def toQueryParameters: List[String] = {
    type Parser = List[String] => List[String]

    val parseMessageLang: Parser = xs => messageLang match {
      case Configuration.browserLanguage => xs
      case Japanese => "mlang=ja" :: xs
      case English => "mlang=en" :: xs
    }

    val parseRecordLang: Parser = xs => recordLang match {
      case Japanese => xs
      case English => "rlang=en" :: xs
    }

    val parsePieceLang: Parser = xs => pieceLang match {
      case Japanese => xs
      case English => "plang=en" :: xs
    }

    val parseFlip: Parser = xs => flip.fold("flip=true" :: xs, xs)

    (parseMessageLang andThen parseRecordLang andThen parsePieceLang andThen parseFlip) (List.empty)
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
}