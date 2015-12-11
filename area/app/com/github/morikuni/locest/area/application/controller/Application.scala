package com.github.morikuni.locest.area.application.controller

import com.github.morikuni.locest.area.application.dto.ErrorDto
import com.github.morikuni.locest.area.application.service.DependAreaSearchService
import com.github.morikuni.locest.area.application.service.impl.InjectAreaSearchService
import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, InjectDefaultExecutionContextProvider}
import java.io.IOException
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext

trait Application extends Controller
  with DependAreaSearchService
  with DependExecutionContextProvider {

  implicit lazy val ctx: ExecutionContext = executionContextProvider.default

  def areas(id: Int) = Action.async {
    areaSearchService.search(id)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto(s"no area has such id.")))
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }

  def areas_ids = Action.async {
    areaSearchService.allAreaId.map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }

  def areas_ids_coordinate(lat: Double, lng: Double) = Action.async {
    areaSearchService.searchIdOfAreaContain(lat, lng)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto(s"no area contains such coordinate.")))
        case _: IllegalArgumentException => BadRequest(Json.toJson(ErrorDto("lattitude or lngitude is out of range.")))
        case _: IOException => InternalServerError(Json.toJson(ErrorDto.internalServerError))
      }
  }

}

object Application extends Application
  with InjectAreaSearchService
  with InjectDefaultExecutionContextProvider
