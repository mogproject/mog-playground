package com.mogproject.mogami.playground.view.parts.common

import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
case class RadioButton[Key](keys: Seq[Key],
                            labels: Map[Language, Seq[String]],
                            buttonClasses: Seq[String] = Seq("btn-sm"),
                            buttonGroupClasses: Seq[String] = Seq("btn-group-justified"),
                            onClick: Key => Unit = { _: Key => {} }) extends EventManageable {

  private[this] lazy val labelMap: Map[Language, Map[Key, String]] = labels.map { case (l, s) => l -> keys.zip(s).toMap }

  private[this] lazy val inputMap: Map[Key, Anchor] = keys.map(k => k -> generateInput(k)).toMap

  private[this] def generateInput(key: Key): Anchor = a(
    cls := ("btn" :: "btn-primary" :: buttonClasses.toList).mkString(" "),
    onclick := { () => onClick(key); updateValue(key) }
  ).render

  private[this] def inputs: Seq[Anchor] = keys.map(inputMap)

  def initialize(): Unit = inputMap.foreach { case (k, e) => setClickEvent(e, () => onClick(k)) }

  lazy val output: Div = div(cls := "input-group",
    div(cls := ("btn-group" :: buttonGroupClasses.toList).mkString(" "),
      inputs
    )
  ).render

  def updateLabel(lang: Language): Unit = inputMap.foreach { case (k, e) => e.innerHTML = labelMap(lang)(k) }

  def updateValue(newValue: Key): Unit = {
    inputMap.foreach { case (k, e) =>
      if (k == newValue) {
        e.classList.remove("notActive")
        e.classList.add("active")
      } else {
        e.classList.remove("active")
        e.classList.add("notActive")
      }
    }
  }

  def getValue: Key = {
    inputMap.find(_._2.classList.contains("active")).map(_._1).getOrElse {
      throw new RuntimeException("Could not find the selected value")
    }
  }

  def enable(): Unit = {
    inputs.foreach(_.disabled = false)
  }

  def disable(): Unit = {
    inputs.foreach(_.disabled = true)
  }
}
