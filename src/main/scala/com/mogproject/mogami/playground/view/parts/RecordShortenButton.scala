package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller

/**
  *
  */
object RecordShortenButton extends ShortenButtonLike {
  override protected val ident = "record-short"

  override def onClick(): Unit = Controller.shortenRecordUrl()
}
