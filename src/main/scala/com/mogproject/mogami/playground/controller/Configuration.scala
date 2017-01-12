package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.piece.{EnglishPieceRenderer, PieceRenderer, SimpleJapanesePieceRenderer}

/**
  *
  */
case class Configuration(layout: Layout = Layout(320, 480),
                         mode: Mode = Playing,
                         lang: Language = Japanese
                        ) {
  lazy val pieceRenderer: PieceRenderer = lang match {
    case Japanese => SimpleJapanesePieceRenderer(layout)
    case English => EnglishPieceRenderer(layout)
  }

  def toQueryParameters: List[String] = {
    (mode match {
      case Playing => List()
      case Viewing => List("mode=view")
      case Editing => List("mode=edit")
    }) ++ (lang match {
      case Japanese => List()
      case English => List("lang=en")
    })
  }

}
