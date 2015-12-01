package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.model.{Area, AreaId, Coordinate}
import com.github.morikuni.locest.area.domain.repository.{DependAreaRepositoryTransactionManager, AreaRepository, AreaRepositorySession, DependAreaRepository}
import com.github.morikuni.locest.util.{TransactionManager, Transaction}
import scala.concurrent.{Future, ExecutionContext}

class AreaRepositoryImpl extends AreaRepository {

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction.successful(Area) 成功時
    *         Transaction.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def find(id: AreaId): Transaction[AreaRepositorySession, Area] = ???

  /** coordinate を含むエリアのIDを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction.successful(AreaId) 成功時
    *         Transaction.failed(NoSuchElementException) coordinate を含むエリアが存在しないとき
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def findByCoordinate(coordinate: Coordinate): Transaction[AreaRepositorySession, AreaId] = ???

  /** 全てのエリアIDを取得する。
    *
    * @return Transaction.successful(List[AreaId]) 成功時
    *         Transaction.failed(IOException) 入出力に失敗したとき
    */
  override def all: Transaction[AreaRepositorySession, List[AreaId]] = ???
}

trait InjectAreaRepository extends DependAreaRepository {
  override val areaRepository: AreaRepository = new AreaRepositoryImpl
}

trait InjectAreaRepositoryTransactionManager extends DependAreaRepositoryTransactionManager {
  override def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession] = new TransactionManager[AreaRepositorySession] {
    override def execute[A](transaction: Transaction[AreaRepositorySession, A])(ctx: ExecutionContext): Future[A] =
      MySQLTransactionManager.execute(transaction)(ctx)
  }
}
