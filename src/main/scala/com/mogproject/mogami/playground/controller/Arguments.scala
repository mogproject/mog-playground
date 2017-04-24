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
case class Arguments(sfen: Option[String] = None, // deprecated
                     usen: Option[String] = None,
                     gameInfo: GameInfo = GameInfo(),
                     gamePosition: GamePosition = GamePosition(0, 0),
                     comments: Map[BranchNo, Map[Int, String]] = Map.empty,
                     action: Action = PlayAction,
                     config: Configuration = Configuration()) {
  def parseQueryString(query: String): Arguments = {
    @tailrec
    def f(sofar: Arguments, ls: List[List[String]]): Arguments = ls match {
      case ("sfen" :: s :: Nil) :: xs => f(sofar.copy(sfen = Some(s)), xs)
      case ("u" :: s :: Nil) :: xs => f(sofar.copy(usen = Some(s)), xs)
      case (x :: s :: Nil) :: xs if x.startsWith("c") => // comments
        parseGamePosition(x.drop(1)) match {
          case Some(pos) =>
            val c = comments.updated(pos.branch, comments.getOrElse(pos.branch, Map.empty).updated(pos.position, s))
            f(sofar.copy(comments = c), xs)
          case _ =>
            println(s"Invalid parameter: ${x}=${s}")
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
      case ("bn" :: s :: Nil) :: xs => f(sofar.copy(gameInfo = sofar.gameInfo.updated('blackName, s)), xs)
      case ("wn" :: s :: Nil) :: xs => f(sofar.copy(gameInfo = sofar.gameInfo.updated('whiteName, s)), xs)
      case _ :: xs => f(sofar, xs)
      case Nil => sofar
    }

    f(this, query.stripPrefix("?").split("&").map(s => decodeURIComponent(s).split("=", 2).toList).toList)
  }

  private[this] def parseGamePosition(s: String): Option[GamePosition] = {
    val pattern = raw"(?:([\d])+[.])?([\d]+)".r

    s match {
      case pattern(null, y) => for {yy <- Try(y.toInt).toOption}  yield GamePosition(0, yy)
      case pattern(x, y) => for {xx <- Try(x.toInt).toOption; yy <- Try(y.toInt).toOption}  yield GamePosition(xx, yy)
      case _ => None
    }
  }

}

case class ArgumentsBuilder(game: Game,
                            gamePosition: GamePosition = GamePosition(0, 0),
                            config: Configuration = Configuration()) {

  private[this] def toGamePosition(branchNo: BranchNo, pos: Int) = (branchNo == 0).fold("", branchNo + ".") + pos

  private[this] lazy val instantGame = Game(Branch(game.getState(gamePosition).get))

  lazy val fullRecordUrl: String = createUrl(gameParams ++ commentParams ++ gameInfoParams ++ positionParams)

  /**
    * true if comments are too long and omitted
    */
  lazy val commentOmitted: Boolean = fullRecordUrl.length > 2000

  def toRecordUrl: String = {
    if (commentOmitted) createUrl(gameParams ++ gameInfoParams ++ positionParams) else fullRecordUrl
  }

  def toSnapshotUrl: String = {
    createUrl(("u", instantGame.toUsenString) +: gameInfoParams)
  }

  def toImageLinkUrl: String = {
    createUrl(imageActionParams ++ gameParams ++ gameInfoParams ++ positionParams)
  }

  private[this] def gameParams: Seq[(String, String)] = Seq(("u", game.toUsenString))

  private[this] def commentParams: Seq[(String, String)] = {
    ((game.trunk, -1) +: game.branches.zipWithIndex).flatMap { case (br, n) =>
      br.comments.map { case (i, s) =>
        ("c" + toGamePosition(n + 1, i), s)
      }
    }
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

  /** comments may include Japanese characters */
  //  private[this] def encodeComment(s: String): String = s.getBytes("utf-8").toBase64
  //
  //  private[this] def decodeComment(s: String): String = new String(s.toByteArray, "utf-8")

}