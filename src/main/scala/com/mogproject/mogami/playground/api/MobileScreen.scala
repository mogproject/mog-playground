package com.mogproject.mogami.playground.api

import org.scalajs.dom

import scala.scalajs.js
import scala.util.{Try, Success, Failure}

@js.native
class MobileScreen extends js.Object {
  @js.native
  def orientation: js.Object = js.native
}

@js.native
class Orientation extends js.Object {
  @js.native
  def angle: Int = js.native
}

object MobileScreen {
  /**
    *
    * @return true if the orientation is the landscape mode, false if the portrait mode
    */
  def isLandscape: Boolean = {
    Try(dom.window.screen.asInstanceOf[MobileScreen].orientation.asInstanceOf[Orientation].angle) match {
      case Success(x) => math.abs(x) == 90
      case Failure(_) => false
    }
  }
}