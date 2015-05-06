package ja.co.o3.dictionary.route

import akka.actor._
import spray.routing._
import spray.http._
import spray.json._
import MediaTypes._


import ja.co.o3.dictionary.client._
import ja.co.o3.dictionary.client.DictionaryClient._
import ja.co.o3.dictionary.http._


object DictionaryRoute {

  def props: Props = Props(new DictionaryRoute())

}

class DictionaryRoute extends Actor with DictionaryRouteTrait {
  implicit def actorRefFactory = context
  def receive = runRoute(dictionaryRoute)

  def dictionaryService = context.actorOf(DictionaryClient.props)
}

// DictionaryRouteTrait to define the route
trait DictionaryRouteTrait extends HttpService with Actor with DictionaryRequestDelegator {

  def dictionaryService : ActorRef 

  val dictionaryRoute = 
    pathPrefix("dic" ~ Segment) { dicName => 
      pathEnd {
        get {
          // get specified dictionary status
          ctx => delegateDictionaryRequest(ctx, dictionaryService, GetDictionary(dicName))
        } ~
        put {
          ctx => delegateDictionaryRequest(ctx, dictionaryService, CreateDictionary(dicName))
        } ~
        delete {
          ctx => delegateDictionaryRequest(ctx, dictionaryService, DeleteDictionary(dicName))
        }
      } ~
      path( Segment ) { termName =>
        get {
          respondWithMediaType(`application/json`) {
            complete {
              """
              {
                "message": "term"
              }
              """
            }
          }
        }
      }
    }
}


