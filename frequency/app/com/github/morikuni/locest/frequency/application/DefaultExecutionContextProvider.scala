package com.github.morikuni.locest.frequency.application

import com.github.morikuni.locest.frequency.domain.support.{DependExecutionContextProvider, ExecutionContextProvider}
import scala.concurrent.ExecutionContext

class DefaultExecutionContextProvider extends ExecutionContextProvider {
  override def default: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  override def repository: ExecutionContext = default
}

trait InjectExecutionContextProvider extends DependExecutionContextProvider {
  override implicit val executionContextProvider: ExecutionContextProvider = new DefaultExecutionContextProvider
}
