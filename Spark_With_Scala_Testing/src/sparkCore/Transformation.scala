package sparkCore

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkConf
import org.spark_project.dmg.pmml.True

object Transformation {
  def main(args: Array[String]): Unit = {
    System.out.println("Start***********")
    /*getData("data/hello.txt")
    getData("", Array(1,2,3,4,5))*/

    /*map_flatMap_result()*/
    /*distinct_result()*/

   /*  filter_result()
    groupByKey_result()
    reduceByKey_result()
    sortByKey_result()*/
    join_result()
  }

  //### 数据来源

  def getData(path: String, arr: Array[Int] = Array()) {
    // path：file:// 或 hdfs://master:9000/ [指定端口9000]
    val conf = new SparkConf().setMaster("local").setAppName("getData")
    val sc = new SparkContext(conf)

    if (!arr.isEmpty) {
      sc.parallelize(arr).map(_ + 1).foreach(println)
    } else {
      sc.textFile(path, 1).foreach(println)
    }
    sc.stop()
  }

  def map_flatMap_result() {
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
  //去重
  def distinct_result() {
    val conf = new SparkConf().setMaster("local").setAppName("去重")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("data/hello.txt", 1)
    rdd.flatMap(_.split(",")).distinct().foreach(println)
  }
  //保留偶数
  def filter_result() {
    val conf = new SparkConf().setMaster("local").setAppName("过滤偶数")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(Array(1, 2, 3, 4, 5, 6), 1)
    rdd.filter(_ % 2 == 0).foreach(println)
  }
  //每个班级的学生
  def groupByKey_result() {
    val conf = new SparkConf().setMaster("local").setAppName("分组")
    val sc = new SparkContext(conf)
    val classmates = Array(Tuple2("class1", "Lee"), Tuple2("class2", "Liu"),
      Tuple2("class2", "Ma"), Tuple2("class3", "Wang"),
      Tuple2("class1", "Zhao"), Tuple2("class1", "Zhang"),
      Tuple2("class1", "Mao"), Tuple2("class4", "Hao"),
      Tuple2("class3", "Zha"), Tuple2("class2", "Zhao"))

    val students = sc.parallelize(classmates)
    students.groupByKey().foreach(x ⇒ {
      println(x._1 + " :")
      x._2.foreach(println)
      println("***************")
    })
  }
  //每个班级总分
  def reduceByKey_result() {
    val conf = new SparkConf().setMaster("local").setAppName("聚合")
    val sc = new SparkContext(conf)
    val classmates = Array(Tuple2("class1", 90), Tuple2("class2", 85),
      Tuple2("class2", 60), Tuple2("class3", 95),
      Tuple2("class1", 70), Tuple2("class4", 100),
      Tuple2("class3", 80), Tuple2("class2", 65))

    val students = sc.parallelize(classmates)
    val scores = students.reduceByKey(_ + _).foreach(x ⇒ {
      println(x._1 + " :" + x._2)
      println("****************")
    })
  }
  //按分数排序
  def sortByKey_result() {
    val conf = new SparkConf()
        .setAppName("sortByKey")  
        .setMaster("local")  
    val sc = new SparkContext(conf)
    
    val scoreList = Array(Tuple2(65, "leo"), Tuple2(50, "tom"), 
        Tuple2(100, "marry"), Tuple2(85, "jack"))  
    val scores = sc.parallelize(scoreList, 1)  
    val sortedScores = scores.sortByKey(false)
    
    sortedScores.foreach(studentScore => println(studentScore._1 + ": " + studentScore._2))  
    sc.stop()
    println("排序Over×××××××××")
  }
  
  def join_result(){
    val conf = new SparkConf().setMaster("local").setAppName("聚合")
    val sc = new SparkContext(conf)
    val classmates = Array(Tuple2("class1", 90), Tuple2("class2", 85),
      Tuple2("class2", 60), Tuple2("class3", 95),
      Tuple2("class1", 70), Tuple2("class4", 100),
      Tuple2("class3", 80), Tuple2("class2", 65))

    val students = sc.parallelize(classmates)
    val allScores = students.reduceByKey(_ + _)
    val nums = students.countByKey().toArray
    allScores.join(sc.parallelize(nums, 1)).sortByKey(true).foreach(x⇒{
    println(x._1+" :")
    println("All_scores: "+x._2._1+",Nums: "+x._2._2)
    println("Avg_scores: "+x._2._1.toDouble/x._2._2)
    })
    }
}
