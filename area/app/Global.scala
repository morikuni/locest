import com.github.morikuni.locest.area.application.dto.ErrorDto
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.{GlobalSettings, Logger}
import scala.concurrent.Future
import scala.util.control.NonFatal

object Global extends GlobalSettings with Results {
  val logger = Logger(getClass)

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = Future.successful(BadRequest(Json.toJson(ErrorDto("Bad request. Check API document."))))

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    logger.error("Unrecoverd error occured", ex)
    ex match {
      case NonFatal(_) => Future.successful(InternalServerError(Json.toJson(ErrorDto.internalServerError)))
    }
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = Future.successful(NotFound(Json.toJson(ErrorDto("404: Not found."))))

}
