package com.github.morikuni.locest.area.domain.repository

import com.github.morikuni.locest.area.domain.model.{Coordinate, Area, AreaId}
import com.github.morikuni.locest.area.domain.repository.impl.AreaRepositoryImpl
import com.github.morikuni.locest.util.{Repository, Transaction}

trait AreaRepository extends Repository[Area] {
  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction.successful(Area) 成功時
    *         Transaction.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def find(id: AreaId): Transaction[Session, Area]

  /** coordinate を含むエリアを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction.successful(AreaId) 成功時
    *         Transaction.failed(NoSuchElementException) coordinate を含むエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def findAreaContain(coordinate: Coordinate): Transaction[Session, AreaId]

  /** 全てのエリアを取得する
    *
    * @return Transaction.successful(List[AreaId]) 成功時
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  def all: Transaction[Session, List[AreaId]]
}

trait DependAreaRepository {
  def areaRepository: AreaRepository
}

trait InjectAreaRepository extends DependAreaRepository {
  override val areaRepository: AreaRepository = new AreaRepositoryImpl
}
