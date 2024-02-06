/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 1.0.1
 * apibuilder app.apibuilder.io/nashtech/delivery/latest/play_2_8_client
 */
package com.nashtech.delivery.v1.models {

  /**
   * @param text Full text version of address
   * @param streets Array for street line 1, street line 2, etc., in order
   * @param streetNumber The specific street number, if available.
   */

  final case class Address(
    text: _root_.scala.Option[String] = None,
    streets: _root_.scala.Option[Seq[String]] = None,
    streetNumber: _root_.scala.Option[String] = None,
    city: _root_.scala.Option[String] = None,
    province: _root_.scala.Option[String] = None,
    postal: _root_.scala.Option[String] = None,
    country: _root_.scala.Option[String] = None,
    latitude: _root_.scala.Option[String] = None,
    longitude: _root_.scala.Option[String] = None
  )

  final case class Contact(
    emailId: _root_.scala.Option[String] = None,
    lastName: _root_.scala.Option[String] = None,
    firstName: _root_.scala.Option[String] = None,
    mobileNumber: _root_.scala.Option[String] = None
  )

  final case class Delivery(
    id: String,
    orderNumber: String,
    merchantId: String,
    estimatedDeliveryDate: _root_.org.joda.time.DateTime,
    origin: com.nashtech.delivery.v1.models.Address,
    destination: com.nashtech.delivery.v1.models.Address,
    contactInfo: com.nashtech.delivery.v1.models.Contact
  )

  final case class DeliveryForm(
    origin: com.nashtech.delivery.v1.models.Address,
    destination: com.nashtech.delivery.v1.models.Address,
    contactInfo: com.nashtech.delivery.v1.models.Contact
  )

  final case class Error(
    code: String,
    message: Seq[String]
  )

}

package com.nashtech.delivery.v1.models {

  package object json {
    import play.api.libs.json.__

    private[v1] implicit val jsonReadsUUID: play.api.libs.json.Reads[_root_.java.util.UUID] = __.read[String].map { str =>
      _root_.java.util.UUID.fromString(str)
    }

    private[v1] implicit val jsonWritesUUID: play.api.libs.json.Writes[_root_.java.util.UUID] = (x: _root_.java.util.UUID) => play.api.libs.json.JsString(x.toString)

    private[v1] implicit val jsonReadsJodaDateTime: play.api.libs.json.Reads[_root_.org.joda.time.DateTime] = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(str)
    }

    private[v1] implicit val jsonWritesJodaDateTime: play.api.libs.json.Writes[_root_.org.joda.time.DateTime] = (x: _root_.org.joda.time.DateTime) => {
      play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(x))
    }

    private[v1] implicit val jsonReadsJodaLocalDate: play.api.libs.json.Reads[_root_.org.joda.time.LocalDate] = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(str)
    }

    private[v1] implicit val jsonWritesJodaLocalDate: play.api.libs.json.Writes[_root_.org.joda.time.LocalDate] = (x: _root_.org.joda.time.LocalDate) => {
      play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.date.print(x))
    }

    implicit def jsonReadsDeliveryAddress: play.api.libs.json.Reads[Address] = {
      for {
        text <- (__ \ "text").readNullable[String]
        streets <- (__ \ "streets").readNullable[Seq[String]]
        streetNumber <- (__ \ "street_number").readNullable[String]
        city <- (__ \ "city").readNullable[String]
        province <- (__ \ "province").readNullable[String]
        postal <- (__ \ "postal").readNullable[String]
        country <- (__ \ "country").readNullable[String]
        latitude <- (__ \ "latitude").readNullable[String]
        longitude <- (__ \ "longitude").readNullable[String]
      } yield Address(text, streets, streetNumber, city, province, postal, country, latitude, longitude)
    }

    def jsObjectAddress(obj: com.nashtech.delivery.v1.models.Address): play.api.libs.json.JsObject = {
      (obj.text match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("text" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.streets match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("streets" -> play.api.libs.json.Json.toJson(x))
      }) ++
      (obj.streetNumber match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("street_number" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.city match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("city" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.province match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("province" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.postal match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("postal" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.country match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("country" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.latitude match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("latitude" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.longitude match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("longitude" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesDeliveryAddress: play.api.libs.json.Writes[Address] = {
      (obj: com.nashtech.delivery.v1.models.Address) => {
        jsObjectAddress(obj)
      }
    }

    implicit def jsonReadsDeliveryContact: play.api.libs.json.Reads[Contact] = {
      for {
        emailId <- (__ \ "email_id").readNullable[String]
        lastName <- (__ \ "last_name").readNullable[String]
        firstName <- (__ \ "first_name").readNullable[String]
        mobileNumber <- (__ \ "mobile_number").readNullable[String]
      } yield Contact(emailId, lastName, firstName, mobileNumber)
    }

    def jsObjectContact(obj: com.nashtech.delivery.v1.models.Contact): play.api.libs.json.JsObject = {
      (obj.emailId match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("email_id" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.lastName match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("last_name" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.firstName match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("first_name" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.mobileNumber match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("mobile_number" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesDeliveryContact: play.api.libs.json.Writes[Contact] = {
      (obj: com.nashtech.delivery.v1.models.Contact) => {
        jsObjectContact(obj)
      }
    }

    implicit def jsonReadsDeliveryDelivery: play.api.libs.json.Reads[Delivery] = {
      for {
        id <- (__ \ "id").read[String]
        orderNumber <- (__ \ "order_number").read[String]
        merchantId <- (__ \ "merchant_id").read[String]
        estimatedDeliveryDate <- (__ \ "estimated_delivery_date").read[_root_.org.joda.time.DateTime]
        origin <- (__ \ "origin").read[com.nashtech.delivery.v1.models.Address]
        destination <- (__ \ "destination").read[com.nashtech.delivery.v1.models.Address]
        contactInfo <- (__ \ "contact_info").read[com.nashtech.delivery.v1.models.Contact]
      } yield Delivery(id, orderNumber, merchantId, estimatedDeliveryDate, origin, destination, contactInfo)
    }

    def jsObjectDelivery(obj: com.nashtech.delivery.v1.models.Delivery): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "order_number" -> play.api.libs.json.JsString(obj.orderNumber),
        "merchant_id" -> play.api.libs.json.JsString(obj.merchantId),
        "estimated_delivery_date" -> play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(obj.estimatedDeliveryDate)),
        "origin" -> jsObjectAddress(obj.origin),
        "destination" -> jsObjectAddress(obj.destination),
        "contact_info" -> jsObjectContact(obj.contactInfo)
      )
    }

    implicit def jsonWritesDeliveryDelivery: play.api.libs.json.Writes[Delivery] = {
      (obj: com.nashtech.delivery.v1.models.Delivery) => {
        jsObjectDelivery(obj)
      }
    }

    implicit def jsonReadsDeliveryDeliveryForm: play.api.libs.json.Reads[DeliveryForm] = {
      for {
        origin <- (__ \ "origin").read[com.nashtech.delivery.v1.models.Address]
        destination <- (__ \ "destination").read[com.nashtech.delivery.v1.models.Address]
        contactInfo <- (__ \ "contact_info").read[com.nashtech.delivery.v1.models.Contact]
      } yield DeliveryForm(origin, destination, contactInfo)
    }

    def jsObjectDeliveryForm(obj: com.nashtech.delivery.v1.models.DeliveryForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "origin" -> jsObjectAddress(obj.origin),
        "destination" -> jsObjectAddress(obj.destination),
        "contact_info" -> jsObjectContact(obj.contactInfo)
      )
    }

    implicit def jsonWritesDeliveryDeliveryForm: play.api.libs.json.Writes[DeliveryForm] = {
      (obj: com.nashtech.delivery.v1.models.DeliveryForm) => {
        jsObjectDeliveryForm(obj)
      }
    }

    implicit def jsonReadsDeliveryError: play.api.libs.json.Reads[Error] = {
      for {
        code <- (__ \ "code").read[String]
        message <- (__ \ "message").read[Seq[String]]
      } yield Error(code, message)
    }

    def jsObjectError(obj: com.nashtech.delivery.v1.models.Error): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "code" -> play.api.libs.json.JsString(obj.code),
        "message" -> play.api.libs.json.Json.toJson(obj.message)
      )
    }

    implicit def jsonWritesDeliveryError: play.api.libs.json.Writes[Error] = {
      (obj: com.nashtech.delivery.v1.models.Error) => {
        jsObjectError(obj)
      }
    }
  }
}

package com.nashtech.delivery.v1 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}

    // import models directly for backwards compatibility with prior versions of the generator

    object Core {
      implicit def pathBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.DateTime] = ApibuilderPathBindable(ApibuilderTypes.dateTimeIso8601)
      implicit def queryStringBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.DateTime] = ApibuilderQueryStringBindable(ApibuilderTypes.dateTimeIso8601)

      implicit def pathBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.LocalDate] = ApibuilderPathBindable(ApibuilderTypes.dateIso8601)
      implicit def queryStringBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.LocalDate] = ApibuilderQueryStringBindable(ApibuilderTypes.dateIso8601)
    }

    trait ApibuilderTypeConverter[T] {

      def convert(value: String): T

      def convert(value: T): String

      def example: T

      def validValues: Seq[T] = Nil

      def errorMessage(key: String, value: String, ex: java.lang.Exception): String = {
        val base = s"Invalid value '$value' for parameter '$key'. "
        validValues.toList match {
          case Nil => base + "Ex: " + convert(example)
          case values => base + ". Valid values are: " + values.mkString("'", "', '", "'")
        }
      }
    }

    object ApibuilderTypes {
      val dateTimeIso8601: ApibuilderTypeConverter[_root_.org.joda.time.DateTime] = new ApibuilderTypeConverter[_root_.org.joda.time.DateTime] {
        override def convert(value: String): _root_.org.joda.time.DateTime = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(value)
        override def convert(value: _root_.org.joda.time.DateTime): String = _root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(value)
        override def example: _root_.org.joda.time.DateTime = _root_.org.joda.time.DateTime.now
      }

      val dateIso8601: ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] = new ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] {
        override def convert(value: String): _root_.org.joda.time.LocalDate = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(value)
        override def convert(value: _root_.org.joda.time.LocalDate): String = _root_.org.joda.time.format.ISODateTimeFormat.date.print(value)
        override def example: _root_.org.joda.time.LocalDate = _root_.org.joda.time.LocalDate.now
      }
    }

    final case class ApibuilderQueryStringBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends QueryStringBindable[T] {

      override def bind(key: String, params: Map[String, Seq[String]]): _root_.scala.Option[_root_.scala.Either[String, T]] = {
        params.getOrElse(key, Nil).headOption.map { v =>
          try {
            Right(
              converters.convert(v)
            )
          } catch {
            case ex: java.lang.Exception => Left(
              converters.errorMessage(key, v, ex)
            )
          }
        }
      }

      override def unbind(key: String, value: T): String = {
        s"$key=${converters.convert(value)}"
      }
    }

    final case class ApibuilderPathBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends PathBindable[T] {

      override def bind(key: String, value: String): _root_.scala.Either[String, T] = {
        try {
          Right(
            converters.convert(value)
          )
        } catch {
          case ex: java.lang.Exception => Left(
            converters.errorMessage(key, value, ex)
          )
        }
      }

      override def unbind(key: String, value: T): String = {
        converters.convert(value)
      }
    }

  }

}


package com.nashtech.delivery.v1 {

  object Constants {

    val BaseUrl = "https://nashtechglobal.com"
    val Namespace = "com.nashtech.delivery.v1"
    val UserAgent = "apibuilder app.apibuilder.io/nashtech/delivery/latest/play_2_8_client"
    val Version = "1.0.1"
    val VersionMajor = 1

  }

  class Client(
    ws: play.api.libs.ws.WSClient,
    val baseUrl: String = "https://nashtechglobal.com",
    auth: scala.Option[com.nashtech.delivery.v1.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import com.nashtech.delivery.v1.models.json._

    private[this] val logger = play.api.Logger("com.nashtech.delivery.v1.Client")

    logger.info(s"Initializing com.nashtech.delivery.v1.Client for url $baseUrl")

    def deliveries: Deliveries = Deliveries

    object Deliveries extends Deliveries {
      override def getById(
        merchantId: String,
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery] = {
        _executeRequest("GET", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/delivery/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.delivery.v1.Client.parseJson("com.nashtech.delivery.v1.models.Delivery", r, _.validate[com.nashtech.delivery.v1.models.Delivery])
          case r if r.status == 401 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r => throw com.nashtech.delivery.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def post(
        merchantId: String,
        deliveryForm: com.nashtech.delivery.v1.models.DeliveryForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery] = {
        val payload = play.api.libs.json.Json.toJson(deliveryForm)

        _executeRequest("POST", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/delivery", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.delivery.v1.Client.parseJson("com.nashtech.delivery.v1.models.Delivery", r, _.validate[com.nashtech.delivery.v1.models.Delivery])
          case r if r.status == 401 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.delivery.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.delivery.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }

      override def putByOrderNumber(
        merchantId: String,
        orderNumber: String,
        deliveryForm: com.nashtech.delivery.v1.models.DeliveryForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery] = {
        val payload = play.api.libs.json.Json.toJson(deliveryForm)

        _executeRequest("PUT", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/delivery/${play.utils.UriEncoding.encodePathSegment(orderNumber, "UTF-8")}", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.delivery.v1.Client.parseJson("com.nashtech.delivery.v1.models.Delivery", r, _.validate[com.nashtech.delivery.v1.models.Delivery])
          case r if r.status == 401 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.delivery.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.delivery.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }

      override def deleteByOrderNumber(
        merchantId: String,
        orderNumber: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/delivery/${play.utils.UriEncoding.encodePathSegment(orderNumber, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => ()
          case r if r.status == 401 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.delivery.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.delivery.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.delivery.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {

      val holder = ws.url(baseUrl + path).addHttpHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).addHttpHeaders(defaultHeaders : _*)
      auth.fold(holder) {
        case Authorization.Basic(username, password) => {
          holder.withAuth(username, password.getOrElse(""), play.api.libs.ws.WSAuthScheme.BASIC)
        }
        case a => sys.error("Invalid authorization scheme[" + a.getClass + "]")
      }
    }

    def _logRequest(method: String, req: play.api.libs.ws.WSRequest): play.api.libs.ws.WSRequest = {
      val queryComponents = for {
        (name, values) <- req.queryString
        value <- values
      } yield s"$name=$value"
      val url = s"${req.url}${queryComponents.mkString("?", "&", "")}"
      auth.fold(logger.info(s"curl -X $method '$url'")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' '$url'")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Nil,
      requestHeaders: Seq[(String, String)] = Nil,
      body: Option[play.api.libs.json.JsValue] = None
    ): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

    /**
     * Adds a Content-Type: application/json header unless the specified requestHeaders
     * already contain a Content-Type header
     */
    def _withJsonContentType(headers: Seq[(String, String)]): Seq[(String, String)] = {
      headers.find { _._1.toUpperCase == "CONTENT-TYPE" } match {
        case None => headers ++ Seq("Content-Type" -> "application/json; charset=UTF-8")
        case Some(_) => headers
      }
    }

  }

  object Client {

    def parseJson[T](
      className: String,
      r: play.api.libs.ws.WSResponse,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.body)) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw com.nashtech.delivery.v1.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization extends _root_.scala.Product with _root_.scala.Serializable
  object Authorization {
    final case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def baseUrl: String
      def deliveries: com.nashtech.delivery.v1.Deliveries
    }

  }

  trait Deliveries {
    def getById(
      merchantId: String,
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery]

    def post(
      merchantId: String,
      deliveryForm: com.nashtech.delivery.v1.models.DeliveryForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery]

    def putByOrderNumber(
      merchantId: String,
      orderNumber: String,
      deliveryForm: com.nashtech.delivery.v1.models.DeliveryForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.delivery.v1.models.Delivery]

    def deleteByOrderNumber(
      merchantId: String,
      orderNumber: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import com.nashtech.delivery.v1.models.json._

    final case class ErrorResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(s"${response.status}: ${response.body}")) {
      lazy val error = _root_.com.nashtech.delivery.v1.Client.parseJson("com.nashtech.delivery.v1.models.Error", response, _.validate[com.nashtech.delivery.v1.models.Error])
    }

    final case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    final case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

}