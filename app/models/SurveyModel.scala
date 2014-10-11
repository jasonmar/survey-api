package models

import anorm._
import anorm.SqlParser._
import models.QuestionModel.{QuestionRecord, Question}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._

object SurveyModel {

  case class SurveyRecord(id: Long, name: String)
  case class Survey(name: String)
  case class FullSurvey(id: Long, name: String, questions: List[QuestionRecord])

  def insertSurvey(x: Survey): Unit = {
    DB.withConnection {implicit connection =>
      SQL("INSERT INTO SURVEYS (NAME) values ({name})")
        .on("name" -> x.name)
        .executeInsert()
    }
  }

  def getSurveyName(id: Long): Option[String] = {
    val surveyRecords = DB.withConnection{implicit connection =>
      SQL("""SELECT S_ID,NAME FROM SURVEYS WHERE S_ID = {S_ID} LIMIT 1;""")
        .on("S_ID" -> id)
        .as(surveyRecordRowParser *)
    }
    surveyRecords match {
      case x if x.nonEmpty => Some(x(0).name)
      case _ => None
    }
  }

  def getFullSurvey(id: Long): Option[FullSurvey] = {
    val name = getSurveyName(id)
    val questions = QuestionModel.getQuestionsBySurveyID(id)
    (name,questions) match {
      case (Some(n),Some(q)) => Some(FullSurvey(id,n,q))
      case _ => None
    }
  }
  
  val surveyForm = Form(
    mapping(
      "name" -> text
    )(Survey.apply)(Survey.unapply)
  )

  implicit val fullSurveyWrites: Writes[FullSurvey] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "name").write[String] and
    (JsPath \ "questions").write[List[QuestionRecord]]
  )(unlift(FullSurvey.unapply))

  implicit val surveyRecordWrites = new Writes[SurveyRecord] {
    def writes(survey: SurveyRecord) = Json.obj(
       "id" -> survey.id
      ,"name" -> survey.name
    )
  }

  val selectAllSurveysQuery = SQL("""SELECT S_ID,NAME FROM SURVEYS;""")

  private val surveyRecordRowParser = {
    get[Long]("S_ID") ~
    get[String]("name") map {
      case a~b => SurveyRecord(a,b)
    }
  }

  def selectAll = {
    val list: List[SurveyRecord] = DB.withConnection{implicit connection =>
      selectAllSurveysQuery.as(surveyRecordRowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }
}