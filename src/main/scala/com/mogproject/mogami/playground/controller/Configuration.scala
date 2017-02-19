package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
case class Configuration(screenWidth: Double = 375.0,
                         layoutSize: Int = 0,
                         messageLang: Language = Japanese,
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
      case Japanese => xs
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

    (parseMessageLang andThen parseRecordLang andThen parsePieceLang andThen parseFlip)(List.empty)
  }

}
