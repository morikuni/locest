package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.FrequencyInformationDto
import com.github.morikuni.locest.frequency.application.service.impl.FrequencyInformationSearchServiceImpl
import com.github.morikuni.locest.frequency.domain.model.{AreaId, FrequencyInformation, FrequencyInformationId, FrequencyInformationProperty, WordId}
import com.github.morikuni.locest.frequency.domain.service.FrequencyInformationService
import com.github.morikuni.locest.frequency.test.helper.{FrequencyInformationServiceHelper, InjectExecutionContextProviderHelper}
import java.io.IOException
import org.specs2.mutable.Specification
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class FrequencyInformationSearchServiceSpec extends Specification {
  def create(freqServ: FrequencyInformationService): FrequencyInformationSearchServiceImpl = new FrequencyInformationSearchServiceImpl with InjectExecutionContextProviderHelper {
    override def frequencyInformationService: FrequencyInformationService = freqServ
  }

  val wordId = WordId(1)
  val freqId = FrequencyInformationId(wordId, AreaId(2))
  val freq = FrequencyInformation(freqId, FrequencyInformationProperty(10))
  val freqId2 = FrequencyInformationId(wordId, AreaId(3))
  val freq2 = FrequencyInformation(freqId2, FrequencyInformationProperty(20))

  "searchByWordId" should {
    "return List(FrequencyInformationDto) successfully" in {
      val freqServ = FrequencyInformationServiceHelper.create(allFrequenciesOfWord = Future.successful(List(freq, freq2)))
      val service = create(freqServ)
      val expect = List(freq, freq2).map(FrequencyInformationDto.from)
      val result = service.searchByWordId(wordId.value)

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if FrequencyInformationService.allFrequenciesOfWord failed with IOException" in {
      val freqServ = FrequencyInformationServiceHelper.create(allFrequenciesOfWord = Future.failed(new IOException))
      val service = create(freqServ)
      val result = service.searchByWordId(wordId.value)

      Await.result(result, Duration.Inf) must throwA[IOException]
    }
  }
}
