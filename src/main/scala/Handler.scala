import scalajs.js
import js.annotation.JSExportTopLevel
import net.exoego.facade.aws_lambda._

import scala.concurrent.{ExecutionContext, Future}

object Handler {
  val tableName = "proto-lambda-scalajs"

  def main(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext): Future[APIGatewayProxyResult] = {
    Logic.comp(event).value.map {
      case Left(e) =>
        APIGatewayProxyResult(
          statusCode = e.code,
          body =
            s"""
               |{
               |"code" : ${e.code},
               |"msg" : ${e.msg}
               |}
               |""".stripMargin,
          headers = js.defined(js.Dictionary("Content-Type" -> "text/json"))
      )

      case Right(user) =>
        APIGatewayProxyResult(
          statusCode = 200,
          body =
            s"""
               |{
               |"name" : ${user.name.toString},
               |"city" : ${user.city.toString}
               |}
               |""".stripMargin,
          headers = js.defined(js.Dictionary("Content-Type" -> "text/json"))
        )
    }
  }

  @JSExportTopLevel(name="handler")
  val handler: js.Function2[APIGatewayProxyEvent, Context, js.Promise[APIGatewayProxyResult]] = {
    (event: APIGatewayProxyEvent, _: Context) =>
      import js.JSConverters._
      implicit val ec = ExecutionContext.global
      main(event).toJSPromise
  }

}