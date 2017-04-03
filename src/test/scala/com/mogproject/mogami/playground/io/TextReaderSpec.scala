package com.mogproject.mogami.playground.io

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class TextReaderSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {

  val utf8Text = TextReader.encodeCharArray("あいうえお", "UTF8")
  val sjisText = TextReader.encodeCharArray("あいうえお", "SJIS")

  "TextReader#decodeString" must "decode strings" in {
    utf8Text.toList == sjisText.toList mustBe false

    TextReader.decodeCharArray(utf8Text) mustBe "あいうえお"
    TextReader.decodeCharArray(utf8Text, Some("UTF8")) mustBe "あいうえお"
    TextReader.decodeCharArray(sjisText) mustBe "あいうえお"
    TextReader.decodeCharArray(sjisText, Some("SJIS")) mustBe "あいうえお"
  }
  it must "fail when encoding does not match" in {
    TextReader.decodeCharArray(utf8Text, Some("SJIS")) == "あいうえお" mustBe false
    TextReader.decodeCharArray(sjisText, Some("UTF8")) == "あいうえお" mustBe false
  }
  it must "throw an exception when encoding is invalid" in {
    assertThrows[Exception](TextReader.decodeCharArray(sjisText, Some("xxxx")))
  }
}
