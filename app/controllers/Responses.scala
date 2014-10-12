package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.ResponseModel._
import models.CacheModel.actionCounter

object Responses extends Controller {

  def get = Action {
    val items: Option[List[ResponseRecord]] = getResponses
    Ok(views.html.responses(items))
  }

  /**
   *  Test script:
   *  curl --include --request POST --header "Content-type: application/json" \
   *  --data '
   *  {
   *  "s_id": "srv_g6NFlzCY8R9O0gBUmiQQpmje"
   *  ,"responses":[
   *  {"q_id":"qst_ZsNz8lFmjmFQL4NEhUdbgqBh", "response":5}
   *  ,{"q_id":"qst_TFqVM5ht6HPzrsE7NemNzH9T", "response":4}
   *  ,{"q_id":"qst_i5qafjyTFFKwfL8VETSVx0mQ", "response":3}
   *  ]
   *  }' \
   *  http://localhost:9000/v1/responses
   */
  def post = Action(BodyParsers.parse.json) {request =>
    if (actionCounter(request.remoteAddress, "postResponse") > 5) {
      TooManyRequest("Too many requests")
    } else {
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
  }

  def json = Action {
    val data: Option[List[ResponseRecord]] = getResponses
    data match {
      case Some(list) => Ok(Json.toJson(list).toString())
      case _ => Ok
    }
  }

}