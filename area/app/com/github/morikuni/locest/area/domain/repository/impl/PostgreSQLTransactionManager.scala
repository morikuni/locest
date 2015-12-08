package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.repository.AreaRepositorySession
import com.github.morikuni.locest.util.{Transaction, Session, TransactionManager}
import com.typesafe.config.ConfigFactory
import java.io.IOException
import java.sql.Connection
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalikejdbc.{DB, DBSession, ConnectionPool}

case class PostgreSQLSession(override val session: DBSession) extends ScalikeJDBCSession with AreaRepositorySession

object PostgreSQLTransactionManager extends ScalikeJDBCTransactionManager[PostgreSQLSession] {
  Class.forName("org.postgresql.Driver")

  val (host, port, database, user, pass) = {
    val conf = ConfigFactory.load()
    (
      conf.getString("postgresql.host"),
      conf.getString("postgresql.port"),
      conf.getString("postgresql.database"),
      conf.getString("postgresql.user"),
      conf.getString("postgresql.password")
      )
  }
  ConnectionPool.add('postgresql ,s"jdbc:postgresql://${host}:${port}/${database}", user, pass)

  def ask[A >: PostgreSQLSession <: Session]: Transaction[A, DBSession] = Transaction { (a, _) =>
    Future.successful(a.asInstanceOf[PostgreSQLSession].session)
  }

  override def borrow: Connection = ConnectionPool.borrow('postgresql)

  override def wrap(session: DBSession): PostgreSQLSession = PostgreSQLSession(session)
}