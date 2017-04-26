package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.common.ShortenButtonLike

/**
  *
  */
object SnapshotShortenButton extends ShortenButtonLike {
  override protected val ident = "snapshot-short"

  override def onClick(): Unit = Controller.shortenSnapshotUrl()
}
