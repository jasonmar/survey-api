package models

import anorm.SqlParser._
import anorm._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.libs.json.{Json, Writes}
import play.api.Play.current

object QuestionModel {

  case class QuestionRecord(q_id: String = IdGenerator.newQuestion, question: Question)
  case class Question(text: String, hint1: String, hint3: String, hint5: String)

  def insertQuestion(q: Question): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL("INSERT INTO QUESTIONS (Q_ID,TEXT,HINT1,HINT3,HINT5) values ({q_id},{text},{hint1},{hint3},{hint5})")
          .on("q_id"  -> IdGenerator.newQuestion)
          .on("text"  -> q.text)
          .on("hint1" -> q.hint1)
          .on("hint3" -> q.hint3)
          .on("hint5" -> q.hint5)
          .executeInsert()
    }
  }

  val questionForm = Form(
    mapping(
       "text"  -> text
      ,"hint1" -> text
      ,"hint3" -> text
      ,"hint5" -> text
    )(Question.apply)(Question.unapply)
  )

  implicit val questionWrites = new Writes[Question] {
    def writes(x: Question) = Json.obj(
       "text"  -> x.text
      ,"hint1" -> x.hint1
      ,"hint3" -> x.hint3
      ,"hint5" -> x.hint5
    )
  }

  implicit val questionRecordWrites = new Writes[QuestionRecord] {
    def writes(x: QuestionRecord) = Json.obj(
       "q_id"  -> x.q_id
      ,"text"  -> x.question.text
      ,"hint1" -> x.question.hint1
      ,"hint3" -> x.question.hint3
      ,"hint5" -> x.question.hint5
    )
  }

  private val questionRowParser = {
    get[String]("q_id") ~
    get[String]("text") ~
    get[String]("hint1") ~
    get[String]("hint3") ~
    get[String]("hint5") map {
      case a~b~c~d~e => QuestionRecord(a,Question(b,c,d,e))
    }
  }

  def getQuestions = {
    val list: List[QuestionRecord] = DB.withConnection{implicit connection =>
      SQL("""SELECT Q_ID,TEXT,HINT1,HINT3,HINT5 FROM QUESTIONS;""")
        .as(questionRowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }

  def getQuestionsBySurveyID(surveyId: String): Option[List[QuestionRecord]] = {
    val list: List[QuestionRecord] = DB.withConnection{implicit connection =>
      SQL("""
SELECT A.Q_ID,TEXT,HINT1,HINT3,HINT5
FROM QUESTIONS A
INNER JOIN SURVEY_QUESTIONS B
  ON A.Q_ID = B.Q_ID
WHERE B.S_ID = {S_ID}
;"""
      )
        .on("S_ID" -> surveyId)
        .as(questionRowParser *)
    }
    list match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }

}
