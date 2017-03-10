package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts._
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object GameMenuSection extends Section {
  val output: Div = div(
    h4("Share"),
    SnapshotCopyButton.output,
    SnapshotShortenButton.output,
    br(),
    RecordCopyButton.output,
    RecordShortenButton.output,
    br(),
    ImageLinkButton.output,
    br(),
    SfenStringCopyButton.output,
    br(),
    h4("Manage"),
    label("Load from File"),
    p("to be implemented"),
    SaveRecordButton.output,
    h4("Help"),
    ul(
      li("Click on a player name to set the game information.")
    )
  ).render
}
