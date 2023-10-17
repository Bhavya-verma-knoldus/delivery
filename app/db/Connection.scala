package db

import com.typesafe.config.ConfigFactory

import java.sql.{Connection, DriverManager, SQLException}
import scala.util.{Failure, Success, Try}

object Connection {
  private val config = ConfigFactory.load("postgres.conf")

  private val url = config.getString("postgres.url")
  private val username = config.getString("postgres.username")
  private val password = config.getString("postgres.password")

  def connection(): Connection = {
    Try(DriverManager.getConnection(url, username, password)) match {
      case Success(conn) => conn
      case Failure(_) => throw new SQLException()
    }
  }
}
