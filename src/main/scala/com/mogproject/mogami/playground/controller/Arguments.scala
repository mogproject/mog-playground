package com.mogproject.mogami.playground.controller

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.decodeURIComponent

/**
  * stores parameters
  */
case class Arguments(canvasWidth: Int = 320,
                     canvasHeight: Int = 480,
                     sfen: Option[String] = None,
                     lang: Option[String] = None,
                     mode: Option[String] = None,
                     comment: Option[String] = None
                    ) {
  def parseQueryString(query: String): Arguments = {
    @tailrec
    def f(sofar: Arguments, ls: List[List[String]]): Arguments = ls match {
      case ("sfen" :: s :: Nil) :: xs => f(sofar.copy(sfen = Some(s)), xs)
      case ("lang" :: s :: Nil) :: xs => f(sofar.copy(lang = Some(s)), xs)
      case _ :: xs => f(sofar, xs) // ignore invalid parameters
      case Nil => sofar
    }

    f(this, query.stripPrefix("?").split("&").map(s => decodeURIComponent(s).split("=", 2).toList).toList)
  }
}
