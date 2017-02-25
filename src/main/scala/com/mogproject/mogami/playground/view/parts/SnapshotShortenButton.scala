package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller

/**
  *
  */
object SnapshotShortenButton extends ShortenButtonLike {
  override protected val ident = "snapshot-short"

  override def onClick(): Unit = Controller.shortenSnapshotUrl()
}
