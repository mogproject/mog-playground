package com.mogproject.mogami.playground.view.modal

import org.scalajs.jquery.JQuery

import scala.scalajs.js

@js.native
trait BootstrapJQuery extends JQuery {
  def modal(action: String): BootstrapJQuery = js.native

  def modal(options: js.Any): BootstrapJQuery = js.native
}
