### 为啥要转换？

DataFrame可以借助sql语句处理，简单快捷。向hdfs的数据只能创建RDD，转化为DataFrame后就可以使用SQL进行查询

### 方法

### 方法一：基于反射推断

适合已知RDD的 **Schema** ，这个基于方法的反射可以让你的代码更简洁。其通过Case class 定义了表的 Schema。Case class 的参数名使用反射读取并且成为了列名。Case class 也可以是嵌套的或者包含像 SeqS 或者 ArrayS 这样的复杂类型。这个 RDD 能够被隐式转换成一个DataFrame 然后被注册为一个表。表可以用于后续的 SQL 语句

其次，也可以通过rdd方法从DataFrame转换成RDD

```scala
case class Person(name: String, age: Int, job: String)  //创建Person类
val spark = SparkSession.builder().master("local").appName("rdd to dataFrame").getOrCreate()

import spark.implicits._
//toDF需要导入隐式转换
val rdd = spark.sparkContext.textFile("data/people.txt").cache()
val header = rdd.first()
val rddToDF = rdd.filter(row => row != header) //去头
	.map(_.split(";"))
	.map(x => Person(x(0).toString, x(1).toInt, x(2).toString))
	.toDF()  //调用toDF方法
rddToDF.show()
rddToDF.printSchema()

val dfToRDD = rddToDF.rdd //返回RDD[row],RDD类型的row对像，类似[Jorge,30,Developer]
```

- toDF方法

toDF方法是将以通过case Class构建的类对象转为DataFrame，其可以指定列名参数colNames，否则就默认以类对象中参数名为列名。不仅如此，还可以将本地序列(seq), 数组转为DataFrame

> 要导入Spark sql implicits  ，如import spark.implicits._
>
> 如果直接用toDF()而不指定列名字，那么默认列名为"\_1", "\_2", ...
>
> ```scala
> val df = Seq(
>   (1, "First Value", java.sql.Date.valueOf("2019-01-01")),
>   (2, "Second Value", java.sql.Date.valueOf("2019-02-01"))
> ).toDF("int_column", "string_column", "date_column")
> ```

- toDS方法：转为DataSets

### 方法二：基于编程方式

通过一个允许你构造一个 **Schema** 然后把它应用到一个已存在的 **RDD** 的编程接口。然而这种方法更繁琐，当列和它们的类型知道运行时都是未知时它允许你去构造 **Dataset**

```scala
import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}
import org.apache.spark.sql.Row
//创建RDD[row]对象
val personRDD = rdd.map { line => Row(line.split(";")(0).toString, line.split(";")(1).toInt, line.split(";")(2).toString) }
//创建Schema，需要StructType/StructField
val structType = StructType(Array(
      StructField("name", StringType, true),
      StructField("age", IntegerType, true),
      StructField("job", StringType, true)))
//createDataFrame(rowRDD: RDD[Row], schema: StructType)方法
//Creates a `DataFrame` from an `RDD` containing [[Row]]s using the given schema.
val rddToDF = spark.createDataFrame(personRDD, structType)
```

createDataFrame源码如下：

```scala
 /**
   * Creates a `DataFrame` from an `RDD[Row]`.
   * User can specify whether the input rows should be converted to Catalyst rows.
   */
  private[sql] def createDataFrame(
      rowRDD: RDD[Row],
      schema: StructType,
      needsConversion: Boolean) = {
    // TODO: use MutableProjection when rowRDD is another DataFrame and the applied
    // schema differs from the existing schema on any field data type.
    val catalystRows = if (needsConversion) {
      val encoder = RowEncoder(schema)
      rowRDD.map(encoder.toRow)
    } else {
      rowRDD.map { r: Row => InternalRow.fromSeq(r.toSeq) }
    }
    internalCreateDataFrame(catalystRows.setName(rowRDD.name), schema)
  }
```

### Row对象的方法

- 直接通过下标，如row(0)

- getAs[T]：获取指定列名的列，也可用getAsInt、getAsString等

  ```scala
  //参数要么是列名，要么是列所在位置(从0开始)
  def getAs[T](i: Int): T = get(i).asInstanceOf[T]
  def getAs[T](fieldName: String): T = getAs[T](fieldIndex(fieldName))
  ```

- getValuesMap：获取指定几列的值，返回的是个map

  ```scala
  //源码
  def getValuesMap[T](fieldNames: Seq[String]): Map[String, T] = {
      fieldNames.map { name =>
        name -> getAs[T](name)
      }.toMap
    }
  //使用
  dfToRDD.map{row=>{
   val columnMap = row.getValuesMap[Any](Array("name","age","job"))    Person(columnMap("name").toString(),columnMap("age").toString.toInt,columnMap("job").toString)
  }}
  ```

- isNullAt：判断所在位置i的值是否为null

  ```scala
  def isNullAt(i: Int): Boolean = get(i) == null
  ```

- length