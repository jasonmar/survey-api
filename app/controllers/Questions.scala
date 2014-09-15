package controllers

import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import models.QuestionModel._

object Questions extends Controller {

  def get = Action {
    val items: Option[List[Question]] = selectAll
    Ok(views.html.questions(questionForm,items))
  }

  def post = Action {
    implicit request =>
      val data = questionForm.bindFromRequest.get
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

  def delete = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}