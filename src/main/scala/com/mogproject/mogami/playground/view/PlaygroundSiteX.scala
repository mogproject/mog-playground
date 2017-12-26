package com.mogproject.mogami.playground.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.{MainPaneLike, PlaygroundSite}
import com.mogproject.mogami.frontend.view.footer.FooterLike

case class PlaygroundSiteX(isMobile: Boolean, isDev: Boolean, isDebug: Boolean) extends PlaygroundSite {
  override lazy val mainPane: MainPaneLike = MainPane(isMobile, () => this)

  override lazy val navBar: NavBar = NavBar(isMobile)

  override lazy val footer: FooterLike = Footer(isDev, isDebug)
}

