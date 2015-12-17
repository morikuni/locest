package com.github.morikuni.locest.frequency.domain.repository.impl

import com.github.morikuni.locest.frequency.domain.model.AreaId
import com.github.morikuni.locest.frequency.domain.repository.{AreaRepository, AreaRepositorySession, DependAreaRepository, DependAreaRepositoryTransactionManager}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import com.typesafe.config.ConfigFactory
import java.io.IOException
import play.api.Logger
import play.api.Play.current
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}

class AreaRepositoryImpl extends AreaRepository {

  val url = {
    val conf = ConfigFactory.load()
    conf.getString("areaApi.url")+":"+conf.getString("areaApi.port")
  }

  override def findByCoordinate(lat: Double, lng: Double): Transaction[AreaRepositorySession, Option[AreaId]] = Transaction { (_, ctx) =>
    WS.url(url+s"/areas/ids/coordinate/${lat}/${lng}")
      .withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .get()
      .map{ response =>
        (response.json \ "id").toOption.map { js =>
          AreaId(js.as[Int])
        }
      }(ctx)
  }
}

trait InjectAreaRepository extends DependAreaRepository {
  override def areaRepository: AreaRepository = new AreaRepositoryImpl
}

object EmptyAreaRepositorySession extends AreaRepositorySession

trait InjectAreaRepositoryTransactionManager extends DependAreaRepositoryTransactionManager {
  override def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession] = new TransactionManager[AreaRepositorySession] {
    val logger: Logger = Logger(getClass)

    override def execute[A](transaction: Transaction[AreaRepositorySession, A])(ctx: ExecutionContext): Future[A] = {
      try {
        transaction.run(EmptyAreaRepositorySession, ctx)
          .recoverWith {
            case e: Exception =>
              logger.error("REST API error in recoverWith block", e)
              Future.failed(new IOException(e))
          }(ctx)
      } catch {
        case e: Exception =>
          logger.error("REST API error in catch block", e)
          Future.failed(new IOException(e))
      }
    }
  }
}
