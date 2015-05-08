package ja.co.o3.dictionary.client.mysql

import akka.actor.{Actor, Props}
import ja.co.o3.dictionary.client.DictionaryClient
import ja.co.o3.dictionary.client.DictionaryClient._

import com.github.mauricio.async.db.{RowData, QueryResult, Connection}

object MySqlDictionaryClientImpl {
  def props(host:String, port:Int, username:String, password:String): Props = {
    //val connection = MySQLConnection(Configuration)
    Props(new MySqlDictionaryClientImpl())
  }
}

// MySqlDictionaryClientImpl
//class MySqlDictionaryClientImpl (val connection:Connection) extends DictionaryClient {
class MySqlDictionaryClientImpl extends DictionaryClient {

  def receive = {
    case GetDictionary(dicName) => {
      // fetch (synchronously)
      sender ! DictionaryStat(dicName, 0)
    }
    case CreateDictionary(dicName) => {
      // create (asynchronously)
      sender ! DictionaryRef(dicName)
    }
    case DeleteDictionary(dicName) => {
      sender ! DictionaryRef(dicName)
    }

    case GetTerm(dicName, termName) => {
      sender ! Term(dicName, termName, "Hoge")
    }
    case GetTermByLabel(dicName, label) => {
      sender ! Term(dicName, "hoge", label)
    }
    case CreateTerm(dicName, termName, label, synonyms) => {
      sender ! Term(dicName, termName, label, synonyms)
    }
    case DeleteTerm(dicName, termName) => {
      sender ! TermRef(dicName, termName)
    }
  }
}
