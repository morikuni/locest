package com.github.morikuni.locest.util

/** Entity を識別する。
  *
  * @tparam A 識別子の型
  */
trait Identifier[A] {

  val value: A

  override def hashCode: Int = value.hashCode

  override def equals(other: Any): Boolean = other match {
    case that: Identifier[_] => this.value == that.value
    case _ => false
  }
}
