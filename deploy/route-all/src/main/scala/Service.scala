package app 

import akka.actor.{Actor, Props, ActorRef}
import spray.routing._
import spray.http._
import MediaTypes._

import jp.co.o3.dictionary.route.ServiceRouteTrait
import jp.co.o3.dictionary.DictionaryService

object ApiServiceActor {
  def props(dictionaryService:ActorRef) = Props(new ApiServiceActor(dictionaryService)) 
}
// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ApiServiceActor(var dictionaryService:ActorRef) extends Actor with ApiService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  implicit def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

trait ApiService extends HttpService 
  with ServiceRouteTrait 
{
  val route =
    pathPrefix("") {
      pathEnd {
        get {
          respondWithMediaType(`application/json`) {
            // service status
            complete {
              """
              {
                "message": "success"
              }
              """
            }
          }
        }
      }
    } ~
    dictionaryRoute
}

