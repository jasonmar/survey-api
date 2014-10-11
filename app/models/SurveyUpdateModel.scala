package models

import anorm._
import play.api.db.DB
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._
import play.api.Play.current

object SurveyUpdateModel {
  
  case class SurveyQuestions (s_id: Long, q_ids: List[Long])

  implicit val surveyQuestionsReads: Reads[SurveyQuestions] = (
    (JsPath \ "s_id").read[Long] and
    (JsPath \ "q_ids").read[List[Long]]
  )(SurveyQuestions.apply _)

  def addQuestionToSurvey(s_id: Long, q_id: Long): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL("INSERT INTO SURVEY_QUESTIONS (S_ID,Q_ID) values ({S_ID},{Q_ID})")
          .on("S_ID"  -> s_id)
          .on("Q_ID" -> q_id)
          .executeInsert()
    }
  }

  def addSurveyQuestions(x: SurveyQuestions): Unit =  x.q_ids.foreach{q_id => addQuestionToSurvey(x.s_id,q_id)}

}
