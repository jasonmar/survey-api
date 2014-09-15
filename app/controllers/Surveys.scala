package controllers

import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import models.SurveyModel._

object Surveys extends Controller {

  def get = Action {
    val items: Option[List[Survey]] = selectAll
    Ok(views.html.survey(surveyForm, items))
  }

  def post = Action {
    implicit request =>
      val data = surveyForm.bindFromRequest.get
      data.insert()
      Ok(Json.toJson(data).toString())
  }

  def json = Action {
    val data = selectAll
    data match {
      case Some(list) => Ok(Json.toJson(list).toString())
      case _ => Ok
    }
  }

  def put = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def delete = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}