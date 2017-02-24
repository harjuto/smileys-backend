package models

import org.joda.time.{LocalDate}
import play.api.libs.json.{JsValue, Writes, Json}

/**
  * Created by thar on 28/01/17.
  */
case class Smiley (name: String, date: LocalDate, image: Array[Byte], user_external_id: String)

object Smiley {
  implicit val writes = new Writes[Smiley] {
    def writes(s: Smiley): JsValue = {
      Json.obj(
        "name" -> s.name,
        "date" -> s.date,
        "image" -> new String(s.image),
        "user_external_id" -> s.user_external_id
      )
    }

  }

  implicit val reads = Json.reads[Smiley]


}
