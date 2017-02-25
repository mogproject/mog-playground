package com.mogproject.mogami.playground.api.google

import scala.scalajs.js
import scala.scalajs.js.|

/**
  * Google API Client Facade
  */

@js.native
trait RequestParams extends js.Object {
  var longUrl: String = js.native
}

object RequestParams {
  def apply(longUrl: String): RequestParams = {
    val r = js.Dynamic.literal()
    r.longUrl = longUrl
    r.asInstanceOf[RequestParams]
  }
}

@js.native
object gapi extends js.Object {

  @js.native
  object client extends js.Object {

    def setApiKey(apiKey: String): Unit = js.native

    def load(urlOrObject: String | js.Object): Unit = js.native

    @js.native
    class HttpRequest[T] extends js.Object {
      def execute(callback: js.Function2[T, js.Any, Any]): Unit = js.native
    }

    @js.native
    object urlshortener extends js.Object {

      @js.native
      trait Response extends js.Object {
        var id: String | Null = js.native
      }

      @js.native
      object url extends js.Object {
        def insert(params: RequestParams): HttpRequest[Response] = js.native
      }

    }

  }

}