lazy val root = project.enablePlugins(ScalaJSPlugin)

name := "proto-lambda-scalajs"

scalaVersion := "2.13.4"
//scalacOptions ++= Seq(
//  "-deprecation",
//  "-encoding",
//  "UTF-8",
//  // Explain type errors in more detail.
//  "-explaintypes",
//  // Warn when we use advanced language features
//  "-feature",
//  // Give more information on type erasure warning
//  "-unchecked",
//  // Enable warnings and lint
//  "-Ywarn-unused",
//  "-Xlint"
//)
//version in webpack := "4.16.1"
//useYarn := true
//webpackConfigFile := Some(baseDirectory.value / "webpack.config.js")
//version in startWebpackDevServer := "3.1.4"

// Optional: Disable source maps to speed up compile times
//scalaJSLinkerConfig ~= { _.withSourceMap(false) }

// Incluce type defintion for aws lambda handlers
libraryDependencies += "net.exoego" %%% "aws-lambda-scalajs-facade_sjs1" % "0.11.0"
// Optional: Include the AWS SDK as a dep

//val awsSdkVersion              = "v2.798.0"
//val awsSdkScalajsFacadeVersion = s"0.32.0-${awsSdkVersion}"
//libraryDependencies += "net.exoego" %%% "aws-sdk-scalajs-facade-dynamodb" % "0.32.0-v2.798.0"
//enablePlugins(ScalaJSBundlerPlugin)
//npmDependencies += "aws-sdk" -> "2.731.0"

// Optional: Include some nodejs types (useful for, say, accessing the env)
//libraryDependencies += "net.exoego" %%% "scala-js-nodejs-v12" % "0.9.1"

// Include scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
// Other dependencies
libraryDependencies += "io.monix" %%% "monix" % "3.2.2"

// Package lambda as a zip. Use `universal:packageBin` to create the zip
//topLevelDirectory := None
//mappings in Universal ++= (webpack in (Compile, fullOptJS)).value.map { f =>
//  // remove the bundler suffix from the file names
//  f.data -> f.data.getName().replace("-opt-bundle", "")
//}
