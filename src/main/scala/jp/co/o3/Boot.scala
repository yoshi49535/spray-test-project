package ja.co.o3

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import com.typesafe.config._
import ja.co.o3.dictionary.client.mysql.MySqlDictionaryClientImpl

import ja.co.o3.core.util.Helpers._
import ja.co.o3.dictionary.util.Helpers._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  import com.typesafe.config._

  // load configuration
  val defaultConf = ConfigFactory.load("reference.conf")
  val conf = ConfigFactory.load().withFallback(defaultConf)

  val dicConf  = conf.dictionary
  val srvrConf = conf.server

  // Create Actor of the serviceClient
  val dictionaryService = system.actorOf(MySqlDictionaryClientImpl.props(dicConf.dbHost, dicConf.dbPort, dicConf.dbUsername, dicConf.dbPassword))

  // create and start our service actor
  val service = system.actorOf(ApiServiceActor.props(dictionaryService), "root-service")

  implicit val timeout = Timeout(srvrConf.timeout.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = srvrConf.port)
}

