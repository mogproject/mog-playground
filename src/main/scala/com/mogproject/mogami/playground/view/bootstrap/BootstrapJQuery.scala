package com.mogproject.mogami.playground.view.bootstrap

import org.scalajs.jquery.JQuery

import scala.scalajs.js

@js.native
trait BootstrapJQuery extends JQuery {
  def modal(action: String): BootstrapJQuery = js.native

  def modal(options: js.Any): BootstrapJQuery = js.native

  def tooltip(options: js.Any = ???): BootstrapJQuery = js.native
}

@js.native
trait TooltipOptions extends js.Object {
  var animation: Boolean = js.native
  var container: js.Any = js.native
  var delay: js.Any = js.native
  var html: Boolean = js.native
  var placement: js.Any = js.native
  var selector: js.Any = js.native
  var template: String = js.native
  var title: js.Any = js.native
  var trigger: String = js.native
  var viewport: js.Any = js.native
}