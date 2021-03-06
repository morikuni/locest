package com.github.morikuni.locest.frequency.application.controller

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.dto.ErrorDto
import com.github.morikuni.locest.frequency.application.service.impl.{InjectFrequencyInformationUpdateService, InjectMorphologicalAnalysisService, InjectCountService, InjectFrequencyInformationSearchService}
import com.github.morikuni.locest.frequency.application.service.{DependFrequencyInformationUpdateService, DependMorphologicalAnalysisService, DependCountService, DependFrequencyInformationSearchService}
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import java.io.IOException
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.{Future, ExecutionContext}


trait RestApi extends Controller
  with DependFrequencyInformationSearchService
  with DependCountService
  with DependMorphologicalAnalysisService
  with DependFrequencyInformationUpdateService
  with DependExecutionContextProvider {

  implicit lazy val ec: ExecutionContext = executionContextProvider.default

  def frequencies_word(wordId: Int) = Action.async {
    frequencyInformationSearchService.searchByWordId(wordId)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
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

  def morphemes(sentence: String) = Action.async {
    morphologicalAnalysisService.morphologicalAnalysis(sentence)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }

  def register_sentence(sentence: String, lat: Double, lng: Double) = Action.async {
    frequencyInformationUpdateService.registerSentence(sentence, lat, lng)
      .map(_ => Ok)
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto(s"No area has such coordinate.")))
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }
}

object RestApi extends RestApi
  with InjectFrequencyInformationSearchService
  with InjectCountService
  with InjectMorphologicalAnalysisService
  with InjectFrequencyInformationUpdateService
  with InjectExecutionContextProvider