package sparkSql

import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

object RDDtoDataFrame2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("rdd to dataFrame").getOrCreate()

    import spark.implicits._
    //toDF需要导入隐式转换
    val rdd = spark.sparkContext.textFile("data/people.txt").cache()
    val header = rdd.first()
    val personRDD = rdd.filter(row => row != header) //去头
      .map { line => Row(line.split(";")(0).toString, line.split(";")(1).toInt, line.split(";")(2).toString) }
    
    val structType = StructType(Array(
      StructField("name", StringType, true),
      StructField("age", IntegerType, true),
      StructField("job", StringType, true)))
      
    val rddToDF = spark.createDataFrame(personRDD, structType)
    rddToDF.createOrReplaceTempView("people")
 
    val results = spark.sql("SELECT name FROM people")
 
    results.map(attributes => "Name: " + attributes(0)).show()
  }
}