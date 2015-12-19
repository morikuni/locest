package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.application.service.FrequencyInformationUpdateService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object FrequencyInformationUpdateServiceHelper extends Mockito {
  def create(registerSentence: Future[Unit] = null): FrequencyInformationUpdateService = {
    val serv = mock[FrequencyInformationUpdateService]
    serv.registerSentence(any, any, any) returns registerSentence
    serv
  }
}
