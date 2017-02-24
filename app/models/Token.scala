package models

import play.api.libs.json.Json

/**
  * Created by thar on 28/01/17.
  */
case class Token(sub: Option[String], app_metadata: Option[AppMetaData])

object Token {
  implicit val reads = Json.reads[Token]
}


