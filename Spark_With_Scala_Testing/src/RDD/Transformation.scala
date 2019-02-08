package RDD

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object Transformation {
  def main(args: Array[String]): Unit = {
    System.out.println("Start***********")
    /*getData("data/hello.txt")
    getData("", Array(1,2,3,4,5))*/
    
    /*map_flatMap_result()*/
    distinct_result()
  }

  //### 数据来源
  
  def getData(path: String, arr: Array[Int]=Array()) {
    // path：file:// 或 hdfs://master:9000/ [指定端口9000]
    val conf = new SparkConf().setMaster("local").setAppName("getData")
    val sc = new SparkContext(conf)

    if (!arr.isEmpty) {
      sc.parallelize(arr).map(_ + 1).foreach(println)
    } else {
      sc.textFile(path,1).foreach(println)   
    }
    sc.stop()
  }
  
  def map_flatMap_result(){
    val conf = new SparkConf().setMaster("local").setAppName("map_flatMap")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("data/hello.txt", 1)
    val mapResult = rdd.map(_.split(","))
    val flatMapResult = rdd.flatMap(_.split(","))
    
    mapResult.foreach(println)
    flatMapResult.foreach(println)
    
    println("差别×××××××××")
    flatMapResult.map(_.toUpperCase).foreach(println)
    flatMapResult.flatMap(_.toUpperCase).foreach(println)
  }
  
  def distinct_result(){
    val conf = new SparkConf().setMaster("local").setAppName("map_flatMap")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("data/hello.txt", 1)
    rdd.flatMap(_.split(",")).distinct().foreach(println)
  }
  
  
}
