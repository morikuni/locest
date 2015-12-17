package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.MorphemeDto
import scala.concurrent.Future

trait MorphologicalAnalysisService {
  /** 形態素解析をする
    *
    * @param sentence 形態素解析をする文章
    * @return Future.successful(Seq(MorphemeDto)) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def morphologicalAnalysis(sentence: String): Future[Seq[MorphemeDto]]
}

trait DependMorphologicalAnalysisService {
  def morphologicalAnalysisService: MorphologicalAnalysisService
}