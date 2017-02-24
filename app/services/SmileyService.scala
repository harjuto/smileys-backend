package services

import javax.inject.Inject
import anorm.JodaParameterMetaData._
import anorm._
import models.{SmileyInput, Smiley}
import play.api.db.Database
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.joda.time.{LocalDate}

/**
  * Created by thar on 28/01/17.
  */
class SmileyService @Inject() (db: Database) {

  val smileyParser: RowParser[Smiley] = Macro.namedParser[Smiley]

  def list(customerExternalId: String): Future[Seq[Smiley]] = {
    Future {
      db.withConnection {
        implicit c =>
          SQL("SELECT * FROM smiley WHERE customer_external_id={id} order by date asc")
            .on(
              "id" -> customerExternalId
            )
            .as(smileyParser.*)
      }
    }
  }

  def create(customerExternalId: String, userExternalId: String, data: SmileyInput)(implicit date: LocalDate): Future[Int] = {
    Future {
      db.withConnection {
         implicit c =>
           SQL("INSERT smiley SET user_external_id={user_external_id}, name={name}, date={date}, image={image}, customer_external_id={customer_external_id}")
             .on(
               "user_external_id" -> userExternalId,
               "name" -> data.name,
               "date" -> date,
               "image" -> data.image.getBytes,
               "customer_external_id" -> customerExternalId
             ).executeUpdate()
      }
    }
  }


  def update(customerExternalId: String, userExternalId: String, data: SmileyInput)(implicit date: LocalDate): Future[Int] = {
    Future {
      db.withConnection {
        implicit c =>
          SQL(
            """
              |UPDATE
              | smiley
              |SET
              | image={image}
              |WHERE
              | user_external_id={user_external_id}
              | AND
              | customer_external_id={customer_external_id}
              | AND
              | date={date}
            """.stripMargin)
            .on(
              "user_external_id" -> userExternalId,
              "image" -> data.image.getBytes,
              "date" -> date,
              "customer_external_id" -> customerExternalId
            ).executeUpdate()
      }
    }
  }

  def hasSmileyForToday(userExternalId: String)(implicit date: LocalDate): Future[Long] = {
    Future {
      db.withConnection {
        implicit c =>
        SQL("SELECT EXISTS(SELECT name FROM smiley WHERE user_external_id={user_external_id} AND date = {date}) as result")
          .on(
            "user_external_id" -> userExternalId,
            "date" -> date
          )
          .as(SqlParser.long("result").single)
      }
    }
  }
}
