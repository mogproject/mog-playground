package com.mogproject.mogami.playground.api.google

import gapi.client.urlshortener.Response

/**
  *
  */
case class URLShortener(
                         baseUrl: String = "http://play.mogproject.com/",
                         apiKey: String = "AIzaSyD5UJMg2z6OHFo-AnQPJLK0oV8tf6BH6Nc" // todo: application.conf
                       ) {
  def initialize(): Unit = {
    gapi.client.setApiKey(apiKey)
    gapi.client.load("urlshortener")
  }

  def legalizeUrl(url: String): String = {
    if (url.startsWith(baseUrl))
      url
    else
      baseUrl + url.dropWhile(_ != '?')
  }

  def makeShortenedURL(longUrl: String, callback: String => Unit, failure: () => Unit): Unit = {
    val request = gapi.client.urlshortener.url.insert(RequestParams(legalizeUrl(longUrl)))

    request.execute {
      case (response: Response, _) if response.id != null => callback(response.id.toString)
      case _ => failure()
    }
  }
}
