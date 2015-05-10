package jp.co.o3.dictionary.client.dal

import slick.driver.JdbcProfile
import java.util.UUID

/***
 *  Dictionary Data Access Layer
 *    Dictionary DAL on Slick FRM 
 * 
 * 
 */
// Data-Access-Layer of Dictionary Service Models
class DictionaryDAL(override val profile: JdbcProfile) extends DictionaryDALTrait with SlickDriverTrait {
}

trait DictionaryDALTrait { this: SlickDriverTrait =>
  // import slick driver api with object profile
  import profile.api._

  // Define models' case-classes of DAL
  case class Dictionary(name:String, label: String)
  case class Term(name:String, label: String, dictionaryName:String)

  // Slick Table definitions
  class Dictionaries(tag: Tag) extends Table[Dictionary](tag, "dictionary") {
    def name  = column[String]("name", O.PrimaryKey)  
    def label = column[String]("label")  

    def * = (name, label) <> (Dictionary.tupled, Dictionary.unapply)
  }

  class Terms(tag:Tag) extends Table[Term] (tag, "term") {
    def name         = column[String]("name", O.PrimaryKey)  
    def label        = column[String]("label")  
    def dictionaryName = column[String]("dictionary_name")

    def * = (name, label, dictionaryName) <> (Term.tupled, Term.unapply)
    // getter of mapped object
    def dictionary = foreignKey("DICTIONARY_FK", dictionaryName, dictionaries)(_.name)
  }

  // TableQueries
  val dictionaries = TableQuery[Dictionaries]
  val terms = TableQuery[Terms]

  // Methods provides
  /**
   * create
   *   Create tables
   */
  def create(implicit session: Session): Unit = {
    try {
      dictionaries.schema.create
      terms.schema.create
    } catch {
      //case e: Exception => logger.info("Could not create database.... assuming it already exists")
      case e: Exception => 
    }
  }

  /**
   * drop
   *   Drop table 
   */
  def drop(implicit session: Session): Unit = {
    try {
      terms.schema.drop
      dictionaries.schema.drop
    } catch {
      //case e: Exception => logger.info("Could not drop database")
      case e: Exception => 
    }
  }

  /**
   * purge
   *   Drop and Create tables 
   */
  def purge(implicit session: Session) = { drop; create }
}

// 
trait SlickDriverTrait {
  val profile: JdbcProfile
}

