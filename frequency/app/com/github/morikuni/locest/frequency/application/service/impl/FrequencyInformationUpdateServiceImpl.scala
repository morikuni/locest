package com.github.morikuni.locest.frequency.application.service.impl

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.service.{DependFrequencyInformationUpdateService, FrequencyInformationUpdateService}
import com.github.morikuni.locest.frequency.domain.service.DependFrequencyInformationService
import com.github.morikuni.locest.frequency.domain.service.impl.InjectFrequencyInformationService
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import scala.concurrent.Future

trait FrequencyInformationUpdateServiceImpl extends FrequencyInformationUpdateService
  with DependFrequencyInformationService
  with DependExecutionContextProvider {
  /** 文章を登録して頻度情報を更新する
    *
    * @param sentence 登録する文章
    * @param lat 緯度
    * @param lng 経度
    * @return Future.successful(())
    *         Future.failed(NoSuchElementException)
    *         Future.failed(IOException)
    */
  override def registerSentence(sentence: String, lat: Double, lng: Double): Future[Unit] = {
    frequencyInformationService.registerSentence(sentence, lat, lng)
  }
}

trait InjectFrequencyInformationUpdateService extends DependFrequencyInformationUpdateService {
  override def frequencyInformationUpdateService: FrequencyInformationUpdateService =
    new FrequencyInformationUpdateServiceImpl
      with InjectFrequencyInformationService
      with InjectExecutionContextProvider
}