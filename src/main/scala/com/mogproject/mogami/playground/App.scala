package com.mogproject.mogami.playground

import scala.scalajs.js.JSApp
import com.mogproject.mogami.playground.controller.{Arguments, Controller}
import org.scalajs.dom

/**
  * entry point
  */
object App extends JSApp {
  def main(): Unit = {
    Controller.initialize(dom.document.getElementById("app"), Arguments().loadLocalStorage().parseQueryString(dom.window.location.search))
  }
}
