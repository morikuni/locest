package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.repository.Session
import com.github.morikuni.locest.util.{TransactionManager, Transaction}
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}
import scalikejdbc.{DB, DBSession, ConnectionPool}

case class MySQLSession(session: DBSession) extends Session

object MySQLTransactionManager extends TransactionManager[Session] {
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

  def ask: Transaction[Session, DBSession] = Transaction { session =>
    session.asInstanceOf[MySQLSession].session
  }

  override def execute[A](transaction: Transaction[Session, A])(ctx: ExecutionContext): Future[A] = {
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
