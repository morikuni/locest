package test.helper

import com.github.morikuni.locest.area.domain.model.{Area, AreaId}
import com.github.morikuni.locest.area.domain.repository.{AreaRepository, AreaRepositorySession}
import com.github.morikuni.locest.util.Transaction
import org.specs2.mock.Mockito

object AreaRepositoryHelper extends Mockito {
  def createMock(
    all: Transaction[AreaRepositorySession,List[AreaId]] = null,
    find: Transaction[AreaRepositorySession, Area] = null,
    findByCoordinate: Transaction[AreaRepositorySession, AreaId] = null
  ): AreaRepository = {
    val repo = mock[AreaRepository]
    repo.all returns all
    repo.find(any) returns find
    repo.findByCoordinate(any) returns findByCoordinate
    repo
  }
}