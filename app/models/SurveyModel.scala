package models

import anorm._
import anorm.SqlParser._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

object SurveyModel {

  case class Survey(name: String){
    def insert(): Option[Long] = {
      val id: Option[Long] = DB.withConnection {
        implicit c =>
          SQL("INSERT INTO SURVEYS (NAME) values ({name})")
            .on("name" -> name)
            .executeInsert()
      }
      id
    }
  }
  
  val surveyForm = Form(
    mapping(
      "name" -> text
    )(Survey.apply)(Survey.unapply)
  )

  implicit val surveyWrites = new Writes[Survey] {
    def writes(survey: Survey) = Json.obj(
      "name" -> survey.name
    )
  }

  val selectAllQ = SQL("""SELECT S_ID,NAME FROM SURVEYS;""")

  val rowParser = {
    get[String]("name") map {
      case name => Survey(name)
    }
  }

  def selectAll = {
    val list: List[Survey] = DB.withConnection{implicit connection =>
      selectAllQ.as(rowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }
}
