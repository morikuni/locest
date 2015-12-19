package com.github.morikuni.locest.frequency.test.helper

import com.github.morikuni.locest.frequency.domain.support.{DependExecutionContextProvider, ExecutionContextProvider}
import scala.concurrent.ExecutionContext

class ExecutionContextProviderHelper extends ExecutionContextProvider {
  override implicit def default: ExecutionContext = scala.concurrent.ExecutionContext.global

  override def repository: ExecutionContext = default
}

trait InjectExecutionContextProviderHelper extends DependExecutionContextProvider {
  override implicit def executionContextProvider: ExecutionContextProvider = new ExecutionContextProviderHelper
}
