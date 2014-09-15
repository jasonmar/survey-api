package models

import anorm._
import anorm.SqlParser._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

object OrgModel {

  case class Org(name: String) {
    def insert(): Option[Long] = {
      val id: Option[Long] = DB.withConnection {
        implicit c =>
          SQL("INSERT INTO ORGS (NAME) values ({name})")
            .on("name" -> name)
            .executeInsert()
      }
      id
    }
  }

  val orgForm: Form[Org] = Form(
    mapping(
      "name" -> text
    )(Org.apply)(Org.unapply)
  )

  implicit val orgWrites = new Writes[Org] {
    def writes(org: Org) = Json.obj(
      "name" -> org.name
    )
  }

  val selectAllQ = SQL("""SELECT O_ID,NAME FROM ORGS;""")

  val org = {
    get[String]("name") map {
      case name => Org(name)
    }
  }

  def selectAll = {
    val orgs: List[Org] = DB.withConnection{implicit connection =>
      selectAllQ.as(org *)
    }
    orgs match {
      case x if x.nonEmpty => Some(x)
      case _ => None
    }
  }
}
