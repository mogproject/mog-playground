package com.mogproject.mogami.playground.view


import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.{BasePlaygroundView, PlaygroundSiteLike}
import org.scalajs.dom.Element


case class PlaygroundView(isMobile: Boolean, isDev: Boolean, isDebug: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSiteLike = PlaygroundSite(isMobile, isDev, isDebug)
}
