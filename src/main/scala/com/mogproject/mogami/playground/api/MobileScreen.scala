package com.mogproject.mogami.playground.api

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.UndefOr

@js.native
class MobileWindow extends js.Object {
  @js.native
  def orientation: UndefOr[Int] = js.native
}

object MobileScreen {
  /**
    *
    * @return true if the orientation is the landscape mode, false if the portrait mode
    */
  def isLandscape: Boolean = {
    dom.window.asInstanceOf[MobileWindow].orientation.map(math.abs(_) == 90).getOrElse(false)
  }
}