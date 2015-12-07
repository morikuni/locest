package com.github.morikuni.locest.util

import scala.concurrent.{ExecutionContext, Future}

/** Transaction の実行環境。
  *
  * @tparam S Transactionに与えるパラメータ
  */
trait TransactionManager[+S <: Session] {
  def execute[A](transaction: Transaction[S, A])(ctx: ExecutionContext): Future[A]
}
