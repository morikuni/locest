package com.github.morikuni.locest.util

/** 永続化するデータを表す。
  *
  * @tparam Id 識別子の型
  * @tparam Prop 扱う属性の型
  */
trait Entity[Id <: Identifier[_], Prop <: Property] {
  val id: Id
  val property: Prop

  override def hashCode: Int = id.hashCode

  override def equals(other: Any): Boolean = other match {
    case that: Entity[_, _] => this.id == that.id
    case _ => false
  }
}
