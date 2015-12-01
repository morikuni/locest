package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.repository.AreaRepositorySession
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalikejdbc.{ConnectionPool, DB, DBSession}

case class MySQLSession(val session: DBSession) extends AreaRepositorySession

object MySQLTransactionManager extends TransactionManager[MySQLSession] {
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
  ConnectionPool.singleton(s"jdbc:mysql://${host}/${database}", user, pass)

  def ask: Transaction[MySQLSession, DBSession] = Transaction { session =>
    session.session
  }

  override def execute[A](transaction: Transaction[MySQLSession, A])(ctx: ExecutionContext): Future[A] = {
    implicit val db = DB(ConnectionPool.borrow())
    try {
      db.begin()
      val future = DB withinTx { session =>
        transaction.run(MySQLSession(session), ctx)
      }
      future.andThen {
        case Success(_) => db.commit()
        case Failure(e) => e match {
          case _: IOException => db.rollback()
        }
      }(ctx).andThen {
        case _ => db.close()
      }(ctx)
    } catch {
      case e: Exception =>
        db.rollbackIfActive()
        db.close()
        throw e
    }
  }
}
