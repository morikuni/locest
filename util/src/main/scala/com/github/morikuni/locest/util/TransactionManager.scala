package com.github.morikuni.locest.util

import scala.concurrent.{ExecutionContext, Future}

trait TransactionManager[Env] {
  def execute[A](transaction: Transaction[Env, A])(ctx: ExecutionContext): Future[A]
}
