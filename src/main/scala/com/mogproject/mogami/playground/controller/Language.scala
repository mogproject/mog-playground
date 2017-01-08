package com.mogproject.mogami.playground.controller

abstract sealed class Language(val label: String)

case object Japanese extends Language("JP")

case object English extends Language("EN")
