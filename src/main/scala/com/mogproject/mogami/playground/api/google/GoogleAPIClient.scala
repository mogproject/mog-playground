package com.mogproject.mogami.playground.api.google

import scala.scalajs.js
import scala.scalajs.js.|
import scala.scalajs.js.annotation.JSGlobal

/**
  * Google API Client Facade
  */

@js.native
trait Thenable extends js.Object {
  def `then`(callback: js.Function): Unit = js.native
}

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
@JSGlobal
object gapi extends js.Object {

  @js.native
  object client extends js.Object {

    def setApiKey(apiKey: String): Unit = js.native

    def load(urlOrObject: String | js.Object): Thenable = js.native

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