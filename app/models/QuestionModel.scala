package models

import anorm.SqlParser._
import anorm._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.libs.json.{Json, Writes}
import play.api.Play.current

object QuestionModel {

  case class Question(text: String, hint1: String, hint3: String, hint5: String){
    def insert(): Option[Long] = {
      val id: Option[Long] = DB.withConnection {
        implicit c =>
          SQL("INSERT INTO QUESTIONS (TEXT,HINT1,HINT3,HINT5) values ({text},{hint1},{hint3},{hint5})")
            .on("text"  -> text)
            .on("hint1" -> hint1)
            .on("hint3" -> hint3)
            .on("hint5" -> hint5)
            .executeInsert()
      }
      id
    }
  }

  val questionForm = Form(
    mapping(
      "text" -> text
      ,"hint1" -> text
      ,"hint3" -> text
      ,"hint5" -> text
    )(Question.apply)(Question.unapply)
  )

  implicit val surveyWrites = new Writes[Question] {
    def writes(x: Question) = Json.obj(
      "text" -> x.text
      ,"hint1" -> x.hint1
      ,"hint3" -> x.hint3
      ,"hint5" -> x.hint5
    )
  }

  val selectAllQ = SQL("""SELECT Q_ID,TEXT,HINT1,HINT3,HINT5 FROM QUESTIONS;""")

  val rowParser = {
    get[String]("text") ~
    get[String]("hint1") ~
    get[String]("hint3") ~
    get[String]("hint5") map {
      case a~b~c~d => Question(a,b,c,d)
    }
  }

  def selectAll = {
    val list: List[Question] = DB.withConnection{implicit connection =>
      selectAllQ.as(rowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }

}
