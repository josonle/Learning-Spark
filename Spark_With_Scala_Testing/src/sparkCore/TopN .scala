package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._

object TopN {
  //Top3
  def top3(data:RDD[String]): Array[String] = {
    if(data.isEmpty()){
      println("RDD为空,返回空Array")
      Array()
    }
    if(data.count()<=3){
      data.collect()
    }else {
      val sortedData = data.map(x=>(x,x.split(" ")(1).toInt)).sortBy(_._2, false)
      val result = sortedData.take(3).map(_._1)
      result
    }
  }
  //分组TopN
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
        .setAppName("Top3")
        .setMaster("local")  
    val sc = new SparkContext(conf)
    val lines = sc.textFile("data/topN.txt").cache()
    
    top3(lines).foreach(println)
  }
}