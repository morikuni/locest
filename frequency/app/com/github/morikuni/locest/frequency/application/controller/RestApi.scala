package com.github.morikuni.locest.frequency.application.controller

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.dto.ErrorDto
import com.github.morikuni.locest.frequency.application.service.impl.{InjectCountService, InjectFrequencyInformationSearchService}
import com.github.morikuni.locest.frequency.application.service.{DependCountService, DependFrequencyInformationSearchService}
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import java.io.IOException
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext


trait RestApi extends Controller
  with DependFrequencyInformationSearchService
  with DependCountService
  with DependExecutionContextProvider {

  implicit lazy val ec: ExecutionContext = executionContextProvider.default

  def frequencies_word(wordId: Int) = Action.async {
    frequencyInformationSearchService.searchByWordId(wordId)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto(s"No word has such id.")))
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }

  def count_all = Action.async {
    countService.totalNumberOfCount
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }
}

object RestApi extends RestApi
  with InjectFrequencyInformationSearchService
  with InjectCountService
  with InjectExecutionContextProvider