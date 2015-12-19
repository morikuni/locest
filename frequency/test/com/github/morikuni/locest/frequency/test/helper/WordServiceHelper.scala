package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.model.Word
import com.github.morikuni.locest.frequency.domain.service.WordService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object WordServiceHelper extends Mockito {
  def create(morphologicalAnalysis: Future[Map[Word, Int]] = null): WordService = {
    val serv = mock[WordService]
    serv.morphologicalAnalysis(any)(any) returns morphologicalAnalysis
    serv
  }
}
