package com.mogproject.mogami.playground.controller

abstract sealed class Language(val label: String) {
  override def toString: String = label
}

case object Japanese extends Language("ja")

case object English extends Language("en")

object Language {
  def parseString(s: String): Option[Language] = s match {
    case English.label => Some(English)
    case Japanese.label => Some(Japanese)
    case _ => None
  }
}