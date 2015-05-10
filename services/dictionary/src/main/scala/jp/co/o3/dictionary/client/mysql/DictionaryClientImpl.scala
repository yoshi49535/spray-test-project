package jp.co.o3.dictionary.client.mysql

import akka.actor.{Actor, Props}
import jp.co.o3.dictionary.DictionaryService
import jp.co.o3.dictionary.DictionaryService._

import com.github.mauricio.async.db.{RowData, QueryResult, Connection}

object MySqlDictionaryClientImpl {
  def props(host:String, port:Int, username:String, password:String): Props = {
    //val connection = MySQLConnection(Configuration)
    Props(new MySqlDictionaryClientImpl())
  }
}

/*
 *  Client implementation for MySQL
 */
class MySqlDictionaryClientImpl extends DictionaryService with Actor {

  def receive = {
    case GetDictionary(dicName) => {
      // fetch (synchronously)
      /*
      dictionary = connection.getByDictionaryName(dicName)

      sender ! DictionaryStat(dictionary.name, dictionary.count)
       */
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
