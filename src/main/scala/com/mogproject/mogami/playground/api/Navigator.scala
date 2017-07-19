package com.mogproject.mogami.playground.api

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("Navigator")
class Navigator extends js.Object {
  @js.native
  def languages: js.UndefOr[js.Array[String]] = js.native

  @js.native
  def language: js.UndefOr[String] = js.native

  @js.native
  def userLanguage: js.UndefOr[String] = js.native

  @js.native
  def browserLanguage: js.UndefOr[String] = js.native
}
