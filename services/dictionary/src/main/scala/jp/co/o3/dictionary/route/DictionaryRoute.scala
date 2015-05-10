package jp.co.o3.dictionary.route

import akka.actor._
import spray.routing._
import spray.http._
import spray.json._
import MediaTypes._

import spray.http.StatusCodes

import jp.co.o3.dictionary.client._
import jp.co.o3.dictionary.DictionaryService
import jp.co.o3.dictionary.DictionaryService._
import jp.co.o3.dictionary.http._

object ServiceRoute {
  def props(dictionaryService:ActorRef): Props = Props(new ServiceRoute(dictionaryService))
}

class ServiceRoute(var dictionaryService:ActorRef) extends Actor with ServiceRouteTrait {
  implicit def actorRefFactory = context

  def receive = runRoute(dictionaryRoute)
}

object ServiceRouteTrait {
  case class CreateTermRequest(label:String, synonyms:Option[Set[String]])

  object ServiceRouteProtocol extends DefaultJsonProtocol {
    implicit val createTermRequestFormat = jsonFormat2(CreateTermRequest)
  }
}
// DictionaryRouteTrait to define the route
trait ServiceRouteTrait extends HttpService with Actor with DictionaryRequestDelegator {
  import jp.co.o3.dictionary.DictionaryService.DictionaryJsonProtocol._
  import jp.co.o3.dictionary.route.ServiceRouteTrait._
  import jp.co.o3.dictionary.route.ServiceRouteTrait.ServiceRouteProtocol._

  var dictionaryService : ActorRef 

  val dictionaryRoute = 
    pathPrefix("dic" ~ Segment) { dicName => 
      pathEnd {
        get {
          parameters('label.? ) { label => 
            // get specified dictionary status
            if(None != label)
              ctx => delegateDictionaryRequest(ctx, dictionaryService, GetTermByLabel(dicName, label.get))
            else
              ctx => delegateDictionaryRequest(ctx, dictionaryService, GetDictionary(dicName))
          }
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
          // get specified dictionary status
          ctx => delegateDictionaryRequest(ctx, dictionaryService, GetTerm(dicName, termName))
        } ~
        put {
          entity (as[CreateTermRequest]) { term => 
            ctx => delegateDictionaryRequest(ctx, dictionaryService, CreateTerm(dicName, termName, term.label, term.synonyms.getOrElse(Set())))
          }
        } ~
        delete {
          ctx => delegateDictionaryRequest(ctx, dictionaryService, DeleteTerm(dicName, termName))
        }
      }
    }
}


