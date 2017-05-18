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
    "info-sign",
    isExpanded = false,
    isVisible = true,
    div(
      p(i(""""Run anywhere. Needs NO installation."""")),
      p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
        " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
      p("This website best fits with ", a(href := "https://source.typekit.com/source-han-serif/", "Source Han Serif"), " font."),
      p("If you have any questions, trouble, or suggestion, please tell the ",
        a(href := "https://twitter.com/mogproject", target := "_blank", "author"),
        ". Your voice matters.")
    )))
}
