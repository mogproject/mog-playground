package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}

/**
  *
  */
case class Configuration(screenWidth: Double = 375.0,
                         lang: Language = Japanese,
                         baseUrl: String = ""
                        ) {
  val layout: Layout = screenWidth match {
    case x if x >= 375.0 => Layout(375)
    case _ => Layout(320)
  }

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
