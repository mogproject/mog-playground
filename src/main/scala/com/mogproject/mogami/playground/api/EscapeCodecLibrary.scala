package com.mogproject.mogami.playground.api

import scala.scalajs.js

/**
  * for ecl.js
  */

@js.native
@js.annotation.JSGlobalScope
object EscapeCodecLibrary extends js.Object {
  def GetEscapeCodeType(s: String): String = js.native

  def EscapeSJIS(s: String): String = js.native

  def UnescapeSJIS(s: String): String = js.native

  def EscapeEUCJP(s: String): String = js.native

  def UnescapeEUCJP(s: String): String = js.native

  def EscapeJIS7(s: String): String = js.native

  def UnescapeJIS7(s: String): String = js.native

  def EscapeJIS8(s: String): String = js.native

  def UnescapeJIS8(s: String): String = js.native

  def EscapeUnicode(s: String): String = js.native

  def UnescapeUnicode(s: String): String = js.native

  def EscapeUTF7(s: String): String = js.native

  def UnescapeUTF7(s: String): String = js.native

  def EscapeUTF8(s: String): String = js.native

  def UnescapeUTF8(s: String): String = js.native

  def EscapeUTF16LE(s: String): String = js.native

  def UnescapeUTF16LE(s: String): String = js.native

}
