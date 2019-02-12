package test

import org.apache.spark.{SparkConf,SparkContext}

class Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("test-tools")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(List())
    println("测试：" + rdd.count)

    if (sc.emptyRDD[(String, Int)].isEmpty()) {
      println("空")
    }
  }
}