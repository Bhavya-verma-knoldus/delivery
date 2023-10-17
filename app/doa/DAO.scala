package doa

import com.nashtech.delivery.v1.models.Delivery

import scala.concurrent.Future

trait DAO {

  def addUser(delivery: Delivery): Future[String]

  def getByid(merchantId: String, id: String): Either[Seq[String], Delivery]

  def getAll:   Either[Seq[String], Delivery]

  def updateById(merchantId: String, id: String):  Either[Seq[String], Delivery]

  def deleteById(merchantId: String): Future[String]

  def deleteAll(): Future[String]
}
