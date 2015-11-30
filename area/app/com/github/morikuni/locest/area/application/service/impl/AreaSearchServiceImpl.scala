package com.github.morikuni.locest.area.application.service.impl

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto}
import com.github.morikuni.locest.area.application.service.{AreaSearchService, DependAreaSearchService}
import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, InjectDefaultExecutionContextProvider}
import com.github.morikuni.locest.area.domain.model.{Area, AreaId, Coordinate}
import com.github.morikuni.locest.area.domain.repository.DependAreaRepository
import com.github.morikuni.locest.area.domain.repository.impl.InjectAreaRepository
import scala.concurrent.{ExecutionContext, Future}

trait AreaSearchServiceImpl extends AreaSearchService
  with DependAreaRepository
  with DependExecutionContextProvider {

  implicit lazy val ctx: ExecutionContext = executionContextProvider.default

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Future.successful(AreaDto) 成功時
    *         Future.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def search(id: Int): Future[AreaDto] =
    areaRepositoryTransactionManager.execute(areaRepository.find(AreaId(id)))(executionContextProvider.repository)
      .map(AreaDto.from)

  /** 指定された座標を含むエリアのIDを取得する。
    *
    * @param lat 検索に用いる緯度
    * @param lng 検索に用いる経度
    * @return Future.successful(AreaIdDto) 成功時
    *         Future.failed(NoSuchElementException) 指定座標を含むエリアが存在しないとき
    *         Future.failed(IllegalArgumentException) lat か lng が不正な値だった場合
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def searchIdOfAreaContain(lat: Double, lng: Double): Future[AreaIdDto] =
    for {
      coordinate <- Future.fromTry(Coordinate.create(lat, lng))
      areaId <- areaRepositoryTransactionManager.execute(areaRepository.findByCoordinate(coordinate))(executionContextProvider.repository)
    } yield AreaIdDto.from(areaId)


  /** 全てのエリアIDを取得する。
    *
    * @return Future.successful(List[AreaIdDto]) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def allAreaId: Future[List[AreaIdDto]] =
    areaRepositoryTransactionManager.execute(areaRepository.all)(executionContextProvider.repository)
      .map(_.map(AreaIdDto.from))
}

trait InjectAreaSearchService extends DependAreaSearchService {
  override val areaSearchService: AreaSearchService =
    new AreaSearchServiceImpl
      with InjectAreaRepository
      with InjectDefaultExecutionContextProvider
}