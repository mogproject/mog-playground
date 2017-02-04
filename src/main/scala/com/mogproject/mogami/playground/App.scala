package com.mogproject.mogami.playground

import scala.scalajs.js.JSApp
import com.mogproject.mogami.playground.controller.{Arguments, Configuration, Controller}
import org.scalajs.dom

/**
  * entry point
  */
object App extends JSApp {
  def main(): Unit = {
    val baseUrl = s"${dom.window.location.protocol}//${dom.window.location.host}${dom.window.location.pathname}"
    val elem = dom.document.getElementById("app")
    val config = Configuration(screenWidth = dom.window.screen.width, baseUrl = baseUrl)
    val args = Arguments(config = config).parseQueryString(dom.window.location.search)

    Controller.initialize(elem, args)
  }
}
