package test.helper

import com.github.morikuni.locest.area.domain.model.{Area, AreaId}
import com.github.morikuni.locest.area.domain.repository.{AreaRepository, AreaRepositorySession}
import com.github.morikuni.locest.util.Transaction
import org.specs2.mock.Mockito

object AreaRepositoryHelper extends Mockito {
  def createMock(
    all: Transaction[AreaRepositorySession,List[AreaId]] = null,
    solve: Transaction[AreaRepositorySession, Option[Area]] = null,
    findByCoordinate: Transaction[AreaRepositorySession, Option[AreaId]] = null
  ): AreaRepository = {
    val repo = mock[AreaRepository]
    repo.all returns all
    repo.solve(any) returns solve
    repo.findByCoordinate(any) returns findByCoordinate
    repo
  }
}