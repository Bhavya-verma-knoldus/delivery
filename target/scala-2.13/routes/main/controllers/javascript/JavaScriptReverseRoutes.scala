// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:5
package controllers.javascript {

  // @LINE:5
  class ReverseDeliveries(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def getById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Deliveries.getById",
      """
        function(merchant_id0,id1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("merchant_id", merchant_id0)) + "/delivery/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id1))})
        }
      """
    )
  
  }


}
