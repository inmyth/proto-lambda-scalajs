import Handler.tableName
import cats.data.EitherT
import facade.amazonaws.services.dynamodb.{AttributeValue, DynamoDB, GetItemInput, StringAttributeValue}
import monix.eval.Task
import net.exoego.facade.aws_lambda.{APIGatewayProxyEvent, SNSMessage}
import cats.instances.future.catsStdInstancesForFuture
import facade.amazonaws.AWS
import monix.reactive.Observable

import scala.language.postfixOps
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.util.{Random, Try}
import facade.amazonaws.AWS
import facade.amazonaws.services.sns
import facade.amazonaws.services.sns._

import scala.concurrent.duration.DurationInt

object Logic extends App {
  import monix.execution.Scheduler.Implicits.global
  sealed trait Error {
    val code: Int
    val msg: String
  }

  object Error {
    final case object QueryParameterMissing extends Error {
      override val code: Int   = 422
      override val msg: String = "Incorrect query format"
    }

    final case class UserNotFound(myId: String) extends Error {
      override val code: Int   = 200
      override val msg: String = s"User not found for myId : $myId"
    }
  }

  case class User(name: js.UndefOr[StringAttributeValue], city: js.UndefOr[StringAttributeValue])

  def parseMyId(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext) =
    EitherT {
      Future(event.queryStringParameters.asInstanceOf[js.Dictionary[String]]("myid"))
        .map(Right.apply)
        .recoverWith(e => Future.successful(Left(Error.QueryParameterMissing: Error)))
    }

  def fetchUser(myId: String)(implicit ec: ExecutionContext) =
    EitherT {
      (for {
        record <- new DynamoDB()
          .getItemFuture(GetItemInput(Key = Dictionary("myId" -> AttributeValue.S(myId)), TableName = tableName))
        item <- Future(record.Item)
      } yield item)
        .map(p => Right(User(p.get("name").S, p.get("city").S)))
        .recoverWith(e => Future.successful(Left(Error.UserNotFound(myId): Error)))
    }

  def compa(event: APIGatewayProxyEvent)(implicit ec: ExecutionContext) =
    for {
      a <- parseMyId(event)
      b <- fetchUser(a)
    } yield b

  def comp(msg: String): EitherT[Task, Throwable, PublishResponse] = {
    /*
    var msg = {
        "default": "A message.",
        "email": "A message for email.",
        "email-json": "A message for email (JSON).",
        "http": "A message for HTTP.",
        "https": "A message for HTTPS.",
        "sqs": "A message for Amazon SQS."
    }

          var params = {
                    MessageStructure: "json",
                    Message: JSON.stringify({
                        default: JSON.stringify( msg )
                    }),
              			MessageGroupId: "user-123",
		              	MessageDeduplicationId: "53f4a129a6ad74166af9d33053cb1948",
                    TopicArn: "arn:aws:sns:ap-northeast-2:197367645398:testn.fifo"
                    };
     */
    val x = for {
      a <- Task.now {
        PublishInput(
          s"""
             |{
             | "default": "A message.",
             | "email": "A message for email.",
             | "email-json": "A message for email (JSON).",
             | "http": "A message for HTTP.",
             | "https": "A message for HTTPS.",
             | "sqs": "A message for Amazon SQS."
             |}          
             |""".stripMargin,
          js.undefined,
          js.undefined,
          "ResumeCertificateGroup",
          "json",
          js.undefined,
          "test subject",
          js.undefined,
          "arn:aws:sns:ap-northeast-2:197367645398:testn.fifo"
        )
      }
      b <- retryOnFailure(3, Task.fromFuture[PublishResponse](new SNS().publish(a).promise().toFuture))
    } yield b
    val y = x
      .map(p => Right(p))
      .onErrorHandle { p => Left(p) }
    EitherT(y)
  }

  def retryOnFailure[A](times: Int, source: Task[A]): Task[A] =
    source
    //      .flatMap(p => if (Random.nextInt(10) < 8) Task.raiseError(new Exception) else Task(p))
      .onErrorHandleWith { err =>
        if (times <= 0) Task.raiseError(err)
        else {
          println("retrying")
          retryOnFailure(times - 1, source).delayExecution(1 second)
        }
      }
}
