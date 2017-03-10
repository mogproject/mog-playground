package com.mogproject.mogami.playground.io

import com.mogproject.mogami.playground.api.FileSaver
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Blob, BlobPropertyBag}

import scala.scalajs.js
import scala.util.Try

/**
  *
  */
object FileWriter {
  def saveTextFile(content: String, fileName: String): Unit = {
    val result = Try {
      val data = new Blob(js.Array(content.asInstanceOf[js.Any]), BlobPropertyBag("text/plain;charset=utf-8"))
      FileSaver.saveAs(data, fileName)
    }
    if (result.isFailure) {
      dom.window.alert("Failed to save the file.")
    }
  }

  def saveImageFile(canvas: Canvas, fileName: String): Unit = ???
}
