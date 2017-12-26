package com.mogproject.mogami.playground.view


import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.{BasePlaygroundView, PlaygroundSite}
import org.scalajs.dom.Element


case class PlaygroundView(isMobile: Boolean, isDev: Boolean, isDebug: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSite = PlaygroundSiteX(isMobile, isDev, isDebug)
}
