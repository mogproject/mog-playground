package com.mogproject.mogami.playground.controller

import org.scalajs.dom

import scala.util.Try

/**
  *
  */
case class LocalStorage(canvasSize: Option[Int] = None,
                        doubleBoardMode: Option[Boolean] = None,
                        messageLang: Option[Language] = None,
                        recordLang: Option[Language] = None,
                        pieceLang: Option[Language] = None)

object LocalStorage {
  def load(): LocalStorage = {
    val keys: Seq[(String, String => LocalStorage => LocalStorage)] = Seq(
      ("size", s => ls => ls.copy(canvasSize = Try(s.toInt).toOption)),
      ("double", s => ls => ls.copy(doubleBoardMode = parseBooleanString(s))),
      ("mlang", s => ls => ls.copy(messageLang = Language.parseString(s))),
      ("rlang", s => ls => ls.copy(recordLang = Language.parseString(s))),
      ("plang", s => ls => ls.copy(pieceLang = Language.parseString(s)))
    )
    keys.foldLeft[LocalStorage](LocalStorage()) { case (ls, (k, f)) => f(dom.window.localStorage.getItem(k))(ls) }
  }

  def saveSize(size: Int): Unit = setItem("size", size)

  def saveDoubleBoardMode(enabled: Boolean): Unit = setItem("double", enabled)

  def saveMessageLang(lang: Language): Unit = setItem("mlang", lang)

  def saveRecordLang(lang: Language): Unit = setItem("rlang", lang)

  def savePieceLang(lang: Language): Unit = setItem("plang", lang)

  private[this] def parseBooleanString(s: String): Option[Boolean] = s match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }

  private[this] def setItem(key: String, item: Any): Unit = dom.window.localStorage.setItem(key, item.toString)
}
