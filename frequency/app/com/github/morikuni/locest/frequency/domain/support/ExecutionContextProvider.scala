package com.github.morikuni.locest.frequency.domain.support

import scala.concurrent.ExecutionContext

trait ExecutionContextProvider {
  def default: ExecutionContext
  def repository: ExecutionContext
}

trait DependExecutionContextProvider {
  def executionContextProvider: ExecutionContextProvider
}