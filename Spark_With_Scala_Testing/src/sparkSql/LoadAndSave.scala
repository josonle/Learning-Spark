package sparkSql

import org.apache.spark.sql.SparkSession

object LoadAndSave {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("load and save datas").getOrCreate()
    val df = spark.read.load("data/users.parquet")
    val df1 = spark.read.format("json").load("data/people.json")
//    df.printSchema()
//    df.show()
//    df1.show()
//    df1.select("name","age").write.format("csv").mode("overwrite").save("data/people")
    df1.write.option("header", true).option("sep", ";").csv("data/test")
  }
}