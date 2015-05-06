package ja.co.o3.dictionary.client

import akka.actor.{Actor, Props}
import ja.co.o3.dictionary.client.DictionaryClient._

object DictionaryClient {
  def props:Props = Props(new DictionaryClient())
  // Service Request
  case class GetDictionary(dicName: String) extends DictionaryMessage
  case class CreateDictionary(dicName: String) extends DictionaryMessage
  case class DeleteDictionary(dicName: String) extends DictionaryMessage
  // Service Response
  case class DictionaryStat(dicName: String, countTerms: Int) extends DictionaryMessage
  case class DictionaryDeleted(dicName:String) extends DictionaryMessage
  case class DictionaryCreated(dicName:String) extends DictionaryMessage
}

class DictionaryClient extends Actor {
  def receive = {
    case GetDictionary(dicName) => {
      // fetch (synchronously)
      sender ! DictionaryStat(dicName, 0)
    }
    case CreateDictionary(dicName) => {
      // create (asynchronously)
      sender ! DictionaryCreated(dicName)
    }
    case DeleteDictionary(dicName) => {
      sender ! DictionaryDeleted(dicName)
    }
  }
}

trait DictionaryMessage {}
