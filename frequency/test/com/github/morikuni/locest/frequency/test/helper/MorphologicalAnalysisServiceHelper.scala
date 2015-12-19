package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.application.dto.MorphemeDto
import com.github.morikuni.locest.frequency.application.service.MorphologicalAnalysisService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object MorphologicalAnalysisServiceHelper extends Mockito {
  def create(morphologicalAnalysis: Future[Seq[MorphemeDto]] = null): MorphologicalAnalysisService = {
    val serv = mock[MorphologicalAnalysisService]
    serv.morphologicalAnalysis(any) returns morphologicalAnalysis
    serv
  }
}
