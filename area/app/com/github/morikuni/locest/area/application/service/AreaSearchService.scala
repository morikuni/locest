package com.github.morikuni.locest.area.application.service

import com.github.morikuni.locest.area.application.dto.{AreaIdDto, AreaDto}
import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, InjectDefaultExecutionContextProvider}
import com.github.morikuni.locest.area.domain.model.{Area, AreaId}
import com.github.morikuni.locest.area.domain.repository.DependAreaRepository
import com.github.morikuni.locest.area.domain.repository.impl.InjectAreaRepository
import scala.concurrent.Future

trait AreaSearchService {
  /** 全てのエリアIDを取得する。
    *
    * @return Future.successful(List[AreaIdDto]) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def allAreaId: Future[List[AreaIdDto]]

  /** 指定された座標を含むエリアのIDを取得する。
    *
    * @param lat 検索に用いる緯度
    * @param lng 検索に用いる経度
    * @return Future.successful(AreaIdDto) 成功時
    *         Future.failed(NoSuchElementException) 指定座標を含むエリアが存在しないとき
    *         Future.failed(IllegalArgumentException) lat か lng が不正な値だった場合
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def searchIdOfAreaContain(lat: Double, lng: Double): Future[AreaIdDto]

  /** id に対応するエリアを取得する。
    *
    * @param id 取得するID
    * @return Future.successful(AreaDto) 成功時
    *         Future.failed(NoSuchElementException) id に対応するエリアが存在しないとき
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def search(id: Int): Future[AreaDto]
}

trait DependAreaSearchService {
  def areaSearchService: AreaSearchService
}