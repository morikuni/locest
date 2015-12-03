package test.helper

import com.github.morikuni.locest.area.domain.repository.{AreaRepositorySession, DependAreaRepositoryTransactionManager}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import org.specs2.mock.Mockito
import scala.concurrent.{ExecutionContext, Future}

object TransactionManagerHelper extends Mockito {
  def emptyTransactionManager[Env]: TransactionManager[Env] = new TransactionManager[Env] {
    override def execute[A](transaction: Transaction[Env, A])(ctx: ExecutionContext): Future[A] = transaction.run(null.asInstanceOf[Env], ctx)
  }
}

trait InjectEmptyTransactionManager extends DependAreaRepositoryTransactionManager {
  override def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession] = TransactionManagerHelper.emptyTransactionManager
}