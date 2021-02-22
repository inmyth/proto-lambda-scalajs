import monix.eval.Task
import monix.reactive.{Consumer, Observable}

import scalajs.js
import js.annotation.JSExportTopLevel
import net.exoego.facade.aws_lambda._

import scala.language.postfixOps
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

object Handler {
  val tableName   = "proto-lambda-scalajs"
  implicit val ec = monix.execution.Scheduler.Implicits.global

  def main(event: APIGatewayProxyEvent): Future[APIGatewayProxyResult] = {
    Logic.comp("asdasda").map {
//      case Left(e) =>
//        APIGatewayProxyResult(statusCode = e.code, body = s"""
//               |{
//               |"code" : ${e.code},
//               |"msg" : "${e.msg}"
//               |}
//               |""".stripMargin, headers = js.defined(js.Dictionary("Content-Type" -> "application/json")))
//
//      case Right(user) =>
//        APIGatewayProxyResult(statusCode = 200, body = s"""
//               |{
//               |"name" : "${user.name.toString}",
//               |"city" : "${user.city.toString}"
//               |}
//               |""".stripMargin, headers = js.defined(js.Dictionary("Content-Type" -> "application/json")))
      case Left(e) => {
        println(e)
        APIGatewayProxyResult(
          statusCode = 400,
          body = s"""
                                                          |{
                                                          |"code" : 400,
                                                          |"msg" : "notok"
                                                          |}
                                                          |""".stripMargin,
          headers = js.defined(js.Dictionary("Content-Type" -> "application/json"))
        )
      }

      case Right(user) =>
        APIGatewayProxyResult(statusCode = 200, body = s"""
             |{
             |"code" : 200,
             |"msg" : "------"
             |}
             |""".stripMargin, headers = js.defined(js.Dictionary("Content-Type" -> "application/json")))

    }

  }.runToFuture

  def retryOnFailure(times: Int, source: Task[Int]): Task[Int] =
    source
      .flatMap(p => if (Random.nextInt(10) < 8) Task.raiseError(new Exception) else Task(p))
      .onErrorHandleWith { err =>
        if (times <= 0) Task.raiseError(err)
        else {
          println("retrying")
          retryOnFailure(times - 1, source).delayExecution(1 second)
        }
      }

  @JSExportTopLevel(name = "handler")
  val handler: js.Function2[APIGatewayProxyEvent, Context, js.Promise[APIGatewayProxyResult]] = {
    (event: APIGatewayProxyEvent, _: Context) =>
      import js.JSConverters._
      main(event).toJSPromise
  }

}
