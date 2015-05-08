package jp.co.o3.core.util

import com.typesafe.config.{ConfigFactory, Config} 

object ServerConfig {
  def apply() = {
      new ServerConfig(ConfigFactory.load("server.conf"))
  }
  def apply(configFile:String) = {
      // configFile
      new ServerConfig(ConfigFactory.load(configFile))
  }
  def apply(config:Config) = {
      new ServerConfig(config)
  }
}

class ServerConfig (var config: Config) {
  // make sure you configure config before use lazy vals
  lazy val port = config.getInt("port")

  lazy val timeout = config.getInt("timeout")
}

object Helpers {
  implicit class ConfigForServer(val self:Config) extends AnyVal {
    def server = {
      ServerConfig(self.getConfig("server"))
    }
  }
}

