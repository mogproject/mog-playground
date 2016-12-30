package com.mogproject.mogami.playground

import scala.scalajs.js.JSApp
import org.scalajs.dom.document
import com.mogproject.mogami._
import com.mogproject.mogami.playground.view.piece.SimpleJapanesePieceRenderer
import com.mogproject.mogami.playground.view.{Layout, Renderer}

/**
  * entry point
  */
object App extends JSApp {
  def main(): Unit = {
    val elem = document.getElementById("app")
    val layout = Layout(320, 480)
    val renderer = Renderer(elem, layout, SimpleJapanesePieceRenderer(layout))
    renderer.drawBoard()
    renderer.drawPieces(State.HIRATE)
  }
}
