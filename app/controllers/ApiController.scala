package controllers
import javax.inject.Inject

import actors.WebSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import anorm.AnormException
import models.{SmileyInput}
import org.joda.time.LocalDate
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import play.api.libs.streams._
import security.Auth
import services.SmileyService
import scala.Long
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by thar on 28/01/17.
  */
class ApiController @Inject() ( implicit system: ActorSystem, materializer: Materializer,
                                auth: Auth,
                                smileyService: SmileyService) extends Controller {

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => WebSocketActor.props(out, system))
  }

  def list() = auth.Authenticated.async { req =>
    smileyService.list(req.user.app_metadata.get.authorization.groups.head)
      .map { smileys =>
        Ok(Json.toJson(smileys.groupBy(_.user_external_id)))
      }
      .recover {
        case e: AnormException =>
          InternalServerError(e.getMessage)
      }

  }

  def submit() = auth.Authenticated.async(BodyParsers.parse.json) { req =>
    val parsedSmiley = req.body.validate[SmileyInput]
    parsedSmiley match {
      case smiley: JsSuccess[SmileyInput] =>
        implicit val date = LocalDate.now()
        // Smiley exists?
        smileyService.hasSmileyForToday(req.user.sub.get)
            .flatMap {
              case 0 =>
                smileyService.create(req.user.app_metadata.get.authorization.groups.head, req.user.sub.get, smiley.get)
                  .map { _ =>
                    Ok("Created")
                  }
              case numRows: Long if numRows > 0 =>
                smileyService.update(req.user.app_metadata.get.authorization.groups.head, req.user.sub.get, smiley.get)
                  .map { _ =>
                    Ok("Updated")
                  }
            }
          .recover {
            case e: Exception =>
              InternalServerError(e.getMessage)
          }
      case e: JsError => Future{BadRequest("Errors: " + JsError.toJson(e).toString())}
    }

  }

  def health() = Action {
    Ok("Ok")
  }

}

