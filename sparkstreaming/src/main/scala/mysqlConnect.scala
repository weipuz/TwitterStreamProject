package org.apache.spark.examples.streaming

import java.sql.DriverManager
import java.sql.Connection
import java.sql.Timestamp
 
/**
 * A Scala JDBC connection example by Alvin Alexander,
 * <a href="http://alvinalexander.com" title="http://alvinalexander.com">http://alvinalexander.com</a>
 */
object ScalaJdbcConnectSelect {
 
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://hashtagdb.ctscqnvckzf1.us-east-1.rds.amazonaws.com:3306/hashtagDB"
    val username = "weipuz"
    val password = "twitterhashtag"
    //val url = "jdbc:mysql://mysql14.000webhost.com/a6206192_db"
    //val username = "a6206192_shijiel"
    //val password = "123456lili"

 
    // there's probably a better way to do this
    var connection:Connection = null
 
    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      val create = connection.createStatement()
      create.executeUpdate("CREATE TABLE IF NOT EXISTS hashtag60s (time datetime, name varchar(255), value int, PRIMARY KEY (time, name))")

      val time:Timestamp = new Timestamp(System.currentTimeMillis) 
      println(time.toString())
      TwitterCount.sendRecord(connection,1,"#test",time)
/*
      val prep = connection.prepareStatement("INSERT INTO quotes (quote, author) VALUES (?, ?) ")
      prep.setString(1, "Nothing great was ever achieved without enthusiasm.")
      prep.setString(2, "Ralph Waldo Emerson2")
      prep.executeUpdate
 */
      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT time,name, value FROM hashtag60s")
      while ( resultSet.next() ) {
        val time = resultSet.getTimestamp("time").toString()
        val name = resultSet.getString("name")
        println("time, name = " + time + ", " + name)
      }
    } catch {
      case e => e.printStackTrace
    }
    connection.close()
  }
 
}
