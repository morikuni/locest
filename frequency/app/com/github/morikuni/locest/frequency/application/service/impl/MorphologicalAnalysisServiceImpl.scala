package com.github.morikuni.locest.frequency.application.service.impl

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.dto.MorphemeDto
import com.github.morikuni.locest.frequency.application.service.{DependMorphologicalAnalysisService, MorphologicalAnalysisService}
import com.github.morikuni.locest.frequency.domain.service.DependWordService
import com.github.morikuni.locest.frequency.domain.service.impl.InjectWordService
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import scala.concurrent.Future

trait MorphologicalAnalysisServiceImpl extends MorphologicalAnalysisService
  with DependWordService
  with DependExecutionContextProvider {
  /** 形態素解析をする
    *
    * @param sentence 形態素解析をする文章
    * @return Future.successful(Seq(MorphemeDto)) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def morphologicalAnalysis(sentence: String): Future[Seq[MorphemeDto]] = {
    wordService.morphologicalAnalysis(sentence)
      .map(MorphemeDto.fromMap)(executionContextProvider.default)
  }
}

trait InjectMorphologicalAnalysisService extends DependMorphologicalAnalysisService {
  override def morphologicalAnalysisService: MorphologicalAnalysisService =
    new MorphologicalAnalysisServiceImpl
      with InjectWordService
      with InjectExecutionContextProvider
}