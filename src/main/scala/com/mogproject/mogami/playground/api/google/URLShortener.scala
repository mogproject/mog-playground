package com.mogproject.mogami.playground.api.google

import gapi.client.urlshortener.Response

import scala.scalajs.js

/**
  *
  */
case class URLShortener(
                         baseUrl: String = "http://play.mogproject.com/",
                         apiKey: String = "AIzaSyD5UJMg2z6OHFo-AnQPJLK0oV8tf6BH6Nc" // todo: application.conf
                       ) {
  private[this] var initialized: Option[Thenable] = None

  private[this] def getClient: Option[Thenable] = {
    initialized match {
      case Some(_) => // do nothing
      case None => initialized = initialize()
    }
    initialized
  }

  private[this] def initialize(): Option[Thenable] = {
    // check if Google API client is ready
    if (js.isUndefined(gapi.client)) {
      // not yet loaded
      None
    } else {
      gapi.client.setApiKey(apiKey)
      Some(gapi.client.load("urlshortener"))
    }
  }

  def legalizeUrl(url: String): String = {
    if (url.startsWith(baseUrl))
      url
    else
      baseUrl + url.dropWhile(_ != '?')
  }

  def makeShortenedURL(longUrl: String, callback: String => Unit, failure: String => Unit): Unit = {
    getClient match {
      case None =>
        failure("Failed: Google API Client is not ready")
      case Some(th) => th.`then` { () =>
        val request = gapi.client.urlshortener.url.insert(RequestParams(legalizeUrl(longUrl)))

        request.execute {
          case (response: Response, _) if response.id != null => callback(response.id.toString)
          case _ => failure("Failed: Cannot create a shortened URL")
        }
      }
    }

  }
}
