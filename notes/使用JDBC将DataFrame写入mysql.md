# spark foreachPartition 把df 数据插入到mysql

> 转载自：http://www.waitingfy.com/archives/4370，确实写的不错

```scala
import java.sql.{Connection, DriverManager, PreparedStatement}
 
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
 
import scala.collection.mutable.ListBuffer
 
object foreachPartitionTest {
 
  case class TopSongAuthor(songAuthor:String, songCount:Long)
 
 
  def getConnection() = {
    DriverManager.getConnection("jdbc:mysql://localhost:3306/baidusong?user=root&password=root&useUnicode=true&characterEncoding=UTF-8")
  }
 
  def release(connection: Connection, pstmt: PreparedStatement): Unit = {
    try {
      if (pstmt != null) {
        pstmt.close()
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (connection != null) {
        connection.close()
      }
    }
  }
 
  def insertTopSong(list:ListBuffer[TopSongAuthor]):Unit ={
 
     var connect:Connection = null
     var pstmt:PreparedStatement = null
 
     try{
         connect = getConnection()
       connect.setAutoCommit(false)
       val sql = "insert into topSinger(song_author, song_count) values(?,?)"
       pstmt = connect.prepareStatement(sql)
       for(ele <- list){
          pstmt.setString(1, ele.songAuthor)
          pstmt.setLong(2,ele.songCount)
 
          pstmt.addBatch()
       }
       pstmt.executeBatch()
       connect.commit()
     }catch {
       case e:Exception => e.printStackTrace()
     }finally {
         release(connect, pstmt)
     }
  }
 
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("foreachPartitionTest")
      .getOrCreate()
    val gedanDF = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306").option("dbtable", "baidusong.gedan").option("user", "root").option("password", "root").option("driver", "com.mysql.jdbc.Driver").load()
//    mysqlDF.show()
    val detailDF = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306").option("dbtable", "baidusong.gedan_detail").option("user", "root").option("password", "root").option("driver", "com.mysql.jdbc.Driver").load()
 
    val joinDF = gedanDF.join(detailDF, gedanDF.col("id") === detailDF.col("gedan_id"))
 
//    joinDF.show()
    import spark.implicits._
    val resultDF = joinDF.groupBy("song_author").agg(count("song_name").as("song_count")).orderBy($"song_count".desc).limit(100)
//    resultDF.show()
 
 
    resultDF.foreachPartition(partitionOfRecords =>{
       val list = new ListBuffer[TopSongAuthor]
       partitionOfRecords.foreach(info =>{
           val song_author = info.getAs[String]("song_author")
           val song_count = info.getAs[Long]("song_count")
 
           list.append(TopSongAuthor(song_author, song_count))
       })
      insertTopSong(list)
 
    })
 
    spark.close()
  }
 
}
```



上面的例子是用[《python pandas 实战 百度音乐歌单 数据分析》](http://www.waitingfy.com/archives/4105)用spark 重新实现了一次

默认的foreach的性能缺陷在哪里？

首先，对于每条数据，都要单独去调用一次function，task为每个数据，都要去执行一次function函数。
如果100万条数据，（一个partition），调用100万次。性能比较差。

另外一个非常非常重要的一点
如果每个数据，你都去创建一个数据库连接的话，那么你就得创建100万次数据库连接。
但是要注意的是，数据库连接的创建和销毁，都是非常非常消耗性能的。虽然我们之前已经用了
数据库连接池，只是创建了固定数量的数据库连接。

你还是得多次通过数据库连接，往数据库（MySQL）发送一条SQL语句，然后MySQL需要去执行这条SQL语句。
如果有100万条数据，那么就是100万次发送SQL语句。

以上两点（数据库连接，多次发送SQL语句），都是非常消耗性能的。

foreachPartition，在生产环境中，通常来说，都使用foreachPartition来写数据库的

使用批处理操作（一条SQL和多组参数）
发送一条SQL语句，发送一次
一下子就批量插入100万条数据。

用了foreachPartition算子之后，好处在哪里？

1、对于我们写的function函数，就调用一次，一次传入一个partition所有的数据
2、主要创建或者获取一个数据库连接就可以
3、只要向数据库发送一次SQL语句和多组参数即可

参考《算子优化 foreachPartition》 https://blog.csdn.net/u013939918/article/details/60881711