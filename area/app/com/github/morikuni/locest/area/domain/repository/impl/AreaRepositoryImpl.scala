package com.github.morikuni.locest.area.domain.repository.impl

import com.github.morikuni.locest.area.domain.model.{Area, AreaId, AreaProperty, Coordinate}
import com.github.morikuni.locest.area.domain.repository.{AreaRepository, AreaRepositorySession, DependAreaRepository, DependAreaRepositoryTransactionManager}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc.scalikejdbcSQLInterpolationImplicitDef

class AreaRepositoryImpl extends AreaRepository {

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Transaction(Some(Area)) 成功時
    *         Transaction(None) id に対応するエリアが存在しないとき
    */
  override def find(id: AreaId): Transaction[AreaRepositorySession, Option[Area]] = PostgreSQLTransactionManager.ask.map { implicit session =>
    sql"SELECT area_id, (prefecture || ' ' || area.city), ST_Y(center), ST_X(center) FROM area WHERE area_id = ?"
      .bind(id.value)
      .map { r =>
        val aid = AreaId(r.int(1))
        val name = r.string(2)
        val lat = r.double(3)
        val lng = r.double(4)
        val coordinate = Coordinate.createUnsafe(lat, lng)
        Area(aid, AreaProperty(name, coordinate))
      }.single.apply
  }

  /** 全てのエリアIDを取得する。
    *
    * @return Transaction(List[AreaId]) 成功時
    */
  override def all: Transaction[AreaRepositorySession, List[AreaId]] = PostgreSQLTransactionManager.ask.map{ implicit session =>
    sql"SELECT area_id FROM area".map(r => AreaId(r.int(1))).list.apply
  }

  /** coordinate を含むエリアのIDを取得する。
    *
    * @param coordinate 検索に用いる座標
    * @return Transaction(Some(AreaId)) 成功時
    *         Transaction(None) coordinate を含むエリアが存在しないとき
    */
  override def findByCoordinate(coordinate: Coordinate): Transaction[AreaRepositorySession, Option[AreaId]] = PostgreSQLTransactionManager.ask.map{ implicit session =>
    sql"SELECT area_id FROM shape WHERE ST_Intersects(shape, ST_GeomFromText(?, 4326))"
      .bind(s"POINT(${coordinate.lat} ${coordinate.lng})")
      .map(r => AreaId(r.int(1))).first.apply
  }

}

trait InjectAreaRepository extends DependAreaRepository {
  override val areaRepository: AreaRepository = new AreaRepositoryImpl
}

trait InjectAreaRepositoryTransactionManager extends DependAreaRepositoryTransactionManager {
  override def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession] = new TransactionManager[AreaRepositorySession] {
    override def execute[A](transaction: Transaction[AreaRepositorySession, A])(ctx: ExecutionContext): Future[A] =
      PostgreSQLTransactionManager.execute(transaction)(ctx)
  }
}
