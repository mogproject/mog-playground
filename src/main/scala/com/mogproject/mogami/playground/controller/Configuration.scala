package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}

/**
  *
  */
case class Configuration(layout: Layout = Layout(320, 480),
                         lang: Language = Japanese,
                         baseUrl: String = ""
                        ) {
  lazy val pieceRenderer: PieceRenderer = lang match {
    case Japanese => SimpleJapanesePieceRenderer(layout)
    case English => EnglishPieceRenderer(layout)
  }

  def toQueryParameters: List[String] = {
    lang match {
      case Japanese => List()
      case English => List("lang=en")
    }
  }

}
