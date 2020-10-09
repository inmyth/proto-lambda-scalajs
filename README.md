# Prototype AWS Lambda in Scala.js

A prototype of AWS Lambda function written in scala.js. 
Connects to DynamoDb and returns the result as http response. 

- Gitter Template
https://github.com/bgahagan/scalajs-lambda.g8
- AWS Facades
https://github.com/exoego/aws-sdk-scalajs-facade
https://github.com/exoego/aws-lambda-scalajs-facade

First commit contains updated template that works by 2020 Oct.
bgahagan's template works. Updating dependencies may cause it to stop working (no resulting js, if it works shell should show "Fast optimizing target\scala-2.13\scalajs-bundler\main\project-name.js").

Steps:
- clone the gitter template or revert to first commit.
- update dependencies on `plugins.sbt` (in my experience updating just one dependency didn't work), 'build.sbt'
- add necessary facades
This project uses DynamoDB so just do `libraryDependencies += "net.exoego" %%% "aws-sdk-scalajs-facade-dynamodb" % awsSdkScalajsFacadeVersion`.
The other alternative is to use the entire bundled facades (artifact `aws-sdk-scalajs-facade`) which is huge. 
- run `sbt`
- enter `~fastOptJS::webpack` which hotloads packaging into js
- to test if it really works, delete `scalajs-bundler` and repeat the command
- paste the content of the js file on AWS Lambda editor

Environment:
- create a function in AWS Lambda with basic role
- create a table in DynamoDb, populate it with something
- take note of the table's ARN
- go to IAM, find the role created for Lambda, add inline policy with that ARN



