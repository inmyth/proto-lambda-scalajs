import Handler.tableName
import cats.data.EitherT
import facade.amazonaws.services.dynamodb.{AttributeValue, DynamoDB, GetItemInput, StringAttributeValue}
import monix.eval.Task
import net.exoego.facade.aws_lambda.APIGatewayProxyEvent
import cats.instances.future.catsStdInstancesForFuture

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.util.Try



object Logic extends App{
  import monix.execution.Scheduler.Implicits.global
  sealed trait Error {
    val code : Int
    val msg : String
  }

  object Error {
    final case object QueryParameterMissing extends Error {
      override val code: Int = 422
      override val msg: String = "Incorrect query format"
    }

    final case class UserNotFound(myId: String) extends Error {
      override val code: Int = 200
      override val msg: String = s"User not found for myId : $myId"
    }
  }

  case class User(name: js.UndefOr[StringAttributeValue], city: js.UndefOr[StringAttributeValue])

  def parseMyId(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext) = EitherT{
    Future(event.queryStringParameters.asInstanceOf[js.Dictionary[String]]("myid"))
      .map(Right.apply)
      .recoverWith(e => Future.successful(Left(Error.QueryParameterMissing : Error)))
  }

  def fetchUser(myId: String)(implicit ec: ExecutionContext) = EitherT{
    (for {
      record <- new DynamoDB().getItemFuture(
        GetItemInput(
          Key = Dictionary("myId" -> AttributeValue.S(myId)),
          TableName = tableName
        ))
      item <- Future(record.Item)
    } yield item)
      .map(p => Right(new User(p.get("name").S, p.get("city").S)))
      .recoverWith(e => Future.successful(Left(Error.UserNotFound(myId): Error)))
  }

  def comp(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext) =
    for{
      a <- parseMyId(event)
      b <- fetchUser(a)
    } yield b

}
