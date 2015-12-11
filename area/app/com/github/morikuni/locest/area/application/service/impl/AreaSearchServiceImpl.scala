package com.github.morikuni.locest.area.application.service.impl

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto}
import com.github.morikuni.locest.area.application.service.{AreaSearchService, DependAreaSearchService}
import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, InjectDefaultExecutionContextProvider}
import com.github.morikuni.locest.area.domain.model.{AreaId, Coordinate}
import com.github.morikuni.locest.area.domain.repository.impl.{InjectAreaRepository, InjectAreaRepositoryTransactionManager}
import com.github.morikuni.locest.area.domain.repository.{DependAreaRepository, DependAreaRepositoryTransactionManager}
import scala.concurrent.{ExecutionContext, Future}

trait AreaSearchServiceImpl extends AreaSearchService
  with DependAreaRepository
  with DependAreaRepositoryTransactionManager
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
    for {
      areaOption <- areaRepositoryTransactionManager.execute(areaRepository.find(AreaId(id)))(executionContextProvider.repository)
      areaDto <- areaOption.fold[Future[AreaDto]](
        Future.failed(new NoSuchElementException("No area has such id"))
      )(
        area => Future.successful(AreaDto.from(area))
      )
    } yield areaDto

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
      areaIdOption <- areaRepositoryTransactionManager.execute(areaRepository.findByCoordinate(coordinate))(executionContextProvider.repository)
      areaIdDto <- areaIdOption.fold[Future[AreaIdDto]](
        Future.failed(new NoSuchElementException("No area contains such coordinate"))
      )(
        areaId => Future.successful(AreaIdDto.from(areaId))
      )
    } yield areaIdDto


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
      with InjectAreaRepositoryTransactionManager
      with InjectDefaultExecutionContextProvider
}