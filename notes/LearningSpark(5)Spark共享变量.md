## 共享变量

Spark又一重要特性————共享变量

worker节点中每个Executor会有多个task任务，而算子调用函数要使用外部变量时，默认会每个task拷贝一份变量。这就导致如果该变量很大时网络传输、占用的内存空间也会很大，所以就有了 **共享变量**。每个节点拷贝一份该变量，节点上task共享这份变量

spark提过两种共享变量：Broadcast Variable（广播变量），Accumulator（累加变量）

## Broadcast Variable 和 Accumulator

广播变量**只可读**不可修改，所以其用处是优化性能，减少网络传输以及内存消耗；累加变量能让多个task共同操作该变量，**起到累加作用**，通常用来实现计数器（counter）和求和（sum）功能

### 广播变量

广播变量通过调用SparkContext的broadcast()方法，来对某个变量创建一个Broadcast[T]对象，Scala中通过value属性访问该变量，Java中通过value()方法访问该变量

通过广播方式进行传播的变量，会经过序列化，然后在被任务使用时再进行反序列化

```scala
scala> val brodcast = sc.broadcast(1)
brodcast: org.apache.spark.broadcast.Broadcast[Int] = Broadcast(12)

scala> brodcast
res4: org.apache.spark.broadcast.Broadcast[Int] = Broadcast(12)

scala> brodcast.value
res5: Int = 1
```
```scala
val factor = 3
val factorBroadcast = sc.broadcast(factor)

val arr = Array(1, 2, 3, 4, 5)
val rdd = sc.parallelize(arr)
val multipleRdd = rdd.map(num => num * factorBroadcast.value())
```

### 如何更新广播变量

通过unpersist()将老的广播变量删除，然后重新广播一遍新的广播变量

```scala
import java.io.{ ObjectInputStream, ObjectOutputStream }
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.StreamingContext
import scala.reflect.ClassTag

/* wrapper lets us update brodcast variables within DStreams' foreachRDD
 without running into serialization issues */
case class BroadcastWrapper[T: ClassTag](
 @transient private val ssc: StreamingContext,
  @transient private val _v: T) {

  @transient private var v = ssc.sparkContext.broadcast(_v)

  def update(newValue: T, blocking: Boolean = false): Unit = {
    // 删除RDD是否需要锁定
    v.unpersist(blocking)
    v = ssc.sparkContext.broadcast(newValue)
  }

  def value: T = v.value

  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeObject(v)
  }

  private def readObject(in: ObjectInputStream): Unit = {
    v = in.readObject().asInstanceOf[Broadcast[T]]
  }
}
```

参考：

[How can I update a broadcast variable in spark streaming?](https://stackoverflow.com/questions/33372264/how-can-i-update-a-broadcast-variable-in-spark-streaming)

[Spark踩坑记——共享变量](https://www.cnblogs.com/xlturing/p/6652945.html)

***

### 累加变量

累加变量有几点注意：

- 集群上只有Driver程序可以读取Accumulator的值，task只对该变量调用add方法进行累加操作；
- 累加器的更新只发生在 **action** 操作中，不会改变懒加载，**Spark** 保证每个任务只更新累加器一次，比如，重启任务不会更新值。在 transformations（转换）中， 用户需要注意的是，如果 task（任务）或 job stages（阶段）重新执行，每个任务的更新操作可能会执行多次
- Spark原生支持数值类型的累加器longAccumulator（Long类型）、doubleAccumulator（Double类型），我们也可以自己添加支持的类型，在2.0.0之前的版本中，通过继承AccumulatorParam来实现，而2.0.0之后的版本需要继承AccumulatorV2来实现自定义类型的累加器

```scala
scala> val accum1 = sc.longAccumulator("what?")		//what?是name属性，可直接sc.accumulator(0)
accum1: org.apache.spark.util.LongAccumulator = LongAccumulator(id: 379, name: Some(what?), value: 0)
scala> accum1.value
res30: Long = 0
scala> sc.parallelize(Array(1,2,3,4,5)).foreach(accum1.add(_))
scala> accum1
res31: org.apache.spark.util.LongAccumulator = LongAccumulator(id: 379, name: Some(what?), value: 15)
scala> accum1.value
res32: Long = 15
```
```scala
scala> accum1.value
res35: Long = 15
scala> val rdd = sc.parallelize(Array(1,2,3,4,5))
rdd: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[32] at parallelize at <console>:24
scala> val tmp = rdd.map(x=>(x,accum1.add(x)))
tmp: org.apache.spark.rdd.RDD[(Int, Unit)] = MapPartitionsRDD[33] at map at <console>:27
scala> accum1.value
res36: Long = 15
scala> tmp.map(x=>(x,accum1.add(x._1))).collect		//遇到action操作才执行累加操作
res37: Array[((Int, Unit), Unit)] = Array(((1,()),()), ((2,()),()), ((3,()),()), ((4,()),()), ((5,()),()))
scala> accum1.value		//结果是两次累加操作的结果
res38: Long = 45
```

```scala
scala> val data = sc.parallelize(1 to 10)
data: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[36] at parallelize at <console>:24
scala> accum.value
res54: Int = 0
scala> val newData = data.map{x => {
     |   if(x%2 == 0){
     |     accum += 1
     |       0
     |     }else 1
     | }}
newData: org.apache.spark.rdd.RDD[Int] = MapPartitionsRDD[37] at map at <console>:27

scala> newData.count
res55: Long = 10
scala> accum.value		//newData执行Action后累加操作执行一次，结果为5
res56: Int = 5
scala> newData.collect		//newData再次执行Action后累加操作又执行一次
res57: Array[Int] = Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0)
scala> accum.value
res58: Int = 10
```

如上代码所示，解释了前面所提的几点。因为newData是data经map而来的，而map函数中有累加操作，所以会有两次累加操作。解决办法如下：

1. 要么一次Action操作就求出累加变量结果
2. 在Action操作前进行持久化，避免了RDD的重复计算导致多次累加 【**推荐**】

- longAccumulator、doubleAccumulator方法

```
add方法：赋值操作
value方法：获取累加器中的值
merge方法：该方法特别重要，一定要写对，这个方法是各个task的累加器进行合并的方法（下面介绍执行流程中将要用到）
iszero方法：判断是否为初始值
reset方法：重置累加器中的值
copy方法：拷贝累加器
name方法：累加器名称
```

> 累加器执行流程： 首先有几个task，spark engine就调用copy方法拷贝几个累加器（不注册的），然后在各个task中进行累加（注意在此过程中，被最初注册的累加器的值是不变的），执行最后将调用merge方法和各个task的结果累计器进行合并（此时被注册的累加器是初始值）
> 见 http://www.ccblog.cn/103.htm

### 自定义累加变量

**注意：使用时需要register注册一下**

1. 类继承extends AccumulatorV2[String, String]，第一个为输入类型，第二个为输出类型
2. 要override以下六个方法

```
isZero: 当AccumulatorV2中存在类似数据不存在这种问题时，是否结束程序。 
copy: 拷贝一个新的AccumulatorV2 
reset: 重置AccumulatorV2中的数据 
add: 操作数据累加方法实现 
merge: 合并数据 
value: AccumulatorV2对外访问的数据结果
```

详情参考这个：https://blog.csdn.net/leen0304/article/details/78866353

