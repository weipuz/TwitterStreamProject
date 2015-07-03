package org.apache.spark.examples.streaming

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter._
import java.sql.DriverManager
import java.sql.Connection
import java.sql.Timestamp
import com.github.nscala_time.time.Imports._


object TwitterCount {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: TwitterCount <consumer key> <consumer secret> " +
        "<access token> <access token secret> [<filters>]")
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    val filters = args.takeRight(args.length - 4)
    

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generat OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    val sparkConf = new SparkConf().setAppName("TwitterCount")
    val ssc = new StreamingContext(sparkConf, Seconds(60))
    val stream = TwitterUtils.createStream(ssc, None, filters)
    //reg exp to represent all non-printable characters. We use this to filter out non-printable hashtags
    val reg = new scala.util.matching.Regex("[\\P{InBasic_Latin}]") 
    val hashTags = stream.flatMap(status => {
        status.getText.split(" ")
              .filter(words => words.startsWith("#") && reg.findAllIn(words).isEmpty) 
        })          
    val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
                     .map{case (topic, count) => (count, topic)}
                     .transform(_.sortByKey(false))
    // Print popular hashtags and put the top 100 hashtags into DB
    topCounts60.foreachRDD(rdd => {
      val topList = rdd.take(100)
      println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
      val connection = createConnection()
      val time:Timestamp = new Timestamp(System.currentTimeMillis)
      println(time.toString())
      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
      topList.foreach{case (count, tag) => sendRecord(connection, count, tag, time)}
      connection.close()
      
    })

   
    ssc.start()
    ssc.awaitTermination()
  }

  def sendRecord(conn:Connection, count:Int, tag:String, time:Timestamp) {

    val prep = conn.prepareStatement("INSERT INTO hashtag60s (time, name, value) VALUES (?, ? ,?) ")
    prep.setTimestamp(1, time)
    prep.setString(2, tag)
    prep.setInt(3, count)
    prep.executeUpdate

  }

  def createConnection() : Connection = {

/*----------------------------------------*/
//Database Connection Secure Info! Please keep safe! //
//for code demo only. will be invalid after the grading period.//
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://hashtagdb.ctscqnvckzf1.us-east-1.rds.amazonaws.com:3306/hashtagDB"
    val username = "#######"
    val password = "#######"
/*----------------------------------------*/

    var connection:Connection = null
    try{
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
    } catch { 
      case e => e.printStackTrace
    }
    return connection 

  }

}
