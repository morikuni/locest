package com.github.morikuni.locest.frequency.domain.service

import com.github.morikuni.locest.frequency.domain.model.{Word, WordId}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import scala.concurrent.Future

trait WordService {
  /** 形態素解析をする
    *
    * @param sentence 形態素解析を刷る文書
    * @return
    */
  def morphologicalAnalysis(sentence: String)(implicit ecp: ExecutionContextProvider): Future[Map[Word, Int]]
}

trait DependWordService {
  def wordService: WordService
}