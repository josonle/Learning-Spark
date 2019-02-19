package sparkSql

import org.apache.spark.sql.SparkSession

object RDDtoDataFrame {
  case class Person(name: String, age: Int, job: String)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("rdd to dataFrame").getOrCreate()

    import spark.implicits._
    //toDF需要导入隐式转换
    val rdd = spark.sparkContext.textFile("data/people.txt").cache()
    val header = rdd.first()
    val rddToDF = rdd.filter(row => row != header) //去头
      .map(_.split(";"))
      .map(x => Person(x(0).toString, x(1).toInt, x(2).toString))
      .toDF("name","age","job")
    rddToDF.show()
    rddToDF.printSchema()

    val dfToRDD = rddToDF.rdd//返回RDD[row],RDD类型的row对象
    dfToRDD.foreach(println)
    
    dfToRDD.map(row =>Person(row.getAs[String]("name"), row.getAs[Int]("age"), row(2).toString()))  //也可用row.getAs[String](2)
      .foreach(p => println(p.name + ":" + p.age + ":"+ p.job))
    dfToRDD.map{row=>{
    val columnMap = row.getValuesMap[Any](Array("name","age","job"))
      Person(columnMap("name").toString(),columnMap("age").toString.toInt,columnMap("job").toString)
    }}.foreach(p => println(p.name + ":" + p.age + ":"+ p.job))
//    isNullAt方法
  }
}