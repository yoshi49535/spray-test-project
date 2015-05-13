package jp.co.o3.core.task.http

import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import spray.http._
import spray.routing.RequestContext
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import scala.util.{ Try, Success, Failure }
import jp.co.o3.core.task._
import jp.co.o3.core.http.Request
import akka.pattern.ask

import jp.co.o3.core.task.TaskManager._
import jp.co.o3.core.http.Message
import jp.co.o3.core.http.{RequestDelegator => BaseRequestDelegator}

object AsyncTaskRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: Any,  override val taskManager:Option[ActorRef], override val taskRoutePrefix: String = "/task") extends AsyncTaskRequest
  case class WithProps(r: RequestContext, props: Props, message: Any, override val taskManager: Option[ActorRef], override val taskRoutePrefix: String = "/task") extends AsyncTaskRequest {
    lazy val target = context.actorOf(props)
  }
}

trait AsyncTaskRequest extends Request with Actor{
  this:Actor => 

  import context._
  import akka.pattern.ask
  import TaskManager._

  val taskManager : Option[ActorRef] = None

  implicit val taskRoutePrefix : String = "/task"
  var task:Task = null;

  // Create Task 
  if(None != taskManager) {
    val taskDone:Future[Any] = taskManager.get ? CreateTask()
    val task = Await.result(taskDone, timeout.duration).asInstanceOf[Task]
  }

  // send the message to the actual request handler service
  target ! message
  
  // send the response back to client with Link header
  if(null != task) {
    r.withHttpResponseHeadersMapped { headers => 
      HttpHeaders.RawHeader("Link", r.request.uri.withPath(Uri.Path(taskRoutePrefix + "/" + task.id)).toString) :: headers
    }.complete(OK, "Task is queued.")
  } else {
    r.complete(OK, "Task is queued")
  }

  // handle the response from service
  def receive = { 
    case ReceiveTimeout => {
      // send taskManager to notify the request is failed by timeout
      if(taskManager.isDefined)
        taskManager.get ! FailTask(task.id, "timed out")
      // actor done
      stop(self)
    }
    case message:Message => {
      if(taskManager.isDefined)
        taskManager.get ! CompleteTask(task.id, message)
      // actor done
      stop(self)
    }
  }
}

trait RequestDelegator extends BaseRequestDelegator {
  this: Actor =>

  implicit val noTaskManager:Option[ActorRef] = None

  def delegateAsyncRequest(r: RequestContext, target: ActorRef, message: Any)(implicit taskManager:Option[ActorRef]) = {
    context.actorOf(Props(new AsyncTaskRequest.WithActorRef(r, target, message, taskManager))); 
  }

  def delegateAsyncRequest(r: RequestContext, target: Props, message: Any)(implicit taskManager:Option[ActorRef]) = {
    context.actorOf(Props(new AsyncTaskRequest.WithProps(r, target, message, taskManager))); 
  }
}

