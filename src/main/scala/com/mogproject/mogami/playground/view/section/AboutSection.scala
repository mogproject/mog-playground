package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object AboutSection extends Section {
  override val accordions: Seq[AccordionMenu] = Seq(AccordionMenu(
    "About",
    "About This Site",
    false,
    true,
    div(
      p(i(""""Run anywhere. Need NO installation."""")),
      p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
        " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
      p("If you have any questions, trouble, or suggestion, please tell the ",
        a(href := "https://twitter.com/mogproject", target := "_blank", "author"),
        ". Your voice matters.")
    )))
}
