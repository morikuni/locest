package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.application.dto.FrequencyInformationDto
import com.github.morikuni.locest.frequency.application.service.FrequencyInformationSearchService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object FrequencyInformationSearchServiceHelper extends Mockito {
  def create(searchByWordId: Future[List[FrequencyInformationDto]] = null): FrequencyInformationSearchService = {
    val serv = mock[FrequencyInformationSearchService]
    serv.searchByWordId(any) returns searchByWordId
    serv
  }
}
