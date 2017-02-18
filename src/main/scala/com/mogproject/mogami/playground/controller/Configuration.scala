package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}

/**
  *
  */
case class Configuration(screenWidth: Double = 375.0,
                         lang: Language = Japanese,
                         flip: Boolean = false,
                         baseUrl: String = "",
                         action: Action = PlayAction
                        ) {
  val layout: Layout = screenWidth match {
    case x if x >= 1024.0 => Layout(400)
    case x if x >= 400.0 => Layout(375)
    case x if x >= 375.0 => Layout(336)
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
