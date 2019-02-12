package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._

object TopN {
  //Top3
  def top3(data: RDD[String]): Array[String] = {
    if (data.isEmpty()) {
      println("RDD为空,返回空Array")
      Array()
    }
    if (data.count() <= 3) {
      data.collect()
    } else {
      val sortedData = data.map(x => (x, x.split(" ")(1).toInt)).sortBy(_._2, false)
      val result = sortedData.take(3).map(_._1)
      result
    }
  }
  //分组TopN
  def groupTopN(data: RDD[String],n:Int): RDD[(String,List[Int])] = {
    //先不考虑其他的
    //分组后类似 (t003,（19,1090,190,109）)
    val groupParis = data.map { x => 
      (x.split(" ")(0), x.split(" ")(1).toInt) 
      }.groupByKey()
    val sortedData = groupParis.map(x=>
      {
        //分组后，排序（默认升序），再倒序去前n
        val sortedLists = x._2.toList.sorted.reverse.take(n)
        (x._1,sortedLists)
      })
    sortedData
  }
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Top3")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("data/topN.txt").cache()

    top3(lines).foreach(println)
    
    groupTopN(lines, 3).sortBy(_._1, true).foreach(x=>{
      print(x._1+": ")
      println(x._2.mkString(","))
    })
  }
}

/*排序几种写法：
arr.sorted(Ordering.Int.reverse)
arr.sorted.reverse
arr.sortWith(_>_)
*/