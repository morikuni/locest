package com.github.morikuni.locest.util

import scala.collection.TraversableOnce
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.Builder
import scala.concurrent.{Future, ExecutionContext}

/** データの入出力処理を表す。
  *
  * @param run 入出力処理
  * @tparam S 入出力を行うための環境型
  * @tparam A 入出力によって生成される型
  */
class Transaction[-S <: Session, +A](val run: (S, ExecutionContext) => Future[A]) {
  def flatMap[B, XS <: S](f: A => Transaction[XS, B]): Transaction[XS, B] = Transaction { (e: XS, ctx: ExecutionContext) =>
    run(e, ctx).flatMap(a => f(a).run(e, ctx))(ctx)
  }
  def map[B](f: A => B): Transaction[S, B] = flatMap((a: A) => Transaction.successful(f(a)))
}

object Transaction {
  def apply[S <: Session, A](f: (S, ExecutionContext) => Future[A]): Transaction[S, A] = new Transaction(f)
  def apply[S <: Session, A](f: S => A): Transaction[S, A] = Transaction((s, ctx) => Future(f(s))(ctx))
  def successful[S <: Session, A](a: A): Transaction[S, A] = Transaction((_, _) => Future.successful(a))
  def failed[S <: Session, A](e: Throwable): Transaction[S, A] = Transaction((_, _) => Future.failed(e))
  def fromFuture[S <: Session, A](f: Future[A]): Transaction[S, A] = Transaction((_, _) => f)
  def sequence[S <: Session, A, M[X] <: TraversableOnce[X]](seq: M[Transaction[S, A]])(implicit cbf: CanBuildFrom[M[Transaction[S, A]], A, M[A]]): Transaction[S, M[A]] = {
    seq.foldLeft(Transaction.successful[S, Builder[A, M[A]]](cbf.apply(seq))) { (acc, tx) =>
      (for {
        ma <- acc
        a <- tx
      } yield (ma += a))
    }.map(_.result)
  }
}