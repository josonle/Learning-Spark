## 数据源自并行集合
调用 SparkContext 的 parallelize 方法，在一个已经存在的 Scala 集合上创建一个 Seq 对象

## 外部数据源
Spark支持任何 `Hadoop InputFormat` 格式的输入，如本地文件、HDFS上的文件、Hive表、HBase上的数据、Amazon S3、Hypertable等，以上都可以用来创建RDD。

常用函数是 `sc.textFile()` ,参数是Path和最小分区数[可选]。Path是文件的 URI 地址，该地址可以是本地路径，或者 `hdfs://`、`s3n://` 等 URL 地址。其次，使用本地文件时，如果在集群上运行要确保worker节点也能访问到文件