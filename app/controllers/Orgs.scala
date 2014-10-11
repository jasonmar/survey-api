package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.OrgModel._

object Orgs extends Controller {

  def get = Action {
    val orgs: Option[List[Org]] = selectAll
    Ok(views.html.org(orgForm, orgs))
  }

  def post = Action {
    implicit request =>
      val orgData = orgForm.bindFromRequest.get
      orgData.insert()
      Ok(Json.toJson(orgData).toString())
  }

  def json = Action {
    val data = selectAll
    data match {
      case Some(list) => Ok(Json.toJson(list).toString())
      case _ => Ok
    }
  }

}