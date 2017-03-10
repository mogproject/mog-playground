package com.mogproject.mogami.playground.io

import com.mogproject.mogami.playground.api.FileSaver
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Blob, BlobPropertyBag}

import scala.scalajs.js

/**
  *
  */
object FileWriter {
  def saveTextFile(content: String, fileName: String): Unit = {
    println(s"Writing: ${content}")
    val data = new Blob(js.Array(content.asInstanceOf[js.Any]), BlobPropertyBag("text/plain;charset=utf-8"))
    FileSaver.saveAs(data, fileName)
  }

  def saveImageFile(canvas: Canvas, fileName: String): Unit = ???
}
