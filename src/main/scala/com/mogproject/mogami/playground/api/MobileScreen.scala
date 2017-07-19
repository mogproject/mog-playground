package com.mogproject.mogami.playground.api

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("MobileWindow")
sealed class MobileWindow extends js.Object {
  @js.native
  def orientation: UndefOr[Int] = js.native
}

@js.native
@JSGlobal("MobileScreen")
sealed class MobileScreen extends js.Object {
  @js.native
  def orientation: UndefOr[Orientation] = js.native
}

@js.native
@JSGlobal("Orientation")
sealed class Orientation extends js.Object {
  @js.native
  def angle: UndefOr[Int] = js.native
}

object MobileScreen {
  private[this] def getAngle1: UndefOr[Int] = dom.window.asInstanceOf[MobileWindow].orientation

  private[this] def getAngle2: UndefOr[Int] = dom.window.screen.asInstanceOf[MobileScreen].orientation.flatMap(_.angle)

  /**
    *
    * @return true if the orientation is the landscape mode, false if the portrait mode
    */
  def isLandscape: Boolean = math.abs(getAngle1.getOrElse(getAngle2.getOrElse(0))) == 90
}