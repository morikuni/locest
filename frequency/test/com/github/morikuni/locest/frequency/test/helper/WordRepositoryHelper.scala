package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.model.Word
import com.github.morikuni.locest.frequency.domain.repository.{WordRepository, WordRepositorySession}
import com.github.morikuni.locest.util.Transaction
import org.specs2.mock.Mockito

object WordRepositoryHelper extends Mockito {
  def create(create: Transaction[WordRepositorySession, Word]): WordRepository = {
    val repo = mock[WordRepository]
    repo.create(any) returns create
    repo
  }
}
