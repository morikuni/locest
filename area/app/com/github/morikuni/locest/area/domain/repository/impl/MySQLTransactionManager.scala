package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.repository.AreaRepositorySession
import com.github.morikuni.locest.util.{Session, Transaction}
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import scala.concurrent.Future
import scalikejdbc.{ConnectionPool, DBSession}

case class MySQLSession(override val session: DBSession) extends ScalikeJDBCSession with AreaRepositorySession

object MySQLTransactionManager extends ScalikeJDBCTransactionManager[MySQLSession] {
  Class.forName("com.mysql.jdbc.Driver")

  val (host, database, user, pass) = {
    val conf = ConfigFactory.load()
    (
      conf.getString("mysql.host"),
      conf.getString("mysql.database"),
      conf.getString("mysql.user"),
      conf.getString("mysql.password")
    )
  }
  ConnectionPool.add('mysql, s"jdbc:mysql://${host}/${database}", user, pass)

  override def borrow: Connection = ConnectionPool.borrow('mysql)

  override def wrap(session: DBSession): MySQLSession = MySQLSession(session)
}
