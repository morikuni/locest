package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.CountDto
import com.github.morikuni.locest.frequency.application.service.impl.CountServiceImpl
import com.github.morikuni.locest.frequency.domain.repository.{FrequencyInformationRepositorySession, FrequencyInformationRepository}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import com.github.morikuni.locest.frequency.test.helper.{FrequencyInformationRepositoryHelper, TransactionManagerHelper, InjectExecutionContextProviderHelper}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import java.io.IOException
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CountServiceSpec extends Specification {
  def create(freqRepo: FrequencyInformationRepository): CountServiceImpl = new CountServiceImpl with InjectExecutionContextProviderHelper {
    override def frequencyInformationRepository: FrequencyInformationRepository = freqRepo

    override def frequencyInformationRepositoryTransactionManger: TransactionManager[FrequencyInformationRepositorySession] = TransactionManagerHelper.emptyTransactionManager
  }

  "totalNumberOfCount" should {
    "return CountDto successfully" in {
      val freqRepo = FrequencyInformationRepositoryHelper.create(sumOfAllCounts = Transaction.successful(10))
      val service = create(freqRepo)
      val expect = CountDto(10)
      val result = service.totalNumberOfCount

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if FrequencyInformationRepository.sumOfAllCounts failed with IOException" in {
      val freqRepo = FrequencyInformationRepositoryHelper.create(sumOfAllCounts = Transaction.failed(new IOException))
      val service = create(freqRepo)
      val result = service.totalNumberOfCount

      Await.result(result, Duration.Inf) must throwA[IOException]
    }

  }
}
