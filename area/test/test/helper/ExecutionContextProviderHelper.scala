package test.helper

import com.github.morikuni.locest.area.application.{DependExecutionContextProvider, ExecutionContextProvider}
import scala.concurrent.ExecutionContext

object ExecutionContextProviderHelper {
  val create: ExecutionContextProvider = new ExecutionContextProvider {
    override def default: ExecutionContext = scala.concurrent.ExecutionContext.global

    override def repository: ExecutionContext = default
  }
}

trait InjectExecutionContextProviderHelper extends DependExecutionContextProvider {
  override def executionContextProvider: ExecutionContextProvider = ExecutionContextProviderHelper.create
}
