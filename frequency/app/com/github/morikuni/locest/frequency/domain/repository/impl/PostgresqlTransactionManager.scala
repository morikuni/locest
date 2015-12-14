package com.github.morikuni.locest.frequency.domain.repository.impl

import com.github.morikuni.locest.frequency.domain.repository.FrequencyInformationRepositorySession
import com.github.morikuni.locest.util.{Session, Transaction, TransactionManager}
import com.typesafe.config.ConfigFactory
import java.io.IOException
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc.{ConnectionPool, DB, DBSession}

case class PostgreSQLSession(val session: DBSession) extends FrequencyInformationRepositorySession

object PostgreSQLTransactionManager extends TransactionManager[PostgreSQLSession] {
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
  ConnectionPool.singleton(s"jdbc:postgresql://${host}:${port}/${database}", user, pass)

  val logger = Logger(getClass)

  /** Transaction を実行する。
    *
    * @param transaction 実行対象
    * @param ctx 実行コンテキスト
    * @tparam A Transaction が返却する型
    * @return Future.successful(A) Transaction の実行に成功した場合
    *         Future.failed(IOException) Transaction の実行に失敗した場合
    */
  override def execute[A](transaction: Transaction[PostgreSQLSession, A])(ctx: ExecutionContext): Future[A] = {
    try {
      implicit val db = DB(ConnectionPool.borrow())
      val future = db.futureLocalTx { session =>
        transaction.run(PostgreSQLSession(session), ctx)
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

  def ask[XS >: PostgreSQLSession <: Session]: Transaction[XS, DBSession] = Transaction { (xs, _) =>
    Future.successful(xs.asInstanceOf[PostgreSQLSession].session)
  }
}