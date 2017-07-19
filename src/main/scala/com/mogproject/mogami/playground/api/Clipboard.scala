package com.mogproject.mogami.playground.api

import org.scalajs.dom.{Element, NodeListOf}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.|

@js.native
@JSGlobal("Clipboard")
class Clipboard(selector: String | Element | NodeListOf[Element] = js.native,
                options: Clipboard.Options = js.native) extends js.Object {

  def on(`type`: String, handler: js.Function): Clipboard = js.native

  def destroy(): Unit = js.native
}

@js.native
@JSGlobal("Clipboard")
object Clipboard extends js.Object {

  @js.native
  trait Options extends js.Object {
    var action: js.Function1[Element, String] = js.native
    var target: js.Function1[Element, Element] = js.native
    var text: js.Function1[Element, String] = js.native
  }

  @js.native
  trait Event extends js.Object {
    var action: String = js.native
    var text: String = js.native
    var trigger: Element = js.native

    def clearSelection(): Unit = js.native
  }

}
