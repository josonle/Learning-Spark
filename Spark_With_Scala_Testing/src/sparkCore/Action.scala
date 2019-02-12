package sparkCore

import org.apache.spark.{SparkConf,SparkContext}

object Action {
  def main(args: Array[String]): Unit = {
    /*Action操作不作过多解释*/
    val conf = new SparkConf().setMaster("local").setAppName("Action")
    val sc = new SparkContext(conf)
    
   /* val rdd = sc.parallelize((1 to 10), 1)
    println(rdd.take(3))  //返回数组
    println(rdd.reduce(_+_))
    println(rdd.collect())    //返回数组
    
    val wc = sc.textFile("data/hello.txt", 1)
    wc.flatMap(_.split(",")).map((_,1)).reduceByKey(_+_)
    .saveAsTextFile("data/result")    //只能指定保存的目录
    //countByKey见Transformation中求解平均成绩
    */
    sc.stop()
  }  
}