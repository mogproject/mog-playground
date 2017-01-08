package com.mogproject.mogami.playground.controller

abstract sealed class Mode(val label: String)

case object Playing extends Mode("Play")

case object Viewing extends Mode("View")

case object Editing extends Mode("Edit")
