Scala里面有三种排序方法，分别是： sorted，sortBy ，sortWith 

分别介绍下他们的功能： 

（1）sorted 

对一个集合进行自然排序，通过传递隐式的Ordering 

（2）sortBy 

对一个属性或多个属性进行排序，通过它的类型。 

（3）sortWith 

基于函数的排序，通过一个comparator函数，实现自定义排序的逻辑。 

例子一：基于单集合单字段的排序 

```scala
val xs=Seq(1,5,3,4,6,2)  
println("==============sorted排序=================")  
println(xs.sorted) //升序  
println(xs.sorted.reverse) //降序  
println("==============sortBy排序=================")  
println( xs.sortBy(d=>d) ) //升序  
println( xs.sortBy(d=>d).reverse ) //降序  
println("==============sortWith排序=================")  
println( xs.sortWith(_<_) )//升序  
println( xs.sortWith(_>_) )//降序  
```

结果： 

```scala
==============sorted排序=================  
List(1, 2, 3, 4, 5, 6)  
List(6, 5, 4, 3, 2, 1)  
==============sortBy排序=================  
List(1, 2, 3, 4, 5, 6)  
List(6, 5, 4, 3, 2, 1)  
==============sortWith排序=================  
List(1, 2, 3, 4, 5, 6)  
List(6, 5, 4, 3, 2, 1)  
```



例子二：基于元组多字段的排序 

注意多字段的排序，使用sorted比较麻烦，这里给出使用sortBy和sortWith的例子 

先看基于sortBy的实现： 

```scala
 val pairs = Array(  
                      ("a", 5, 1),  
                      ("c", 3, 1),  
                      ("b", 1, 3)  
                       )  
  
   //按第三个字段升序，第一个字段降序，注意，排序的字段必须和后面的tuple对应  
   val bx= pairs.  
   sortBy(r => (r._3, r._1))( Ordering.Tuple2(Ordering.Int, Ordering.String.reverse) )  
    //打印结果          
    bx.map( println )  
```

结果： 

```
(c,3,1)  
(a,5,1)  
(b,1,3) 
```



再看基于sortWith的实现： 

```scala
 val pairs = Array(  
                      ("a", 5, 1),  
                      ("c", 3, 1),  
                      ("b", 1, 3)  
                       )  
       val b= pairs.sortWith{  
      case (a,b)=>{  
        if(a._3==b._3) {//如果第三个字段相等，就按第一个字段降序  
         a._1>b._1  
        }else{  
        a._3<b._3 //否则第三个字段降序  
        }  
      }  
    }  
    //打印结果  
    b.map(println)   
```



从上面可以看出，基于sortBy的第二种实现比较优雅，语义比较清晰，第三种灵活性更强，但代码稍加繁琐 

例子三：基于类的排序 

先看sortBy的实现方法 

排序规则：先按年龄排序，如果一样，就按照名称降序排 

```scala
 case class Person(val name:String,val age:Int)  
  
    val p1=Person("cat",23)  
    val p2=Person("dog",23)  
    val p3=Person("andy",25)  
      
    val pairs = Array(p1,p2,p3)  
  
   //先按年龄排序，如果一样，就按照名称降序排  
   val bx= pairs.sortBy(person => (person.age, person.name))( Ordering.Tuple2(Ordering.Int, Ordering.String.reverse) )  
  
    bx.map(  
      println  
    )  
```

结果： 

```
Person(dog,23)  
Person(cat,23)  
Person(andy,25) 
```



再看sortWith的实现方法： 

```scala
 case class Person(val name:String,val age:Int)  
  
    val p1=Person("cat",23)  
    val p2=Person("dog",23)  
    val p3=Person("andy",25)  
  
    val pairs = Array(p1,p2,p3)  
  
    val b=pairs.sortWith{  
      case (person1,person2)=>{  
        person1.age==person2.age match {  
          case true=> person1.name>person2.name //年龄一样，按名字降序排  
          case false=>person1.age<person2.age //否则按年龄升序排  
  
        }  
      }  
    }  
  
    b.map(  
      println  
    )  
```

结果： 

```
Person(dog,23)  
Person(cat,23)  
Person(andy,25) 
```



总结： 

本篇介绍了scala里面的三种排序函数，都有其各自的应用场景： 

sorted：适合单集合的升降序 

sortBy：适合对单个或多个属性的排序，代码量比较少，推荐使用这种 

sortWith：适合定制化场景比较高的排序规则，比较灵活，也能支持单个或多个属性的排序，但代码量稍多，内部实际是通过java里面的Comparator接口来完成排序的。 

实际应用中，可以根据具体的场景来选择合适的排序策略。 