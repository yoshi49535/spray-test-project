package jp.co.o3.dictionary.client

import akka.actor.{Actor, Props}
import jp.co.o3.dictionary.DictionaryService
import jp.co.o3.dictionary.DictionaryService._

import slick.driver.JdbcProfile
import slick.driver.MySQLDriver
import slick.jdbc.JdbcBackend.Database

import javax.sql.DataSource
import com.mchange.v2.c3p0.ComboPooledDataSource
import jp.co.o3.dictionary.client.dal.DictionaryDAL

/** 
 * Compinion Object of MySQLDictionaryClientImpl
 *   which provides factory methods of props
 */
object MySQLDictionaryClientImpl {
  def props(path:String, username:String, password:String): Props = {
    //val connection = MySQLConnection(Configuration)
    val cpds = new ComboPooledDataSource()
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    // path should be "<host>:<port>/<dbname>"
    cpds.setJdbcUrl("jdbc:mysql://" + path)
    cpds.setUser(username)
    cpds.setPassword(password)

    Props(new MySQLDictionaryClientImpl(Database.forDataSource(cpds)))
  }

  def props(datasource:DataSource) : Props = {
    Props(new MySQLDictionaryClientImpl(Database.forDataSource(datasource)))
  }
}

/*
 *  Client implementation for MySQL
 */
class MySQLDictionaryClientImpl (val database:Database) extends DictionaryService with Actor {

  // DAL with Slick MySQLDriver 
  val dal = new DictionaryDAL(MySQLDriver)

  // method provides
  // Map DAL functions
  def createDB = {
    val session = database.createSession()
    dal.create(session)
    session.close
  }
  def dropDB = {
    val session = database.createSession()
    dal.drop(session)
    session.close
  }
  def purgeDB = {
    val session = database.createSession()
    dal.purge(session)
    session.close
  }

  // Actor receive methods for this serviceClient
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
