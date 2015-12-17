package com.github.morikuni.locest.frequency.domain.support

import scala.concurrent.ExecutionContext

trait ExecutionContextProvider {
  implicit def default: ExecutionContext
  def repository: ExecutionContext
}

trait DependExecutionContextProvider {
  implicit def executionContextProvider: ExecutionContextProvider
}