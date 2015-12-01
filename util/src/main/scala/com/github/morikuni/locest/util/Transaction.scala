package com.github.morikuni.locest.util

import scala.concurrent.{Future, ExecutionContext}

class Transaction[-Env, +A](val run: (Env, ExecutionContext) => Future[A]) {
  def flatMap[B, XEnv <: Env](f: A => Transaction[XEnv, B]): Transaction[XEnv, B] = Transaction { (e: XEnv, ctx: ExecutionContext) =>
    run(e, ctx).flatMap(a => f(a).run(e, ctx))(ctx)
  }
  def map[B](f: A => B): Transaction[Env, B] = flatMap((a: A) => Transaction.successful(f(a)))
}

object Transaction {
  def apply[Env, A](f: (Env, ExecutionContext) => Future[A]): Transaction[Env, A] = new Transaction(f)
  def apply[Env, A](f: Env => A): Transaction[Env, A] = Transaction((s, ctx) => Future(f(s))(ctx))
  def successful[Env, A](a: A): Transaction[Env, A] = Transaction((_, _) => Future.successful(a))
  def failed[Env, A](e: Throwable): Transaction[Env, A] = Transaction((_, _) => Future.failed(e))
  def fromFuture[Env, A](f: Future[A]): Transaction[Env, A] = Transaction((_, _) => f)
}