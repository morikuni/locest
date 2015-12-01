package com.github.morikuni.locest.util

import scala.concurrent.{ExecutionContext, Future}

/** Transaction の実行環境。
  *
  * @tparam Env Transactionに与えるパラメータ
  */
trait TransactionManager[+Env] {
  def execute[A](transaction: Transaction[Env, A])(ctx: ExecutionContext): Future[A]
}
