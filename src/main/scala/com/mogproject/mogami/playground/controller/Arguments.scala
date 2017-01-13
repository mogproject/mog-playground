package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.Game

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.decodeURIComponent
import scala.util.{Success, Try}

/**
  * stores parameters
  */
case class Arguments(game: Game = Game(), currentMove: Int = -1, config: Configuration = Configuration()) {
  def parseQueryString(query: String): Arguments = {
    @tailrec
    def f(sofar: Arguments, ls: List[List[String]]): Arguments = ls match {
      case ("sfen" :: s :: Nil) :: xs => Game.parseSfenString(s) match {
        case Some(g) => f(sofar.copy(game = g), xs)
        case None =>
          println(s"Invalid parameter: sfen=${s}")
          f(sofar, xs)
      }
      case ("lang" :: s :: Nil) :: xs => s match {
        case "ja" => f(sofar.copy(config = sofar.config.copy(lang = Japanese)), xs)
        case "en" => f(sofar.copy(config = sofar.config.copy(lang = English)), xs)
        case _ =>
          println(s"Invalid parameter: lang=${s}")
          f(sofar, xs)
      }
      case ("move" :: s :: Nil) :: xs => Try(s.toInt) match {
        case Success(n) if n >= 0 => f(sofar.copy(currentMove = n), xs)
        case _ =>
          println(s"Invalid parameter: move=${s}")
          f(sofar, xs)
      }
      case _ :: xs => f(sofar, xs)
      case Nil => sofar
    }

    f(this, query.stripPrefix("?").split("&").map(s => decodeURIComponent(s).split("=", 2).toList).toList)
  }
}
