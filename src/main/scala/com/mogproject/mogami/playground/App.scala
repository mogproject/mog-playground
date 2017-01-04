package com.mogproject.mogami.playground

import scala.scalajs.js.JSApp
import com.mogproject.mogami.playground.controller.{Arguments, Controller}
import org.scalajs.dom

/**
  * entry point
  */
object App extends JSApp {
  def main(): Unit = {
    val elem = dom.document.getElementById("app")
    val args = Arguments().parseQueryString(dom.window.location.search)
    Controller.initialize(elem, args)
  }
}
