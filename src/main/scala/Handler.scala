import facade.amazonaws.services.dynamodb.{AttributeValue, DynamoDB, GetItemInput}

import scalajs.js
import js.annotation.JSExportTopLevel
import net.exoego.facade.aws_lambda._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dictionary

object Handler {
  val tableName = "proto-lambda-scalajs"

  def main(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext): Future[APIGatewayProxyResult] =
    for {
      item <- fetchDynamo()
      response <- Future {APIGatewayProxyResult(
        statusCode = 200,
        body =
          s"""
             |{
             |"name" : "${item.get("name").S.toString}",
             |"city" : "${item.get("city").S.toString}"
             |}
             |""".stripMargin,
        headers = js.defined(js.Dictionary("Content-Type" -> "text/json"))
      )}
    } yield response

  def fetchDynamo()(implicit ec: ExecutionContext) =
    for {
      record <- new DynamoDB().getItemFuture(
        GetItemInput(
          Key = Dictionary("myId" -> AttributeValue.S("abc")),
          TableName = tableName
        ))
      item <- Future(record.Item)
    } yield item


  @JSExportTopLevel(name="handler")
  val handler: js.Function2[APIGatewayProxyEvent, Context, js.Promise[APIGatewayProxyResult]] = {
    (event: APIGatewayProxyEvent, _: Context) =>
      import js.JSConverters._
      implicit val ec = ExecutionContext.global
      main(event).toJSPromise
  }

}