package com.mogproject.mogami.playground.view

import com.mogproject.mogami.frontend.view.{MainPaneLike, PlaygroundSiteLike}
import com.mogproject.mogami.frontend.view.footer.FooterLike

case class PlaygroundSite(isMobile: Boolean, freeMode: Boolean, embeddedMode: Boolean, isDev: Boolean, isDebug: Boolean) extends PlaygroundSiteLike {
  override lazy val mainPane: MainPaneLike = MainPane(isMobile, embeddedMode, () => this)

  override lazy val navBar: NavBar = NavBar(isMobile, embeddedMode)

  override lazy val footer: FooterLike = Footer(isDev, isDebug)
}

