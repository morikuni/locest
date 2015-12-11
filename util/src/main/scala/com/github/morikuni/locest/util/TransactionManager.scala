package com.github.morikuni.locest.util

import scala.concurrent.{ExecutionContext, Future}

/** Transaction の実行環境。
  *
  * @tparam S Transactionに与えるパラメータ
  */
trait TransactionManager[+S <: Session] {
  /** Transaction を実行する。
    *
    * @param transaction 実行対象
    * @param ctx 実行コンテキスト
    * @tparam A Transaction が返却する型
    * @return Future.successful(A) Transaction の実行に成功した場合
    *         Future.failed(IOException) Transaction の実行に失敗した場合
    */
  def execute[A](transaction: Transaction[S, A])(ctx: ExecutionContext): Future[A]
}
