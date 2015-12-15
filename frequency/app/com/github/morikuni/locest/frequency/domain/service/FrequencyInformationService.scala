package com.github.morikuni.locest.frequency.domain.service

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformation, WordId}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import scala.concurrent.Future

trait FrequencyInformationService {
  /** 指定された単語についての全頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformation)) 成功時
    *         Future.failed(NoSuchElementException) 指定された単語IDがそんざいしない場合
    *         Future.failed(IOException) 入出力に失敗した場合
    */
  def allFrequenciesOfWord(wordId: WordId)(implicit ecp: ExecutionContextProvider): Future[List[FrequencyInformation]]
}

trait DependFrequencyInformationService {
  def frequencyInformationService: FrequencyInformationService
}