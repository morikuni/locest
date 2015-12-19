package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.model.{WordId, FrequencyInformation}
import com.github.morikuni.locest.frequency.domain.repository.{FrequencyInformationRepositorySession, FrequencyInformationRepository}
import com.github.morikuni.locest.frequency.domain.repository.impl.FrequencyInformationRepositoryImpl
import com.github.morikuni.locest.util.Transaction
import org.specs2.mock.Mockito

object FrequencyInformationRepositoryHelper extends Mockito {
  def create(
    findByWordId: Transaction[FrequencyInformationRepositorySession, List[FrequencyInformation]] = null,
    sumOfAllCounts: Transaction[FrequencyInformationRepositorySession, Long] = null,
    add: Transaction[FrequencyInformationRepositorySession, Unit] = null
  ): FrequencyInformationRepository = {
    val repo = mock[FrequencyInformationRepository]
    repo.findByWordId(any) returns findByWordId
    repo.sumOfAllCounts returns sumOfAllCounts
    repo.add(any) returns add
    repo
  }
}
