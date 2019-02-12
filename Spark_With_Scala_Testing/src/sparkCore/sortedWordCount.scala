package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._

object sortedWordCount {
  /*1、对文本文件内的每个单词都统计出其出现的次数。
  2、按照每个单词出现次数的数量，降序排序。
  */
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("sortedWordCount")
    val sc = new SparkContext(conf)
    val data = sc.textFile("data/spark.md", 1)
    data.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).sortBy(_._2, false)
      .foreach(x ⇒ {
        println(x._1 + "出现" + x._2 + "次")
      })
    //另一种方法，还不如sortBy
    anotherSolution(data).foreach(x ⇒ {
      println(x._1 + "出现" + x._2 + "次")
    })
  }

  def anotherSolution(data: RDD[String]): RDD[(String, Int)] = {
    //传入的rdd是所读取的文件rdd
    val wc = data.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    val countWords = wc.map(x => (x._2, x._1)).sortByKey(false)
    val sortedWC = countWords.map(x => (x._2, x._1))

    sortedWC
  }
}