package jp.co.o3.dictionary.util

import com.typesafe.config.{ConfigFactory, Config => ConfigFile}

/*****
 * Usage:
 * 
 * 1) from "dictionary.conf" or other config file
 *   config = dictionary.util.Config()
 *   config = dictionary.util.Config("any.name.conf")
 * 
 * 2) from "dictionary" segment on "application.conf"
 *   config = typesafe.config.ConfigFactory.load()
 *   dicConfig = dictionary.util.Config(config.getConfig("dictionary"))
 * 
 * 
 * Then you can use 
 *    dicConfig.dbHost and etc.
 * 
 * see dictionary.conf 
 *****/
object DictionaryConfig {
    def apply() = {
        new DictionaryConfig(ConfigFactory.load("dictionary.conf"))
    }
    def apply(configFile:String) = {
        // configFile
        new DictionaryConfig(ConfigFactory.load(configFile))
    }
    def apply(config:ConfigFile) = {
        new DictionaryConfig(config)
    }
}

class DictionaryConfig(var config: ConfigFile) {
    // make sure you configure config before use lazy vals
    lazy val dbHost = config.getString("db.host")

    lazy val dbPort = config.getInt("db.port")

    lazy val dbUsername = config.getString("db.username")

    lazy val dbPassword = config.getString("db.password") 

    lazy val dbName     = config.getString("db.name")

    lazy val jdbcPath = dbHost + ":" + dbPort + "/" + dbName
}


object Helpers {
  implicit class ConfigForDictionary(val self:ConfigFile) extends AnyVal {
    // Create new DictionaryConfig  
    def dictionary = {
      DictionaryConfig(self.getConfig("dictionary"))
    }
  }
}
