import monix.eval.Task
import monix.reactive.{Consumer, Observable}

import scalajs.js
import js.annotation.JSExportTopLevel
import net.exoego.facade.aws_lambda._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

object Handler {
  val tableName   = "proto-lambda-scalajs"
  implicit val ec = monix.execution.Scheduler.Implicits.global

  def main(event: APIGatewayProxyEvent): Future[APIGatewayProxyResult] = {

    Logic.comp("asdasda").value.map {
      case Left(e) =>
        APIGatewayProxyResult(statusCode = 500, body = s"""
            |{
            |"code" : 500,
            |"msg" : ${e.getMessage}
            |}
            |""".stripMargin, headers = js.defined(js.Dictionary("Content-Type" -> "application/json")))

      case Right(p) =>
        APIGatewayProxyResult(statusCode = 200, body = s"""
             |{
             |"code" : 200,
             |"msg" : "successs, ${p.MessageId}"
             |}
             |""".stripMargin, headers = js.defined(js.Dictionary("Content-Type" -> "application/json")))
    }
  }.runToFuture

  @JSExportTopLevel(name = "handler")
  val handler: js.Function2[APIGatewayProxyEvent, Context, js.Promise[APIGatewayProxyResult]] = {
    (event: APIGatewayProxyEvent, _: Context) =>
      import js.JSConverters._
      main(event).toJSPromise
  }

}
