package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object AboutSection extends Section {
  val output: Div = div(
    div(
      h4("About This Site"),
      p(i(""""Run anywhere. Need NO installation."""")),
      p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
        " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
      p("If you have any questions, trouble, or suggestion, please tell the ",
        a(href := "https://twitter.com/mogproject", target := "_blank", "author"),
        ". Your voice matters.")
    )
  ).render
}
