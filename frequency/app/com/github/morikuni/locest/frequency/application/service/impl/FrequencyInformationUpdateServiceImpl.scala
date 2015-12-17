package com.github.morikuni.locest.frequency.application.service.impl

import com.github.morikuni.locest.frequency.application.service.FrequencyInformationUpdateService
import scala.concurrent.Future

trait FrequencyInformationUpdateServiceImpl extends FrequencyInformationUpdateService {
  /** 文章を登録して頻度情報を更新する
    *
    * @param sentence 登録する文章
    * @param lat 緯度
    * @param lng 経度
    * @return Future.successful(())
    *         Future.failed(
    *         Future.failed(IOException)
    */
  override def registerSentence(sentence: String, lat: Double, lng: Double): Future[Unit] = {

  }
}
