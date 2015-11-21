package com.github.morikuni.locest.util

trait Entity[Id <: Identifier[_], Prop <: Property] {
  val id: Id
  val property: Prop

  override def hashCode: Int = id.hashCode

  override def equals(other: Any): Boolean = other match {
    case that: Entity[_, _] => this.id == that.id
    case _ => false
  }
}
