package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.model.{Coordinate, Area, AreaId}
import com.github.morikuni.locest.area.domain.repository.{Session, AreaRepository}
import com.github.morikuni.locest.util.Transaction

class AreaRepositoryImpl extends AreaRepository {
  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction.successful(Area) 成功時
    *         Transaction.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def find(id: AreaId): Transaction[Session, Area] = ???

  /** coordinate を含むエリアを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction.successful(AreaId) 成功時
    *         Transaction.failed(NoSuchElementException) coordinate を含むエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def findAreaContain(coordinate: Coordinate): Transaction[Session, AreaId] = ???

  /** 全てのエリアを取得する
    *
    * @return Transaction.successful(List[AreaId]) 成功時
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def all: Transaction[Session, List[AreaId]] = ???
}
