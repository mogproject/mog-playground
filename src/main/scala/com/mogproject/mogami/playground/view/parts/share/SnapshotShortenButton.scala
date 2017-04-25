package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.control.ShortenButtonLike

/**
  *
  */
object SnapshotShortenButton extends ShortenButtonLike {
  override protected val ident = "snapshot-short"

  override def onClick(): Unit = Controller.shortenSnapshotUrl()
}
