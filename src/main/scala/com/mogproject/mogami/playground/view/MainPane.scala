package com.mogproject.mogami.playground.view


import com.mogproject.mogami.frontend.view.{MainPaneLike, PlaygroundSiteLike}

case class MainPane(isMobile: Boolean, override val getSite: () => PlaygroundSiteLike) extends MainPaneLike
