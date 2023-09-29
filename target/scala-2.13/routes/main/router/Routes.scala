// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:5
  Deliveries_0: controllers.Deliveries,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:5
    Deliveries_0: controllers.Deliveries
  ) = this(errorHandler, Deliveries_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Deliveries_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """merchant_id<[^/]+>/delivery/""" + "$" + """id<[^/]+>""", """controllers.Deliveries.getById(merchant_id:String, id:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:5
  private[this] lazy val controllers_Deliveries_getById0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("merchant_id", """[^/]+""",true), StaticPart("/delivery/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Deliveries_getById0_invoker = createInvoker(
    Deliveries_0.getById(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Deliveries",
      "getById",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """""" + "$" + """merchant_id<[^/]+>/delivery/""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:5
    case controllers_Deliveries_getById0_route(params@_) =>
      call(params.fromPath[String]("merchant_id", None), params.fromPath[String]("id", None)) { (merchant_id, id) =>
        controllers_Deliveries_getById0_invoker.call(Deliveries_0.getById(merchant_id, id))
      }
  }
}
