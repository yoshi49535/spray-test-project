package jp.co.o3.dictionary

import spray.json._
import spray.httpx.SprayJsonSupport
import jp.co.o3.core.http.Message

object DictionaryService {

  object DictionaryJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val GetDictionaryFormat = jsonFormat1(GetDictionary)
    implicit val CreateDictionaryFormat = jsonFormat1(CreateDictionary)
    implicit val DeleteDictionaryFormat = jsonFormat1(DeleteDictionary)
    implicit val DictionaryStatFormat = jsonFormat2(DictionaryStat)
    implicit val DictionaryRefFormat = jsonFormat1(DictionaryRef)
  
    implicit val CreateTermFormat = jsonFormat4(CreateTerm)
  }

  // Dictionary Request
  case class GetDictionary(dicName: String) extends Message
  case class CreateDictionary(dicName: String) extends Message
  case class DeleteDictionary(dicName: String) extends Message
  // Service Response
  case class DictionaryStat(dicName: String, countTerms: Int) extends Message
  case class DictionaryRef(dicName:String) extends Message

  // Tem Request
  case class GetTerm(dicName: String, termName:String) extends Message
  case class GetTermByLabel(dicName: String, label:String) extends Message
  case class CreateTerm(dicName: String, termName: String, label: String, synonyms:Set[String] = Set()) extends Message
  case class DeleteTerm(dicName: String, termName: String) extends Message

  // Term Reseponse
  case class Term(dicName: String, termName: String, label: String, synonyms:Set[String] = Set()) extends Message
  case class TermRef(dicName: String, termName: String) extends Message
}

// 
class DictionaryService {
}
