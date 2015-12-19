package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.util.{Session, Transaction, TransactionManager}
import org.specs2.mock.Mockito
import scala.concurrent.{ExecutionContext, Future}

object TransactionManagerHelper extends Mockito {
  def emptyTransactionManager[S <: Session]: TransactionManager[S] = new TransactionManager[S] {
    override def execute[A](transaction: Transaction[S, A])(ctx: ExecutionContext): Future[A] = transaction.run(null.asInstanceOf[S], ctx)
  }
}