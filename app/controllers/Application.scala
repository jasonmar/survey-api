package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {implicit request =>
    val u_id: String = request.session.get("u_id") match {
      case Some(x) => x
      case _ => models.IdGenerator.newUser
    }
    Ok(views.html.index("u_id='" + u_id + "'  ip_addr='" + request.remoteAddress + "'"))
  }

  def responseForm(s_id: String) = Action {implicit request =>
    val u_id: String = request.session.get("u_id") match {
      case Some(x) => x
      case _ => models.IdGenerator.newUser
    }
    Ok(views.html.form(s_id))
  }

}