package ja.co.o3.dictionary.client

import akka.actor.{Actor, Props}
import ja.co.o3.dictionary.client.DictionaryClient._

import spray.json._
import spray.httpx.SprayJsonSupport

object DictionaryJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val GetDictionaryFormat = jsonFormat1(GetDictionary)
  implicit val CreateDictionaryFormat = jsonFormat1(CreateDictionary)
  implicit val DeleteDictionaryFormat = jsonFormat1(DeleteDictionary)
  implicit val DictionaryStatFormat = jsonFormat2(DictionaryStat)
  implicit val DictionaryRefFormat = jsonFormat1(DictionaryRef)

  implicit val CreateTermFormat = jsonFormat4(CreateTerm)
}

object DictionaryClient {
  // Dictionary Request
  case class GetDictionary(dicName: String) extends DictionaryMessage
  case class CreateDictionary(dicName: String) extends DictionaryMessage
  case class DeleteDictionary(dicName: String) extends DictionaryMessage
  // Service Response
  case class DictionaryStat(dicName: String, countTerms: Int) extends DictionaryMessage
  case class DictionaryRef(dicName:String) extends DictionaryMessage

  // Tem Request
  case class GetTerm(dicName: String, termName:String) extends DictionaryMessage
  case class GetTermByLabel(dicName: String, label:String) extends DictionaryMessage
  case class CreateTerm(dicName: String, termName: String, label: String, synonyms:Set[String] = Set()) extends DictionaryMessage
  case class DeleteTerm(dicName: String, termName: String) extends DictionaryMessage

  // Term Reseponse
  case class Term(dicName: String, termName: String, label: String, synonyms:Set[String] = Set()) extends DictionaryMessage
  case class TermRef(dicName: String, termName: String) extends DictionaryMessage
}

abstract class DictionaryClient extends Actor {
//  def receive = {
//    case GetDictionary(dicName) => {
//      // fetch (synchronously)
//      sender ! DictionaryStat(dicName, 0)
//    }
//    case CreateDictionary(dicName) => {
//      // create (asynchronously)
//      sender ! DictionaryRef(dicName)
//    }
//    case DeleteDictionary(dicName) => {
//      sender ! DictionaryRef(dicName)
//    }
//
//    case GetTerm(dicName, termName) => {
//      sender ! Term(dicName, termName, "Hoge")
//    }
//    case GetTermByLabel(dicName, label) => {
//      sender ! Term(dicName, "hoge", label)
//    }
//    case CreateTerm(dicName, termName, label, synonyms) => {
//      sender ! Term(dicName, termName, label, synonyms)
//    }
//    case DeleteTerm(dicName, termName) => {
//      sender ! TermRef(dicName, termName)
//    }
//  }
}

trait DictionaryMessage {}
