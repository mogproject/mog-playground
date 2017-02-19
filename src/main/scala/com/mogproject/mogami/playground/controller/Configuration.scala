package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
case class Configuration(screenWidth: Double = 375.0,
                         layoutSize: Int = 0,
                         lang: Language = Japanese,
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
        case _ => Layout(320, isMobile)
      }
  }

  lazy val pieceRenderer: PieceRenderer = lang match {
    case Japanese => SimpleJapanesePieceRenderer(layout)
    case English => EnglishPieceRenderer(layout)
  }

  def toQueryParameters: List[String] = {
    val parseLanguage = (xs: List[String]) => lang match {
      case Japanese => xs
      case English => "lang=en" :: xs
    }

    val parseFlip = (xs: List[String]) => flip.fold("flip=true" :: xs, xs)

    (parseLanguage andThen parseFlip) (List.empty)
  }

}
