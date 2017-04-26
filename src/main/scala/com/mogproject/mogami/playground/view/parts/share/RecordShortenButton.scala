package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.common.ShortenButtonLike

/**
  *
  */
object RecordShortenButton extends ShortenButtonLike {
  override protected val ident = "record-short"

  override def onClick(): Unit = Controller.shortenRecordUrl()
}
