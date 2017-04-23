package com.mogproject.mogami.playground.controller

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits._

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.{decodeURIComponent, encodeURIComponent}
import scala.util.{Failure, Success, Try}

/**
  * stores parameters
  */
case class Arguments(game: Game = Game(),
                     gamePosition: GamePosition = GamePosition(0, 0),
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
      case ("move" :: s :: Nil) :: xs => parseGamePosition(s) match {
        case Some(gp) => f(sofar.copy(gamePosition = gp), xs)
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

  private[this] def parseGamePosition(s: String): Option[GamePosition] = {
    val pattern = raw"(?:([\d])+[.])?([\d]+)".r

    s match {
      case pattern(null, y) => for {yy <- Try(y.toInt).toOption} yield GamePosition(0, yy)
      case pattern(x, y) => for {xx <- Try(x.toInt).toOption; yy <- Try(y.toInt).toOption} yield GamePosition(xx, yy)
      case _ => None
    }
  }

  private[this] lazy val exg = game.toSfenExtendedGame

  private[this] lazy val instantGame = Game(Branch(game.getState(gamePosition).get))

  def toRecordUrl: String = {
    createUrl(branchParams ++ finalActionParams ++ commentParams ++ gameInfoParams ++ positionParams)
  }

  def toSnapshotUrl: String = {
    createUrl(("sfen", instantGame.toSfenString) +: gameInfoParams)
  }

  def toImageLinkUrl: String = {
    createUrl(imageActionParams ++ branchParams ++ finalActionParams ++ gameInfoParams ++ positionParams)
  }

  private[this] def branchParams: Seq[(String, String)] = {
    Seq(("sfen", exg.trunk.moves)) ++ exg.branches.zipWithIndex.map { case (br, n) => (s"br${n}", br.moves) }
  }

  private[this] def finalActionParams: Seq[(String, String)] = {
    exg.trunk.finalAction.map(("fin", _)).toSeq ++
      exg.branches.zipWithIndex.flatMap { case (br, n) => br.finalAction.map((s"fin${n}", _)) }
  }

  private[this] def commentParams: Seq[(String, String)] = {
    exg.trunk.comments.map { case (i, s) => (s"c${i}", s) }.toSeq ++
      exg.branches.zipWithIndex.flatMap { case (br, n) => br.comments.map { case (i, s) => (s"c${n}.${i}", s) } }
  }

  private[this] def gameInfoParams: Seq[(String, String)] = {
    val params = List(("bn", 'blackName), ("wn", 'whiteName))
    params.flatMap { case (q, k) => game.gameInfo.tags.get(k).map((q, _)) }
  }

  private[this] def positionParams: Seq[(String, String)] = {
    val prefix = (gamePosition.branch == 0).fold("", s"${gamePosition.branch}.")
    (gamePosition.branch != 0 || gamePosition.position != 0).option(("move", s"${prefix}${gamePosition.position}")).toSeq
  }

  private[this] def imageActionParams: Seq[(String, String)] = Seq(("action", "image"))

  private[this] def createUrl(params: Seq[(String, String)]) = {
    config.baseUrl + "?" + (params.map { case (k, v) => k + "=" + encodeURIComponent(v) } ++ config.toQueryParameters).mkString("&")
  }
}