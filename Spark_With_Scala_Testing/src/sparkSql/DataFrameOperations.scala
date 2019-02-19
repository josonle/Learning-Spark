package sparkSql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql
import org.apache.spark.sql.functions
import org.apache.spark.sql.types.DataTypes

object DataFrameOperations {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("DataFrame API").getOrCreate()

    //    读取spark项目中example中带的几个示例数据，创建DataFrame
    val people = spark.read.format("json").load("data/people.json")
//    people.show()
//    people.printSchema()
    
//    val p = people.selectExpr("cast(age as string) age_toString","name")
//    p.printSchema()

    import spark.implicits._ //导入这个为了隐式转换，或RDD转DataFrame之用
//    更改column的类型，也可以通过上面selectExpr实现
    people withColumn("age", $"age".cast(sql.types.StringType))    //DataTypes下有若干数据类型，记住类的位置
    people.printSchema()
//    people.select(functions.col("age").cast(DataTypes.DoubleType)).show()
    
    //    people.select($"name", $"age" + 1).show()
//    people.select(people("age")+1, people.col("name")).show()
    //    people.select("name").s1how()    //select($"name")等效，后者好处看上面
    //    people.filter($"name".contains("ust")).show()
    //    people.filter($"name".like("%ust%")).show
//    people.filter($"name".rlike(".*ust.*")).show()
    println("Test Filter*****************")
    //    people.filter(people("name") contains ("ust")).show()
    //    people.filter(people("name") like ("%ust%")).show()
    //    people.filter(people("name") rlike (".*?ust.*?")).show()

    println("Filter中如何取反*****************")
//    people.filter(!(people("name") contains ("ust"))).select("name", "age").show()
    people.groupBy("age").count().show()
//    people.createOrReplaceTempView("sqlDF")
//    spark.sql("select * from sqlDF where name not like '%ust%' ").show()
    
  }
}