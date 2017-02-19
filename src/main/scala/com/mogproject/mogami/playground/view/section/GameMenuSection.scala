package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.{ImageLinkButton, RecordCopyButton, SfenStringCopyButton, SnapshotCopyButton}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object GameMenuSection extends Section {
  val output: Div = div(
    h4("Share"),
    SnapshotCopyButton.output,
    br(),
    RecordCopyButton.output,
    br(),
    ImageLinkButton.output,
    br(),
    SfenStringCopyButton.output,
    br(),
    h4("Manage"),
    p("to be implemented"),
    h4("Help"),
    p("to be implemented")
  ).render
}
