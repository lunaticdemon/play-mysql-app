package controllers

import java.sql.DriverManager

import com.mysql.jdbc.Connection
import model.{User, UserForm}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import service.UserService

import scala.concurrent.ExecutionContext.Implicits.global

class ApplicationController extends Controller {
  implicit private val StatusWrites = Json.writes[User]

  def addUser(): Action[AnyContent] = Action.async { implicit request =>
    val requestJson = request.body.asJson.get
    val firstName = requestJson.\("firstName").as[String]
    val lastName = requestJson.\("lastName").as[String]
    val mobile = requestJson.\("mobile").as[Long]
    val email = requestJson.\("email").as[String]

    val newUser = User(0, firstName, lastName, mobile, email)
    UserService
      .addUser(newUser)
      .map(res => Ok(res))
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async {
    implicit request =>
      UserService.deleteUser(id) map { res =>
        Ok("user is deleted")
      }
  }
  def list(): Action[AnyContent] = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      Ok(Json.toJson(users))
    }
  }

  def testConnection() = Action.async { implicit request =>
    val requestJson = request.body.asJson.get
    val url = requestJson.\("url").as[String]
    val db = requestJson.\("db").as[String]
    val passWord = requestJson.\("password").as[String]
    val user = requestJson.\("user").as[String]

    Class.forName("com.mysql.jdbc.Driver")
    try {
      DriverManager.getConnection(url + s"/$db", user, passWord)
      Future.successful(Ok("connection established"))
    } catch {
      case e: Exception => Future.successful(Ok("invalid credentials"))

    }

  }
}
