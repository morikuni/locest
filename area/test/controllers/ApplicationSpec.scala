package controllers

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto}
import com.github.morikuni.locest.area.application.service.AreaSearchService
import com.github.morikuni.locest.area.domain.model.{Area, AreaId, AreaProperty, Coordinate}
import java.io.IOException
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification}
import scala.concurrent.Future
import test.helper.{AreaSearchSearviceHelper, InjectExecutionContextProviderHelper}



class ApplicationSpec extends PlaySpecification with Mockito {

  def create(service: AreaSearchService): Application = new Application with InjectExecutionContextProviderHelper {
    override def areaSearchService: AreaSearchService = service
  }

  val areaId = AreaId(1)
  val coordinate = Coordinate.createUnsafe(30, 130)
  val area = Area(areaId, AreaProperty("name", coordinate))
  val areaId2 = AreaId(2)
  val areaIdDto = AreaIdDto.from(areaId)
  val areaIdDto2 = AreaIdDto.from(areaId2)
  val areaDto = AreaDto.from(area)

  "areas" should {
    "return json successfully" in {
      val service = AreaSearchSearviceHelper.createMock(search = Future.successful(areaDto))
      val app = create(service)
      val expect = Json.toJson(areaDto).toString()
      val result = app.areas(areaId.value).apply(FakeRequest())

      status(result) must be equalTo OK
      contentAsString(result) must be equalTo expect
    }

    "return 'Internal Server Error' response if AreaSearchService.search failed with IOException" in {
      val service = AreaSearchSearviceHelper.createMock(search = Future.failed(new IOException))
      val app = create(service)
      val expect = INTERNAL_SERVER_ERROR
      val result = app.areas(areaId.value).apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).search(areaId.value)
    }

    "return 'Not Found' response if AreaSearchService.search failed with NoSuchElementException" in {
      val service = AreaSearchSearviceHelper.createMock(search = Future.failed(new NoSuchElementException))
      val app = create(service)
      val expect = NOT_FOUND
      val result = app.areas(areaId.value).apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).search(areaId.value)
    }
  }

  "areas_ids" should {
    "return json successfully" in {
      val service = AreaSearchSearviceHelper.createMock(allAreaId = Future.successful(List(areaIdDto, areaIdDto2)))
      val app = create(service)
      val expect = Json.toJson(List(areaIdDto, areaIdDto2)).toString
      val result = app.areas_ids.apply(FakeRequest())

      status(result) must be equalTo OK
      contentAsString(result) must be equalTo expect
    }

    "return `Internal Server Error' response if AreaSearchService.allAreaId failed with IOException" in {
      val service = AreaSearchSearviceHelper.createMock(allAreaId = Future.failed(new IOException))
      val app = create(service)
      val expect = INTERNAL_SERVER_ERROR
      val result = app.areas_ids.apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).allAreaId
    }
  }

  "areas_ids_coordinate" should {
    "return json successfully" in {
      val service = AreaSearchSearviceHelper.createMock(searchIdOfAreaContain = Future.successful(areaIdDto))
      val app = create(service)
      val expect = Json.toJson(areaIdDto).toString
      val result = app.areas_ids_coordinate(coordinate.lat, coordinate.lng).apply(FakeRequest())

      status(result) must be equalTo OK
      contentAsString(result) must be equalTo expect
    }

    "return `Internal Server Error' response if AreaSearchService.serachIdOfAreaContain failed with IOException" in {
      val service = AreaSearchSearviceHelper.createMock(searchIdOfAreaContain = Future.failed(new IOException))
      val app = create(service)
      val expect = INTERNAL_SERVER_ERROR
      val result = app.areas_ids_coordinate(coordinate.lat, coordinate.lng).apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).searchIdOfAreaContain(coordinate.lat, coordinate.lng)
    }

    "return 'Bad Request' response if AreaSearchService.searchIdOfAreaContain failed with IllegalArgumentException" in {
      val service = AreaSearchSearviceHelper.createMock(searchIdOfAreaContain = Future.failed(new IllegalArgumentException))
      val app = create(service)
      val expect = BAD_REQUEST
      val result = app.areas_ids_coordinate(coordinate.lat, coordinate.lng).apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).searchIdOfAreaContain(coordinate.lat, coordinate.lng)
    }

    "return 'Not Found' response if AreaSearchService.searchIdOfAreaContain failed with NoSuchElementException" in {
      val service = AreaSearchSearviceHelper.createMock(searchIdOfAreaContain = Future.failed(new NoSuchElementException))
      val app = create(service)
      val expect = NOT_FOUND
      val result = app.areas_ids_coordinate(coordinate.lat, coordinate.lng).apply(FakeRequest())

      status(result) must be equalTo expect
      there was one(service).searchIdOfAreaContain(coordinate.lat, coordinate.lng)
    }
  }
}
