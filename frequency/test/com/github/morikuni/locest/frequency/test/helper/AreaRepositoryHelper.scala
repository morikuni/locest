package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.model.AreaId
import com.github.morikuni.locest.frequency.domain.repository.{AreaRepositorySession, AreaRepository}
import com.github.morikuni.locest.util.Transaction
import org.specs2.mock.Mockito

object AreaRepositoryHelper extends Mockito {
  def create(findByCoordinate: Transaction[AreaRepositorySession, Option[AreaId]] = null): AreaRepository = {
    val repo = mock[AreaRepository]
    repo.findByCoordinate(any, any) returns findByCoordinate
    repo
  }
}
