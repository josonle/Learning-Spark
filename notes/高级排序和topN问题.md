### 按列排序和二次排序问题

搞清楚sortByKey、sortBy作用；其次是自定义Key用来排序，详情可见MapReduce中排序问题

```	scala
class MyKey(val first: Int, val second: Int) extends Ordered[MyKey] with Serializable {
    //记住这种定义写法
  def compare(that:MyKey): Int = {
  	//定义比较方法
  }
 }
```

### topN和分组topN问题

topN包含排序，分组topN只是在分组的基础上考虑排序

分组要搞清groupBy和groupByKey，前者分组后类似 (t003,（（t1003,19），(t1003,1090)）)，后者类似(t003,（19,1090）)

其次是排序，如果是简单集合内排序直接调用工具方法：sorted、sortWith、sortBy

> sorted：适合单集合的升降序 
>
> sortBy：适合对单个或多个属性的排序，代码量比较少
>
> sortWith：适合定制化场景比较高的排序规则，比较灵活，也能支持单个或多个属性的排序，但代码量稍多，内部实际是通过java里面的Comparator接口来完成排序的

```scala
scala> val arr = Array(3,4,5,1,2,6)
arr: Array[Int] = Array(3, 4, 5, 1, 2, 6)
scala> arr.sorted
res0: Array[Int] = Array(1, 2, 3, 4, 5, 6)
scala> arr.sorted.take(3)
res1: Array[Int] = Array(1, 2, 3)
scala> arr.sorted.takeRight(3)
res3: Array[Int] = Array(4, 5, 6)
scala> arr.sorted(Ordering.Int.reverse)
res4: Array[Int] = Array(6, 5, 4, 3, 2, 1)
scala> arr.sorted.reverse
res5: Array[Int] = Array(6, 5, 4, 3, 2, 1)
scala> arr.sortWith(_>_)
res6: Array[Int] = Array(6, 5, 4, 3, 2, 1)
```

考虑降序几种写法如下

```scala
arr.sorted(Ordering.Int.reverse)
arr.sorted.reverse
arr.sortWith(_>_)
```

