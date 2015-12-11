package com.github.morikuni.locest.area.domain.repository

import com.github.morikuni.locest.area.domain.model.{Area, AreaId, Coordinate}
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait AreaRepositorySession extends Session

trait AreaRepository extends Repository[Area] {

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction(Some(Area)) 成功時
    *         Transaction(None) id に対応するエリアが存在しないとき
    */
  def find(id: AreaId): Transaction[AreaRepositorySession, Option[Area]]


  /** coordinate を含むエリアのIDを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction(Some(AreaId)) 成功時
    *         Transaction(None) coordinate を含むエリアが存在しないとき
    */
  def findByCoordinate(coordinate: Coordinate): Transaction[AreaRepositorySession, Option[AreaId]]

  /** 全てのエリアIDを取得する。
    *
    * @return Transaction(List[AreaId]) 成功時
    */
  def all: Transaction[AreaRepositorySession, List[AreaId]]
}

trait DependAreaRepository {
  def areaRepository: AreaRepository
}

trait DependAreaRepositoryTransactionManager {
  def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession]
}