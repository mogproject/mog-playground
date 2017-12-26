package com.mogproject.mogami.playground.view


import com.mogproject.mogami.frontend.view.{MainPaneLike, PlaygroundSite}

case class MainPane(isMobile: Boolean, override val getSite: () => PlaygroundSite) extends MainPaneLike
