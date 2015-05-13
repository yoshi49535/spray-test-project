package jp.co.o3.core.http

import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import spray.http._
import spray.routing.RequestContext
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import scala.util.{ Try, Success, Failure }
import akka.util.Timeout

trait Message 
object AsyncRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: Any) extends AsyncRequest 
  case class WithProps(r: RequestContext, props: Props, message: Any) extends AsyncRequest {
    lazy val target = context.actorOf(props)
  }
}

object SyncRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: Any) extends SyncRequest 
  case class WithProps(r: RequestContext, props: Props, message: Any) extends SyncRequest {
    lazy val target = context.actorOf(props)
  }
}

trait Request extends Actor with Json4sSupport {

  import context._

  val json4sFormats = DefaultFormats

  def r: RequestContext
  def target: ActorRef
  def message: Any

  // default timeout
  implicit val timeout = Timeout(2, SECONDS)
}

trait AsyncRequest extends Request {

  import context._

  // send the message to the actual request handler service
  target ! message
  
  // send the response back to client
  r.complete(OK, "Hope task will be done.")

  // handle the response from service
  def receive = { 
    case ReceiveTimeout => {
      // do something here if you need
      // actor done
      stop(self)
    }
    case _ => {
      // actor done
      stop(self)
    }
  }
}

trait SyncRequest extends Request {

  import context._
  import akka.pattern.ask

  val response = target ! message

  def receive = {
    case ReceiveTimeout => {
      r.complete(GatewayTimeout, "Currently Service is buzy.")
      stop(self)
    }
    case message:Message => {
      r.complete(OK, message)
      stop(self)
    }
    case doMessage:Future[Message] => {
      doMessage onComplete {
        case Success(message) => {
           //r.complete(OK, message)
           r.complete(message)
        } 
        case Failure(t) => {
           r.complete(InternalServerError, t)
        }
      }
      stop(self)
    }
    case _  => {
      r.complete(InternalServerError, "Invalid Receive Message: ")
      stop(self)
    }
  }
}

trait RequestDelegator {
  this: Actor =>

  def delegateAsyncRequest(r: RequestContext, target: ActorRef, message: Any) = {
    context.actorOf(Props(new AsyncRequest.WithActorRef(r, target, message))); 
  }

  def delegateSyncRequest(r: RequestContext, target: ActorRef, message: Any) = {
    context.actorOf(Props(new SyncRequest.WithActorRef(r, target, message))); 
  }

  def delegateAsyncRequest(r: RequestContext, target: Props, message: Any) = {
    context.actorOf(Props(new AsyncRequest.WithProps(r, target, message))); 
  }

  def delegateSyncRequest(r: RequestContext, target: Props, message: Any) = {
    context.actorOf(Props(new SyncRequest.WithProps(r, target, message))); 
  }
}

