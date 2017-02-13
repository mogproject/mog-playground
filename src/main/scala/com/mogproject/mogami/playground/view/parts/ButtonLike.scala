package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement

/**
  *
  */
trait ButtonLike[Key, Input <: HTMLElement, Output <: Element] extends EventManageable {
  protected def keys: Seq[Key]

  protected def labels: Map[Language, Seq[String]]

  protected def generateInput(key: Key): Input

  protected def invoke(key: Key): Unit

  def output: Output

  private[this] lazy val labelMap: Map[Language, Map[Key, String]] = labels.map { case (l, s) => l -> keys.zip(s).toMap }

  private[this] lazy val inputMap: Map[Key, Input] = keys.map(k => k -> generateInput(k)).toMap

  protected def inputs: Seq[Input] = keys.map(inputMap)

  def initialize(): Unit = inputMap.foreach { case (k, e) => setClickEvent(e, () => invoke(k)) }

  final def updateLabel(lang: Language): Unit = inputMap.foreach { case (k, e) => e.innerHTML = labelMap(lang)(k) }

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
}
