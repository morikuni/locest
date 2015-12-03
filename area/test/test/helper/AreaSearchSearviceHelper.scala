package test.helper

import com.github.morikuni.locest.area.application.dto.{AreaDto, AreaIdDto}
import com.github.morikuni.locest.area.application.service.AreaSearchService
import org.specs2.mock.Mockito
import scala.concurrent.Future

object AreaSearchSearviceHelper extends Mockito {
  def createMock(
    search: Future[AreaDto] = null,
    searchIdOfAreaContain: Future[AreaIdDto] = null,
    allAreaId: Future[List[AreaIdDto]] = null
  ): AreaSearchService = {
    val service = mock[AreaSearchService]
    service.search(any) returns search
    service.searchIdOfAreaContain(any, any) returns searchIdOfAreaContain
    service.allAreaId returns allAreaId
    service
  }
}
