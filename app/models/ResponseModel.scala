package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Writes, JsPath, Reads}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import org.joda.time._
import org.joda.time.format._
import models.AnormExtension._
import models.DateUtil.d2s
object ResponseModel {

  case class Response(q_id: String, response: Int) { require(response >= 0 && response <= 5) }
  case class Responses(s_id: String, responses: List[Response]) { require(responses.nonEmpty) }
  case class ResponseRecord(s_id: String, q_id: String, u_id: String, response: Int, timestamp: DateTime)

  implicit val responseReads: Reads[Response] = (
    (JsPath \ "q_id").read[String] and
    (JsPath \ "response").read[Int](Reads.min(0) keepAnd Reads.max(5))
  )(Response.apply _)

  implicit val responsesReads: Reads[Responses] = (
    (JsPath \ "s_id").read[String] and
    (JsPath \ "responses").read[List[Response]]
  )(Responses.apply _)

  implicit val responseWrites = new Writes[ResponseRecord] {
    def writes(x: ResponseRecord) = Json.obj(
      "surveyId" -> x.s_id
      ,"questionId" -> x.q_id
      //,"userId" -> x.u_id
      ,"responseId" -> x.response
      ,"timestamp" -> d2s(x.timestamp)
    )
  }

  def saveResponses(r: Responses): Unit = {
    // TODO verify that S_ID and Q_ID actually exist
    // TODO collect User ID
    r.responses.foreach{response =>
      //response.response match {
        //case x if x <= 5 || x >= 0 =>
          DB.withConnection {implicit connection =>
            SQL("""INSERT INTO RESPONSES (S_ID,Q_ID,U_ID,RESPONSE) values ({S_ID},{Q_ID},{U_ID},{RESPONSE})""")
              .on("S_ID"     -> r.s_id)
              .on("Q_ID"     -> response.q_id)
              .on("U_ID"     -> 1)
              .on("RESPONSE" -> response.response)
              .executeInsert()
          }
        //case _ =>
      //}
    }
  }

  private val rowParser = {
    get[String]("S_ID") ~
    get[String]("Q_ID") ~
    get[String]("U_ID") ~
    get[Int]("RESPONSE") ~
    get[DateTime]("TIMESTAMP") map {
      case a~b~c~d~e => ResponseRecord(a,b,c,d,e)
    }
  }

  def getResponses = {
    val list: List[ResponseRecord] = DB.withConnection{implicit connection =>
      SQL("""SELECT S_ID, Q_ID, U_ID, RESPONSE, TIMESTAMP FROM RESPONSES;""").as(rowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }
}
