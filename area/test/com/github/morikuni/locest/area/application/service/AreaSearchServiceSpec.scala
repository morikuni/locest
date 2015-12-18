package com.github.morikuni.locest.area.application.service

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto}
import com.github.morikuni.locest.area.application.service.impl.AreaSearchServiceImpl
import com.github.morikuni.locest.area.domain.model.{Area, AreaId, AreaProperty, Coordinate}
import com.github.morikuni.locest.area.domain.repository.AreaRepository
import com.github.morikuni.locest.util.Transaction
import java.io.IOException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import test.helper.{AreaRepositoryHelper, InjectEmptyTransactionManager, InjectExecutionContextProviderHelper}

class AreaSearchServiceSpec extends Specification with Mockito {

  def create(areaRepo: AreaRepository): AreaSearchServiceImpl =
    new AreaSearchServiceImpl
      with InjectEmptyTransactionManager
      with InjectExecutionContextProviderHelper {
        override def areaRepository: AreaRepository = areaRepo
      }

  val areaId = AreaId(1)
  val area = Area(areaId, AreaProperty("name", Coordinate.createUnsafe(30, 130)))
  val areaId2 = AreaId(2)

  "search" should {
    "return Area successfully" in {
      val repo = AreaRepositoryHelper.createMock(solve = Transaction.successful(Some(area)))
      val service = create(repo)
      val expect = AreaDto.from(area)

      Await.result(service.search(areaId.value), Duration.Inf) must be equalTo expect
    }

    "fail with IOException if AreaRepository.find failed with IOException" in {
      val repo = AreaRepositoryHelper.createMock(solve = Transaction.failed(new IOException))
      val service = create(repo)

      Await.result(service.search(areaId.value), Duration.Inf) must throwA[IOException]
    }

    "fail with NoSuchElementException if AreaRepository.find returned None" in {
      val repo = AreaRepositoryHelper.createMock(solve = Transaction.successful(None))
      val service = create(repo)

      Await.result(service.search(areaId.value), Duration.Inf) must throwA[NoSuchElementException]
    }
  }

  "searchIdOfAreaContain" should {
    val lat = 30
    val lng = 130

    "return AreaId successfully" in {
      val repo = AreaRepositoryHelper.createMock(findByCoordinate = Transaction.successful(Some(areaId)))
      val service = create(repo)
      val expect = AreaIdDto.from(areaId)

      Await.result(service.searchIdOfAreaContain(lat, lng), Duration.Inf) must be equalTo expect
    }

    "fail with IOException if AreaRepository.findByCoordinate failed with IOException" in {
      val repo = AreaRepositoryHelper.createMock(findByCoordinate = Transaction.failed(new IOException()))
      val service = create(repo)

      Await.result(service.searchIdOfAreaContain(lat, lng), Duration.Inf) must throwA[IOException]
    }

    "fail with IOException if AreaRepository.findByCoordinate returned None" in {
      val repo = AreaRepositoryHelper.createMock(findByCoordinate = Transaction.successful(None))
      val service = create(repo)

      Await.result(service.searchIdOfAreaContain(lat, lng), Duration.Inf) must throwA[NoSuchElementException]
    }

    "fail with IllegalArgumentException if lat is out of range" in {
      val repo = AreaRepositoryHelper.createMock()
      val service = create(repo)

      Await.result(service.searchIdOfAreaContain(1000, lng), Duration.Inf) must throwA[IllegalArgumentException]
      there was no(repo).findByCoordinate(any)
    }

    "fail with IllegalArgumentException if lng is out of range" in {
      val repo = AreaRepositoryHelper.createMock()
      val service = create(repo)

      Await.result(service.searchIdOfAreaContain(lat, 1000), Duration.Inf) must throwA[IllegalArgumentException]
      there was no(repo).findByCoordinate(any)
    }
  }

  "allAreaId" should {
    val data = List(areaId, areaId2)

    "returns List(AreaId) sucessfully" in {
      val repo = AreaRepositoryHelper.createMock(all = Transaction.successful(data))
      val service = create(repo)
      val expect = data.map(AreaIdDto.from)

      Await.result(service.allAreaId, Duration.Inf) must be equalTo expect
    }

    "fail with IOException if AreaRepository.all failed" in {
      val repo = AreaRepositoryHelper.createMock(all = Transaction.failed(new IOException))
      val service = create(repo)

      Await.result(service.allAreaId, Duration.Inf) must throwA[IOException]
    }
  }

}
