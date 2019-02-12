### 如何创建空RDD？

空RDD用处暂且不知

`sc.parallelize(List())  //或seq()`

或者，spark定义了一个 emptyRDD
```scala
/**
 * An RDD that has no partitions and no elements.
 */
private[spark] class EmptyRDD[T: ClassTag](sc: SparkContext) extends RDD[T](sc, Nil) {
 
  override def getPartitions: Array[Partition] = Array.empty
  override def compute(split: Partition, context: TaskContext): Iterator[T] = {
    throw new UnsupportedOperationException("empty RDD")
  }
  
//可以通过 sc.emptyRDD[T] 创建
```
### rdd.count == 0 和 rdd.isEmpty
我第一想到的方法要么通过count算子判断个数是否为0，要么直接通过isEmpty算子来判断是否为空

```scala
def isEmpty(): Boolean = withScope {
    partitions.length == 0 || take(1).length == 0
  }
```

isEmpty的源码也是判断分区长度或者是否有数据，如果是空RDD，isEmpty会抛异常：`Exception in thread "main" org.apache.spark.SparkDriverExecutionException: Execution error`

> emptyRDD判断isEmpty不会报这个错，不知道为什么

之后搜索了一下，看到下面这个办法 （见： https://my.oschina.net/u/2362111/blog/743754

### rdd.partitions().isEmpty()

```
 这种比较适合Dstream 进来后没有经过 类似 reduce 操作的 。
```

### rdd.rdd().dependencies().apply(0).rdd().partitions().length==0

```
 这种就可以用来作为 经过 reduce 操作的 了 
```