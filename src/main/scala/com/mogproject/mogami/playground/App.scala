package com.mogproject.mogami.playground

import scala.scalajs.js.JSApp
import com.mogproject.mogami.core._

object App extends JSApp {
  def main(): Unit = {
    val g = Game()

    println("Hello world!")
    println(g.toCsaString)
  }
}
