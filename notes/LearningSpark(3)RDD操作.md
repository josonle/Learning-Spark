

### 键值对RDD上的操作 隐式转换
shuffle操作中常用针对某个key对一组数据进行操作，比如说groupByKey、reduceByKey这类PairRDDFunctions中需要启用Spark的隐式转换，scala就会自动地包装成元组 RDD。导入 `org.apache.spark.SparkContext._`即可

没啥意思，就是记着导入`import org.apache.spark.SparkContext._`就有隐式转换即可

### 常用Transformation算子

- map：对RDD中的每个数据通过函数映射成一个新的数。**输入与输出分区一对一**

- flatMap：同上，不过会把输出分区合并成一个

  - **注意**：flatMap会把String扁平化为字符数组，但不会把字符串数组Array[String]扁平化

  map和flatMap还有个区别，如下代码    [更多map、flatmap区别见这里](http://www.brunton-spall.co.uk/post/2011/12/02/map-map-and-flatmap-in-scala/) 

  ```scala
  scala> val list = List（1,2,3,4,5）
  list：List [Int] = List（1,2,3,4,5）
  
  scala> def g（v：Int）= List（v-1，v，v + 1）
  g：（v：Int）List [Int]
  
  scala> list.map（x => g（x））
  res0：List [List [Int]] = List（List（0,1,2），List（1,2,3），List（2,3,4），List（3,4,5），List（List） 4,5,6））
  
  scala> list.flatMap（x => g（x））
  res1：List [Int] = List（0,1,2,1,2,3,2,3,4,3,4,5,4,5,6）
  ```

- filter：通过func对RDD中数据进行过滤，func返回true保留，反之滤除

- distinct：对RDD中数据去重

- reduceByKey(func)：对RDD中的每个Key对应的Value进行reduce聚合操作

- groupByKey()：根据key进行group分组，每个key对应一个`Iterable<value>`

- sortByKey([ascending])：对RDD中的每个Key进行排序，ascending布尔值是否升序 【默认升序】

- join(otherDataset)：对两个包含<key,value>对的RDD进行join操作，每个key join上的pair，都会传入自定义函数进行处理


### 常用Action算子

- reduce(func)：通过函数func聚合数据集，func 输入为两个元素，返回为一个元素。多用来作运算
- count()：统计数据集中元素个数
- collect()：以一个数组的形式返回数据集的所有元素。因为是加载到内存中，要求数据集小，否则会有溢出可能
- take(n)：数据集中的前 n 个元素作为一个数组返回，并非并行执行，而是由驱动程序计算所有的元素
- foreach(func)：调用func来遍历RDD中每个元素
- saveAsTextFile(path)：将数据集中的元素以文本文件（或文本文件集合）的形式写入本地文件系统、HDFS 或其它 Hadoop 支持的文件系统中的给定目录中。Spark 将对每个元素调用 toString 方法，将数据元素转换为文本文件中的一行记录
- countByKey():仅适用于（K,V）类型的 RDD 。返回具有每个 key 的计数的 （K , Int）对 的 Map