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
    val args = Arguments(config=Configuration(baseUrl=baseUrl)).parseQueryString(dom.window.location.search)

    Controller.initialize(elem, args)
  }
}
