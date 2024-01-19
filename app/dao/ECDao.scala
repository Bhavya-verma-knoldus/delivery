package dao

import anorm.SQL
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.Inject

class ECDao @Inject()(db: Database) {

  def createEcOrder(order: Order, merchantId: String): Unit = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.insertQuery(order, merchantId))
    }
  }

  private object BaseQuery {

    //    def selectQuery(merchantId: String, id: String): String = {
    //      s"SELECT * FROM deliveries WHERE id = '$id' AND merchant_id = '$merchantId';"
    //    }

    def insertQuery(order: Order, merchantId: String): String = {

      val query =
        s"""
           |INSERT INTO ec_orders
           |(
           |id,
           |number,
           |merchant_id,
           |submitted_at,
           |total
           |)
           |VALUES
           |(
           |'${order.id}',
           |'${order.number}',
           |'${merchantId}',
           |'${DateTime.now()}',
           |${order.total}
           |)
           |RETURNING *
           |""".stripMargin
      query
    }
  }
}
