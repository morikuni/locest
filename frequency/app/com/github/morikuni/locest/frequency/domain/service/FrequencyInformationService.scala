package com.github.morikuni.locest.frequency.domain.service

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformation, WordId}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import scala.concurrent.Future

trait FrequencyInformationService {
  /** 指定された単語についての全頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformation)) 成功時
    *         Future.failed(IOException) 入出力に失敗した場合
    */
  def allFrequenciesOfWord(wordId: WordId)(implicit ecp: ExecutionContextProvider): Future[List[FrequencyInformation]]

  /** 指定された文章を指定された座標のエリアに登録する
    *
    * @param sentence 登録する文章
    * @param lat 緯度
    * @param lng 経度
    * @return Future.successful(()) 成功時
    *         Future.failed(NoSuchElementException)
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def registerSentence(sentence: String, lat: Double, lng: Double)(implicit ecp: ExecutionContextProvider): Future[Unit]
}

trait DependFrequencyInformationService {
  def frequencyInformationService: FrequencyInformationService
}