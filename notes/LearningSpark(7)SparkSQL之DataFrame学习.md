DataFrame说白了就是RDD+Schema（元数据信息），spark1.3之前还叫SchemaRDD

Spark-SQL 可以以 RDD 对象、Parquet 文件、JSON 文件、Hive 表，
以及通过JDBC连接到其他关系型数据库表作为数据源来生成DataFrame对象



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

