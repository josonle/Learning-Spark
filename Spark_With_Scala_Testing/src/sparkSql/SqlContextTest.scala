package sparkSql

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

object SqlContextTest {
  //  Spark2.x中SQLContext已经被SparkSession代替，此处只为了解
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("test sqlContext")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //    读取spark项目中example中带的几个示例数据，创建DataFrame
    val people = sqlContext.read.format("json").load("data/people.json")
    //    DataFrame即RDD+Schema（元数据信息）
    people.show() //打印DF
    people.printSchema() //打印DF结构
  }
}