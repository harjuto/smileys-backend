package actors
import akka.actor._
import scala.concurrent.duration._

/**
  * Created by thar on 28/01/17.
  */
object WebSocketActor {
  def props(out: ActorRef, system: ActorSystem) = Props(new WebSocketActor(out, system))
}

class WebSocketActor(out: ActorRef, system: ActorSystem) extends Actor {
  import context.dispatcher

  val keepAlive: Cancellable = system.scheduler.schedule(
    Duration.create(30, SECONDS),
    Duration.create(30, SECONDS),
    out,
    "Keepalive"
  )
  override def postStop() = {
    keepAlive.cancel()
  }

  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}