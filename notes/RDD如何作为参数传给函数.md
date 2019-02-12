```scala
//分组TopN
  def groupTopN(data: RDD[String],n:Int): RDD[(String,List[Int])] = {
    //先不考虑其他的
    //分组后类似 (t003,（19,1090,190,109）)
    val groupParis = data.map { x => 
      (x.split(" ")(0), x.split(" ")(1).toInt) 
      }.groupByKey()
    val sortedData = groupParis.map(x=>
      {
        //分组后，排序（默认升序），再倒序去前n
        val sortedLists = x._2.toList.sorted.reverse.take(n)
        (x._1,sortedLists)
      })
    sortedData
  }
```

关键是知道传入rdd的类型以及要返回值的类型，如果有编辑器就很简单，知道自己要返回什么，然后拿鼠标移到这个值上方就会显示它的类型

其次是要导入`import org.apache.spark.rdd._` 