package com.github.morikuni.locest.frequency.domain.repository

import com.github.morikuni.locest.frequency.domain.model.AreaId
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait AreaRepositorySession extends Session

trait AreaRepository extends Repository[AreaId] {
  /** 対象座標を含むエリアを取得する
    *
    * @param lat 緯度
    * @param lng 経度
    * @return Transacion(Some(AreaId)) 成功時
    *         Transaction(None) 対象座標を含むエリアが存在しなかったとき
    */
  def findByCoordinate(lat: Double, lng: Double): Transaction[AreaRepositorySession, Option[AreaId]]
}

trait DependAreaRepository {
  def areaRepository: AreaRepository
}

trait DependAreaRepositoryTransactionManager {
  def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession]
}
