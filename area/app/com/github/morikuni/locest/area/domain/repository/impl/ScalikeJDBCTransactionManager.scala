package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.util.{Session, Transaction, TransactionManager}
import java.io.IOException
import java.sql.Connection
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc.{DB, DBSession}

trait ScalikeJDBCSession { val session: DBSession }

trait ScalikeJDBCTransactionManager[S <: Session with ScalikeJDBCSession] extends TransactionManager[S] {

  val logger = Logger(getClass)

  /** Transaction を実行する。
  *
  * @param transaction 実行対象
  * @param ctx 実行コンテキスト
  * @tparam A Transaction が返却する型
  * @return Future.successful(A) Transaction の実行に成功した場合
  *         Future.failed(IOException) Transaction の実行に失敗した場合
  */
  override def execute[A](transaction: Transaction[S, A])(ctx: ExecutionContext): Future[A] = {
    implicit val db = DB(borrow)
    try {
      val future = db.futureLocalTx { session =>
        transaction.run(wrap(session), ctx)
      }(ec = ctx)
      future.recoverWith {
        case e: Throwable =>
          logger.error("ScalikeJDBC error in recoverWith block", e)
          Future.failed(new IOException(e))
      }(ctx)
    } catch {
      case e: Throwable =>
        logger.error("ScalikeJDBC error in catch block", e)
        Future.failed(new IOException(e))
    }
  }

  def borrow: Connection

  def wrap(session: DBSession): S

  def ask[XS >: S <: Session]: Transaction[XS, DBSession] = Transaction { (xs, _) =>
    Future.successful(xs.asInstanceOf[S].session)
  }
}
