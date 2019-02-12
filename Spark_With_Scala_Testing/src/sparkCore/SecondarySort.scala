package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object SecondarySort {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("sortedWordCount")
    val sc = new SparkContext(conf)
    val data = sc.textFile("data/secondarySort.txt", 1)
    val keyWD = data.map(x => (
      new MyKey(x.split(" ")(0).toInt, x.split(" ")(1).toInt), x))
    val sortedWD = keyWD.sortByKey()
    sortedWD.map(_._2).foreach(println)
  }
}