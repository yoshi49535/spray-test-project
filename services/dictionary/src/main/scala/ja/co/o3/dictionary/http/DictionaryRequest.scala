package ja.co.o3.dictionary.http

import akka.actor._
import spray.routing._
import spray.http._
import spray.http.StatusCode
import spray.http.StatusCodes._
import ja.co.o3.dictionary.client._
// json
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
//
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Stop
import akka.actor.OneForOneStrategy

object DictionaryRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: DictionaryMessage) extends DictionaryRequest 
  case class WithProps(r: RequestContext, props: Props, message: DictionaryMessage) extends DictionaryRequest {
    lazy val target = context.actorOf(props)
  }
}

// Bridge dictionary route and service
trait DictionaryRequest extends Actor with Json4sSupport {
  
  import context._

  val json4sFormats = DefaultFormats

  def r: RequestContext
  def target: ActorRef
  def message: DictionaryMessage

  setReceiveTimeout(2.seconds)
  target ! message

  def receive = {
    // Complete the client task
    case res:DictionaryMessage => complete(OK, res)
    // Asych task is executed.
    //case t:Task => complete(REDIRECT, t)
    //case v: Validation => complete(BadRequest, v)
    case ReceiveTimeout   => complete(GatewayTimeout, Error("Request timeout"))
  }

  def complete[T <: AnyRef](status: StatusCode, obj:T) = {
    r.complete(status, obj)
    stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e => {
        complete(InternalServerError, Error(e.getMessage))
        Stop
      }
    }

  case class Error(message:String)
}

trait DictionaryRequestDelegator {
  this: Actor => 

  // send message to the serviceClient with requester
  def delegateDictionaryRequest(r: RequestContext, target: ActorRef, message: DictionaryMessage) = {
    context.actorOf(Props(new DictionaryRequest.WithActorRef(r, target, message)))
  }

  def delegateDictionaryRequest(r: RequestContext, props: Props, message: DictionaryMessage) = {
    context.actorOf(Props(new DictionaryRequest.WithProps(r, props, message)))
  }
}
