### 使用前准备

- hive-site.xml复制到$SPARK_HOME/conf目录下
- hive连接mysql的jar包(mysql-connector-java-8.0.13.jar)也要复制到$SPARK_HOME/jars目录下
    - 或者在spark-submit脚本中通过--jars指明该jar包位置
    - 或者在spark-env.xml中把该jar包位置加入Class Path `export SPARK_CLASSPATH=$SPARK_CLASSPATH:/jar包位置`
      > 我测试不起作用

### spark.sql.warehouse.dir参数

入门文档讲解spark sql如何作用在hive上时，[提到了下面这个例子](http://spark.apachecn.org/#/docs/7?id=hive-%E8%A1%A8)，其次有个配置spark.sql.warehouse.dir
```scala
val spark = SparkSession
  .builder()
  .appName("Spark Hive Example")
  .config("spark.sql.warehouse.dir", warehouseLocation)
  .enableHiveSupport()
  .getOrCreate()
```
该参数指明的是hive数据仓库位置
> spark 1.x 版本使用的参数是"hive.metastore.warehouse" ，在spark 2.0.0 后，该参数已经不再生效，用户应使用 spark.sql.warehouse.dir进行代替

比如说我hive仓库是配置在hdfs上的，所以spark.sql.warehouse.dir=hdfs://master:9000/hive/warehouse
```
<property>
	<name>hive.metastore.warehouse.dir</name>
	<value>/hive/warehouse</value>
</property>
```

有一点是以上要达到期望效果前提是hive要部署好，没部署好的话，会在spark会默认覆盖hive的配置项。因为spark下也有spark-hive模快的，此时会使用内置hive。比如你可以尝试命令行下使用spark-shell使用hive仓库，你会发现当前目录下会产生metastore_db(元数据信息)

另外是该参数可以从hive-site.xml中获取，所以可以不写。但是在eclipse、idea中编程如果不把hive-site.xml放在resource文件夹下无法读取，所以还是配置该参数为好

### 报错解决
1. 报无法加载mysql驱动jar包，导致一些列错误无法访问数据库等。解决是把该jar包如上文所述放在$SPARK_HOME/jars目录下
>  org.datanucleus.exceptions.NucleusException: Attempt to invoke the "BONECP" plugin to create a ConnectionPool gave an error : The specified datastore driver ("com.mysql.jdbc.Driver") was not found in the CLASSPATH. Please check your CLASSPATH specification, and the name of the driver.

2. 报Table 'hive.PARTITIONS' doesn't exist
> ERROR Datastore:115 - Error thrown executing ALTER TABLE `PARTITIONS` ADD COLUMN `TBL_ID` BIGINT NULL : Table 'hive.PARTITIONS' doesn't exist
java.sql.SQLSyntaxErrorException: Table 'hive.PARTITIONS' doesn't exist

这个不知道怎么说，程序可以运行不过会抛该错误和warn
在stackoverflow中搜到了，配置一个参数config("spark.sql.hive.verifyPartitionPath", "false")
见：https://stackoverflow.com/questions/47933705/spark-sql-fails-if-there-is-no-specified-partition-path-available

参考：

- [Spark 2.2.1 + Hive 案例之不使用现有的Hive环境；使用现有的Hive数据仓库；UDF自定义函数](https://blog.csdn.net/duan_zhihua/article/details/79335625)
- [Spark的spark.sql.warehouse.dir相关](https://blog.csdn.net/u013560925/article/details/79854072)
- https://www.jianshu.com/p/60e7e16fb3ce
