name := "TwitterCount"

version := "1.0"

scalaVersion := "2.10.4"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
   {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
   }
}

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming-twitter_2.10" % "1.2.0" 

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "3.0.3" 

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.35"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

