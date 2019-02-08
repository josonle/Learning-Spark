

### 键值对RDD上的操作 隐式转换
shuffle操作中常用针对某个key对一组数据进行操作，比如说groupByKey、reduceByKey这类PairRDDFunctions中需要启用Spark的隐式转换，scala就会自动地包装成元组 RDD。导入 `org.apache.spark.SparkContext._`即可

没啥意思，就是记着导入`import org.apache.spark.SparkContext._`就有隐式转换即可

### 常用Transformation算子

- map
- flatMap
- filter
- reduceByKey
- groupByKey


### 常用Action算子

- reduce(func)：通过函数func聚合数据集，func 输入为两个元素，返回为一个元素。多用来作运算
- count()：统计数据集中元素个数
- collect()：以一个数组的形式返回数据集的所有元素。因为是加载到内存中，要求数据集小，否则会有溢出可能
- take(n)：数据集中的前 n 个元素作为一个数组返回
- foreach(func)
- saveAsTextFile(path)：将数据集中的元素以文本文件（或文本文件集合）的形式写入本地文件系统、HDFS 或其它 Hadoop 支持的文件系统中的给定目录中。Spark 将对每个元素调用 toString 方法，将数据元素转换为文本文件中的一行记录
- countByKey():仅适用于（K,V）类型的 RDD 。返回具有每个 key 的计数的 （K , Int）对 的 map