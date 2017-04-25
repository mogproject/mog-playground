package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.control.ShortenButtonLike

/**
  *
  */
object RecordShortenButton extends ShortenButtonLike {
  override protected val ident = "record-short"

  override def onClick(): Unit = Controller.shortenRecordUrl()
}
