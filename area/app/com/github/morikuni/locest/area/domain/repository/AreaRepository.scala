package com.github.morikuni.locest.area.domain.repository

import com.github.morikuni.locest.area.domain.model.{Area, AreaId, Coordinate}
import com.github.morikuni.locest.util.{TransactionManager, Repository, Transaction}

trait AreaRepositorySession

trait AreaRepository extends Repository[Area] {

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction.successful(Area) 成功時
    *         Transaction.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def find(id: AreaId): Transaction[AreaRepositorySession, Area]


  /** coordinate を含むエリアのIDを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction.successful(AreaId) 成功時
    *         Transaction.failed(NoSuchElementException) coordinate を含むエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def findByCoordinate(coordinate: Coordinate): Transaction[AreaRepositorySession, AreaId]

  /** 全てのエリアIDを取得する。
    *
    * @return Transaction.successful(List[AreaId]) 成功時
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def all: Transaction[AreaRepositorySession, List[AreaId]]
}

trait DependAreaRepository {
  def areaRepository: AreaRepository
}

trait DependAreaRepositoryTransactionManager {
  def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession]
}