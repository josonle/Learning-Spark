package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Persist {
  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("persist_cache")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("data/spark.md").cache()
    println("all length : "+rdd.map(_.length).reduce(_+_))
    //2019-02-11 16:02:57 INFO  DAGScheduler:54 - Job 0 finished: reduce at Persist.scala:11, took 0.391666 s
    println("all_length not None:"+rdd.flatMap(_.split(" ")).map(_.length).reduce(_+_))
    //2019-02-11 16:02:58 INFO  DAGScheduler:54 - Job 1 finished: reduce at Persist.scala:12, took 0.036668 s
    
    //不持久化
    //2019-02-11 16:05:50 INFO  DAGScheduler:54 - Job 0 finished: reduce at Persist.scala:11, took 0.370967 s
    //2019-02-11 16:05:50 INFO  DAGScheduler:54 - Job 1 finished: reduce at Persist.scala:13, took 0.050201 s
  }
}