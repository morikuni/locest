package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.util.{Session, TransactionManager, Transaction}
import java.io.IOException
import java.sql.Connection
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}
import scalikejdbc.{DBSession, ConnectionPool, DB}

trait ScalikeJDBCSession { val session: DBSession }

trait ScalikeJDBCTransactionManager[S <: Session with ScalikeJDBCSession] extends TransactionManager[S] {
  override def execute[A](transaction: Transaction[S, A])(ctx: ExecutionContext): Future[A] = {
    implicit val db = DB(borrow)
    try {
      db.begin()
      val future = DB withinTx { session =>
        transaction.run(wrap(session), ctx)
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

  def borrow: Connection

  def wrap(session: DBSession): S
}
