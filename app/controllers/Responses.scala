package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.ResponseModel._

object Responses extends Controller {

  def get = Action {
    val items: Option[List[ResponseRecord]] = getResponses
    Ok(views.html.responses(items))
  }

  /**
   *  Test script:
   *  curl --include --request POST --header "Content-type: application/json" \
   *  --data '{"s_id":1234,"responses":[{"q_id":56788,"response":5},{"q_id":56789,"response":3}]}' \
   *  http://localhost:9000/v1/responses
   */
  def post = Action(BodyParsers.parse.json) {request =>
    val result = request.body.validate[Responses]
    result.fold(
      errors =>
        BadRequest(
          Json.obj(
             "status"  -> "KO"
            ,"message" -> JsError.toFlatJson(errors))
        )
      ,responses => {
        saveResponses(responses)
        Ok(
          Json.obj(
            "status"  -> "OK"
            ,"message" -> ("Responses for s_id " + responses.s_id + " saved.")
          )
        )
      }
    )
  }

  def json = Action {
    val data: Option[List[ResponseRecord]] = getResponses
    data match {
      case Some(list) => Ok(Json.toJson(list).toString())
      case _ => Ok
    }
  }

}