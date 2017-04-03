package com.mogproject.mogami.playground.io

import com.mogproject.mogami.playground.api.EscapeCodecLibrary._
import org.scalajs.dom
import org.scalajs.dom.raw.FileReader

import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

/**
  *
  */
object TextReader {
  /**
    * @param file file object to read
    * @param callback main logic to use the content
    * @param sizeChecker cancels reading a file when this function returns false
    * @param encoding charactor set is automatically detected if None
    * @param replaceNewLine remove carriage return when true
    */
  def readTextFile(file: dom.raw.File,
                   callback: String => Unit,
                   sizeChecker: Int => Boolean,
                   encoding: Option[String] = None,
                   replaceNewLine: Boolean = true): Unit = {
    val r = new FileReader()
    r.onload = evt => {
      val result = evt.target.asInstanceOf[FileReader].result.asInstanceOf[ArrayBuffer]

      val len = result.byteLength
      if (!sizeChecker(len)) {
        val uints = new Uint8Array(result)
        val chars = for (i <- 0 until len) yield uints(i).toChar
        val content = TextReader.decodeCharArray(removeBOM(chars).toArray)
        val txt = if (replaceNewLine) content.replace("\r", "") else content
        callback(txt)
      }
    }
    r.readAsArrayBuffer(file)
  }

  def encodeCharArray(s: String, encoding: String = "UTF8"): Array[Char] = {
    val escaped = getEscaper(encoding)(s)
    val unicode = UnescapeUnicode(escaped)
    unicode.toCharArray
  }

  def decodeCharArray(xs: Array[Char], encoding: Option[String] = None): String = {
    val encoded = new String(xs)
    val escaped = EscapeUnicode(encoded)
    val enc = encoding.getOrElse(GetEscapeCodeType(escaped))
    getUnescaper(enc)(escaped)
  }

  /**
    * Remove the byte order mark of UTF-8
    */
  private[this] def removeBOM(chars: Seq[Char]): Seq[Char] = {
    val utf8BOM = Seq(0xef, 0xbb, 0xbf)
    if (chars.take(3) == utf8BOM) chars.drop(3) else chars
  }

  private[this] def getEscaper(encoding: String): String => String = encoding match {
    case "SJIS" => EscapeSJIS
    case "EUCJP" => EscapeEUCJP
    case "JIS7" => EscapeJIS7
    case "JIS8" => EscapeJIS8
    case "Unicode" => EscapeUnicode
    case "UTF7" => EscapeUTF7
    case "UTF8" => EscapeUTF8
    case "UTF16LE" => EscapeUTF16LE
    case _ => throw new Exception(s"unknown encoding: ${encoding}")
  }

  private[this] def getUnescaper(encoding: String): String => String = encoding match {
    case "SJIS" => UnescapeSJIS
    case "EUCJP" => UnescapeEUCJP
    case "JIS7" => UnescapeJIS7
    case "JIS8" => UnescapeJIS8
    case "Unicode" => UnescapeUnicode
    case "UTF7" => UnescapeUTF7
    case "UTF8" => UnescapeUTF8
    case "UTF16LE" => UnescapeUTF16LE
    case _ => throw new Exception(s"unknown encoding: ${encoding}")
  }
}
