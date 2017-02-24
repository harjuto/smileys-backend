package models

import play.api.libs.json.Json

/**
  * Created by thar on 04/02/17.
  */
case class SmileyInput (name: String, image: String)

object SmileyInput {
  implicit val writes = Json.writes[SmileyInput]
  implicit val reads = Json.reads[SmileyInput]
}
