package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.application.dto.CountDto
import com.github.morikuni.locest.frequency.application.service.CountService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object CountServiceHelper extends Mockito {
  def create(totalNumberOfCount: Future[CountDto] = null): CountService = {
    val serv = mock[CountService]
    serv.totalNumberOfCount returns totalNumberOfCount
    serv
  }
}
