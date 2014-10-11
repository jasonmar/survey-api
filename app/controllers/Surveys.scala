package controllers

import models.SurveyUpdateModel.{SurveyQuestions,addSurveyQuestions}
import play.api._
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.SurveyModel._

object Surveys extends Controller {

  def get = Action {
    val items: Option[List[SurveyRecord]] = selectAll
    Ok(views.html.survey(surveyForm, items))
  }

  def post = Action {
    implicit request =>
      val survey = surveyForm.bindFromRequest.get
      insertSurvey(survey)
      //Redirect(routes.Surveys.json())
      Ok("'" + survey.name + "' inserted")
  }

  def json = Action {
    val data = selectAll
    data match {
      case Some(list) => Ok(Json.toJson(list).toString())
      case _ => Ok
    }
  }

  def getSurveyById(id: Long) = Action {
    getFullSurvey(id) match {
      case Some(x) => Ok(Json.toJson(x).toString())
      case _ => NotFound("Survey with id " + id.toString + " not found")
    }
  }

  /**
   * Example insert script
   *
   * curl --include --request POST --header "Content-type: application/json" \
   * --data '{"s_id":1, "q_ids":[1,2,3]}' http://localhost:9000/v1/surveys/1
   *
   */

  def addQuestionsToSurvey(id: Long) = Action(BodyParsers.parse.json) {request =>
    val result = request.body.validate[SurveyQuestions]
    result.fold(
      errors =>
        BadRequest(
          Json.obj(
            "status"  -> "KO"
            ,"message" -> JsError.toFlatJson(errors))
        )
      ,surveyQuestions => {
        addSurveyQuestions(surveyQuestions)
        Ok(
          Json.obj(
            "status"  -> "OK"
            ,"message" -> (surveyQuestions.q_ids.length.toString + " questions for s_id " + surveyQuestions.s_id + " added.")
          )
        )
      }
    )
  }

}