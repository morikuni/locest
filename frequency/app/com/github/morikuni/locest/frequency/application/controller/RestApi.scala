package com.github.morikuni.locest.frequency.application.controller

import com.github.morikuni.locest.frequency.application.dto.ErrorDto
import com.github.morikuni.locest.frequency.application.service.DependFrequencyInformationSearchService
import com.github.morikuni.locest.frequency.application.service.impl.InjectFrequencyInformationSearchService
import com.github.morikuni.locest.frequency.domain.model.WordId
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectAreaRepositoryTransactionManager, InjectAreaRepository}
import java.io.IOException
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}


trait RestApi extends Controller
  with DependFrequencyInformationSearchService {
  implicit val ctx =  play.api.libs.concurrent.Execution.defaultContext

  def frequencies_word(wordId: Int) = Action.async {
    frequencyInformationSearchService.searchByWordId(wordId)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto(s"No word has such id.")))
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }
}

object RestApi extends RestApi
  with InjectFrequencyInformationSearchService