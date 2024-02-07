package dao

import anorm.SQL
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.Inject

class ECDao @Inject()(db: Database) {

  def createEcOrder(order: Order): Unit = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.insertQuery(order, order.merchantId))
    }
  }

  private object BaseQuery {

    def insertQuery(order: Order, merchantId: String): String = {

      val query =
        s"""
           |INSERT INTO ec_orders
           |(
           |id,
           |number,
           |merchant_id,
           |total,
           |submitted_at
           |)
           |VALUES
           |(
           |'${order.id}',
           |'${order.number}',
           |'${merchantId}',
           |${order.total},
           |'${order.submittedAt}'
           |)
           |""".stripMargin
      query
    }
  }
}
