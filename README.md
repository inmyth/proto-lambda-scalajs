# AWS Lambda Prototype in Scala.js

A prototype of AWS Lambda function written in scala.js. 
Connects to DynamoDb and returns the result as http response. 

Important:
- sbt run and test will run on node so install it first
- `%%%` is for scala.js lib. `%%` is for normal scala lib. Make sure dependencies in build.sbt are reflected appropriately. 

- Gitter Template
https://github.com/bgahagan/scalajs-lambda.g8
- AWS Facades
https://github.com/exoego/aws-sdk-scalajs-facade
https://github.com/exoego/aws-lambda-scalajs-facade
  
Steps:
- clone the gitter template
- update dependencies on `plugins.sbt`
- add necessary facades
If DynamoDB `libraryDependencies += "net.exoego" %%% "aws-sdk-scalajs-facade-dynamodb" % awsSdkScalajsFacadeVersion`.
The other alternative is to use the entire bundled facades (artifact `aws-sdk-scalajs-facade`) which is huge. 
- run `sbt`
- enter `~fastOptJS::webpack` which hotloads packaging into js
- for production run `sbt fullLinkJS`, result is single file `main.js` in scala.2.13/<projectname>-opt
- to test if it really works, delete `scalajs-bundler` and repeat the command
- paste the content of the js file on AWS Lambda editor

AWS Environment:
- create a function in AWS Lambda with basic role
- create a table in DynamoDb, populate it with something
- take note of the table's ARN
- go to IAM, find the role created for Lambda, add inline policy with that ARN
- to test it as public API, enable API Gateway