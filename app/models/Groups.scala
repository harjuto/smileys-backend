package models

import play.api.libs.json.Json

/**
  * Created by thar on 04/02/17.
  */

case class Groups(groups: Seq[String])
object Groups {
  implicit val reads = Json.reads[Groups]
  implicit val writes = Json.writes[Groups]
}
