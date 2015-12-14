package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.FrequencyInformationDto
import scala.concurrent.Future

trait FrequencyInformationSearchService {
  /** 指定された単語についての全ての頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformationDto)) 成功時
    *         Future.failed(NoSuchElementException) 指定された単語が存在しない場合
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def searchByWordId(wordId: Int): Future[List[FrequencyInformationDto]]
}

trait DependFrequencyInformationSearchService {
  val frequencyInformationSearchService: FrequencyInformationSearchService
}