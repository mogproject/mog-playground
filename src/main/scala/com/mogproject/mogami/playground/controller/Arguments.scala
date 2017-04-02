package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.Game

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.decodeURIComponent
import scala.util.{Failure, Success, Try}

/**
  * stores parameters
  */
case class Arguments(game: Game = Game(),
                     currentMove: Int = 0,
                     action: Action = PlayAction,
                     config: Configuration = Configuration()) {
  def parseQueryString(query: String): Arguments = {
    @tailrec
    def f(sofar: Arguments, ls: List[List[String]]): Arguments = ls match {
      case ("sfen" :: s :: Nil) :: xs => Try(Game.parseSfenString(s)) match {
        case Success(g) => f(sofar.copy(game = g.copy(gameInfo = sofar.game.gameInfo)), xs)
        case Failure(e) =>
          println(s"Failed to create a game: ${e}")
          println(s"Invalid parameter: sfen=${s}")
          f(sofar, xs)
      }
      case ("mlang" :: s :: Nil) :: xs => s match {
        case "ja" => f(sofar.copy(config = sofar.config.copy(messageLang = Japanese)), xs)
        case "en" => f(sofar.copy(config = sofar.config.copy(messageLang = English)), xs)
        case _ =>
          println(s"Invalid parameter: mlang=${s}")
          f(sofar, xs)
      }
      case ("rlang" :: s :: Nil) :: xs => s match {
        case "ja" => f(sofar.copy(config = sofar.config.copy(recordLang = Japanese)), xs)
        case "en" => f(sofar.copy(config = sofar.config.copy(recordLang = English)), xs)
        case _ =>
          println(s"Invalid parameter: rlang=${s}")
          f(sofar, xs)
      }
      case ("plang" :: s :: Nil) :: xs => s match {
        case "ja" => f(sofar.copy(config = sofar.config.copy(pieceLang = Japanese)), xs)
        case "en" => f(sofar.copy(config = sofar.config.copy(pieceLang = English)), xs)
        case _ =>
          println(s"Invalid parameter: plang=${s}")
          f(sofar, xs)
      }
      case ("move" :: s :: Nil) :: xs => Try(s.toInt) match {
        case Success(n) if n >= 0 => f(sofar.copy(currentMove = n), xs)
        case _ =>
          println(s"Invalid parameter: move=${s}")
          f(sofar, xs)
      }
      case ("flip" :: s :: Nil) :: xs => s.toLowerCase match {
        case "true" => f(sofar.copy(config = sofar.config.copy(flip = true)), xs)
        case "false" => f(sofar, xs)
        case _ =>
          println(s"Invalid parameter: flip=${s}")
          f(sofar, xs)
      }
      case ("action" :: s :: Nil) :: xs => s match {
        case "image" => f(sofar.copy(action = ImageAction), xs)
        case _ =>
          println(s"Invalid parameter: action=${s}")
          f(sofar, xs)
      }
      case ("size" :: s :: Nil) :: xs => Try(s.toInt) match {
        case Success(n) if n > 0 => f(sofar.copy(config = sofar.config.copy(layoutSize = n)), xs)
        case _ =>
          println(s"Invalid parameter: size=${s}")
          f(sofar, xs)
      }
      case ("bn" :: s :: Nil) :: xs =>
        f(sofar.copy(game = sofar.game.copy(gameInfo = sofar.game.gameInfo.updated('blackName, s))), xs)
      case ("wn" :: s :: Nil) :: xs =>
        f(sofar.copy(game = sofar.game.copy(gameInfo = sofar.game.gameInfo.updated('whiteName, s))), xs)
      case _ :: xs => f(sofar, xs)
      case Nil => sofar
    }

    f(this, query.stripPrefix("?").split("&").map(s => decodeURIComponent(s).split("=", 2).toList).toList)
  }
}
