// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:5
package controllers {

  // @LINE:5
  class ReverseDeliveries(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def getById(merchant_id:String, id:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("merchant_id", merchant_id)) + "/delivery/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
  }


}
