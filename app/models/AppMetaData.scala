package models

import play.api.libs.json.Json

/**
  * Created by thar on 28/01/17.
  */
case class AppMetaData(authorization: Groups)

object AppMetaData {
  implicit val reads = Json.reads[AppMetaData]
  implicit val writes = Json.writes[AppMetaData]
}


