如下示例，通过最初json文件所生成的df的age列是Long类型，给它改成其他类型。当然不止如下两种方法，但我觉得这是最为简单的两种了

```scala
val spark = SparkSession.builder().master("local").appName("DataFrame API").getOrCreate()

//    读取spark项目中example中带的几个示例数据，创建DataFrame
val people = spark.read.format("json").load("data/people.json")
people.show()
people.printSchema()

val p = people.selectExpr("cast(age as string) age_toString","name")
p.printSchema()

import spark.implicits._ //导入这个为了隐式转换，或RDD转DataFrame之用
import org.apache.spark.sql.types.DataTypes
people withColumn("age", $"age".cast(DataTypes.IntegerType))    //DataTypes下有若干数据类型，记住类的位置
people.printSchema()
```



参考这个：[How to change column types in Spark SQL's DataFrame?](http://padown.com/questions/29383107/how-to-change-column-types-in-spark-sqls-dataframe) 



https://www.jianshu.com/p/0634527f3cce