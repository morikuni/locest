package com.github.morikuni.locest.util

/** Entity の永続化を行う。
  *
  * @tparam XEntity 管理する Entity の型
  */
trait Repository[XEntity <: Entity[_, _]]
