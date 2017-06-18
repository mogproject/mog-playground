package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.parts.common.ShortenButtonLike

/**
  *
  */
object NotesViewShortenButton extends ShortenButtonLike {
  override protected val ident = "notes-view-short"

  override def onClick(): Unit = Controller.shortenNotesViewUrl()
}
