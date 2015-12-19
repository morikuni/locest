package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.model.FrequencyInformation
import com.github.morikuni.locest.frequency.domain.service.FrequencyInformationService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object FrequencyInformationServiceHelper extends Mockito {
  def create(
    allFrequenciesOfWord: Future[List[FrequencyInformation]] = null,
    registerSentence: Future[Unit] = null
  ): FrequencyInformationService = {
    val serv = mock[FrequencyInformationService]
    serv.allFrequenciesOfWord(any)(any) returns allFrequenciesOfWord
    serv.registerSentence(any, any, any)(any) returns registerSentence
    serv
  }
}
