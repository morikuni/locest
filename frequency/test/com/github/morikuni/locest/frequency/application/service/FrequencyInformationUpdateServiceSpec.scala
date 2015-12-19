package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.service.impl.FrequencyInformationUpdateServiceImpl
import com.github.morikuni.locest.frequency.domain.service.FrequencyInformationService
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import com.github.morikuni.locest.frequency.test.helper.{FrequencyInformationServiceHelper, InjectExecutionContextProviderHelper}
import java.io.IOException
import org.specs2.mutable.Specification
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class FrequencyInformationUpdateServiceSpec extends Specification {
  def create(freqServ: FrequencyInformationService): FrequencyInformationUpdateServiceImpl = new FrequencyInformationUpdateServiceImpl with InjectExecutionContextProviderHelper {
    override def frequencyInformationService: FrequencyInformationService = freqServ
  }

  "registerSentence" should {
    "return () successfully" in {
      val freqServ = FrequencyInformationServiceHelper.create(registerSentence = Future.successful(()))
      val service = create(freqServ)
      val expect = ()
      val result = service.registerSentence("test", 34, 134)

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if FrequencyInformationService.registerSentence failed with IOException" in {
      val freqServ = FrequencyInformationServiceHelper.create(registerSentence = Future.failed(new IOException))
      val service = create(freqServ)
      val result = service.registerSentence("test", 34, 134)

      Await.result(result, Duration.Inf) must throwA[IOException]
    }
  }
}
