package com.github.morikuni.locest.area.application

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait ExecutionContextProvider {
  /** 通常時に使う */
  def default: ExecutionContext

  /** Repository のIOなど時間がかかりそうなときに使う */
  def repository: ExecutionContext
}

trait DependExecutionContextProvider {
  def executionContextProvider: ExecutionContextProvider
}

trait InjectDefaultExecutionContextProvider extends DependExecutionContextProvider {
  override val executionContextProvider: ExecutionContextProvider = new ExecutionContextProvider {
    override val default: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
    override val repository: ExecutionContext = default
  }
}
