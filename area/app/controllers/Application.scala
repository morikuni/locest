package controllers

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto, ErrorDto}
import com.github.morikuni.locest.area.application.service.DependAreaSearchService
import com.github.morikuni.locest.area.application.service.impl.InjectAreaSearchService
import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, InjectDefaultExecutionContextProvider}
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
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto("no area has such ID.")))
      }
  }

  def areas_ids = Action.async {
    areaSearchService.allAreaId.map(dto => Ok(Json.toJson(dto)))
  }

  def areas_ids_coordinate(lat: Double, lng: Double) = Action.async {
    areaSearchService.searchIdOfAreaContain(lat, lng)
      .map(dto => Ok(Json.toJson(dto)))
      .recover {
        case _: NoSuchElementException => NotFound(Json.toJson(ErrorDto("no area contains such (lat, lng).")))
        case _: IllegalArgumentException => BadRequest(Json.toJson(ErrorDto("lat or lng is out of range.")))
      }
  }

}

object Application extends Application
  with InjectAreaSearchService
  with InjectDefaultExecutionContextProvider
