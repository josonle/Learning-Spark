DataFrame说白了就是RDD+Schema（元数据信息），spark1.3之前还叫SchemaRDD，以列的形式组织的分布式的数据集合

Spark-SQL 可以以 RDD 对象、Parquet 文件、JSON 文件、Hive 表，
以及通过JDBC连接到其他关系型数据库表作为数据源来生成DataFrame对象

### 如何创建Spark SQL的入口

同Spark Core要先创建SparkContext对象一样，在spark1.6后使用SQLContext对象，或者是它的子类的对象，比如HiveContext的对象，而spark2.2提供了SparkSession对象代替了上述两种方式作为程序入口。如下所示

```scala
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

val conf = new SparkConf().setMaster("local").setAppName("test sqlContext")
val sc = new SparkContext(conf)
val sqlContext = new SQLContext(sc)
//导入隐式转换import sqlContext.implicits._
```

> SparkSession中封装了spark.sparkContext和spark.sqlContext ，所以可直接通过spark.sparkContext创建sc，进而创建rdd（这里spark是SparkSession对象）

```scala
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder().master("local").appName("DataFrame API").getOrCreate()
//或者 SparkSession.builder().config(conf=SparkConf()).getOrCreate()
//导入隐式转换import spark.implicits._
```



> 了解下HiveContext：
>
> 除了基本的SQLContext以外，还可以使用它的子类——HiveContext。HiveContext的功能除了包含SQLContext提供的所有功能之外，还包括了额外的专门针对Hive的一些功能。这些额外功能包括：使用HiveQL语法来编写和执行SQL，使用Hive中的UDF函数，从Hive表中读取数据。
>
> 要使用HiveContext，就必须预先安装好Hive，SQLContext支持的数据源，HiveContext也同样支持——而不只是支持Hive。对于Spark 1.3.x以上的版本，都推荐使用HiveContext，因为其功能更加丰富和完善。
>
> Spark SQL还支持用spark.sql.dialect参数设置SQL的方言。使用SQLContext的setConf()即可进行设置。对于SQLContext，它只支持“sql”一种方言。对于HiveContext，它默认的方言是“hiveql”。

### DataFrame API

#### show

```scala
  def show(numRows: Int): Unit = show(numRows, truncate = true)

  /**
   * Displays the top 20 rows of Dataset in a tabular form. Strings more than 20 characters
   * will be truncated, and all cells will be aligned right.
   *
   * @group action
   * @since 1.6.0
   */
  def show(): Unit = show(20)
```

看源码中所示，numRows=20默认显示前20行，truncate表示一个字段是否最多显示 20 个字符，默认为 true



#### select 选择

```scala
people.select($"name", $"age" + 1).show()
people.select("name").show()    //select($"name")等效，后者好处看上面
```

>使用$"age"提取 age 列数据作比较时，用到了隐式转换，故需在程序中引入相应包
>
>`import spark.implicits._`
>
>注意这里所谓的spark是SparkSession或sqlContext的实例对象



>另外也可以直接使用people("age")+1，或者people.col("age")+1，以上几种方法都是选择某列

#### selectExpr

可以对指定字段进行特殊处理的选择

```
people.selectExpr("cast(age as string) age_toString","name")
```



#### filter 过滤

同sql中的where，无非数字和字符串的比较，注意以下

```scala
//spark2.x写法
people.filter($"age" > 21).show()
people.filter($"name".contains("ust")).show()
people.filter($"name".like("%ust%")).show
people.filter($"name".rlike(".*?ust.*?")).show()
// 同上，spark1.6写法
//people.filter(people("name") contains("ust")).show()
//people.filter(people("name") like("%ust%")).show()
//people.filter(people("name") rlike(".*?ust.*?")).show()
```



- contains：包含某个子串substring
- like：同SQL语句中的like，要借助通配符`_`、`%`
- rlike：这个是java中的正则匹配，用法和正则pattern一样
- 可以用and、or
- 取反not contains：如`people.filter(!(people("name") contains ("ust")))`

#### groupBy 聚合

#### count 计数

#### createOrReplaceTempView 注册为临时SQL表

注册为临时SQL表，便于直接使用sql语言编程

```scala
people.createOrReplaceTempView("sqlDF")
spark.sql("select * from sqlDF where name not like '%ust%' ").show()		//spark是创建的SparkSession对象
```



#### createGlobalTempView 注册为全局临时SQL表

上面创建的TempView是与SparkSession相关的，session结束就会销毁，想跨多个Session共享的话需要使用Global Temporary View。spark examples中给出如下参考示例

```scala
// Global temporary view is tied to a system preserved database `global_temp`
spark.sql("SELECT * FROM global_temp.people").show()
// +----+-------+
// | age|   name|
// +----+-------+
// |null|Michael|
// |  30|   Andy|
// |  19| Justin|
// +----+-------+

// Global temporary view is cross-session
spark.newSession().sql("SELECT * FROM global_temp.people").show()
// +----+-------+
// | age|   name|
// +----+-------+
// |null|Michael|
// |  30|   Andy|
// |  19| Justin|
// +----+-------+
// $example off:global_temp_view$
```

