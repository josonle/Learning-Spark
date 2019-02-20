# Learning-Spark
学习Spark的代码，关于Spark Core、Spark SQL、Spark Streaming、Spark MLLib

## 说明

### 开发环境

- 基于Deepin Linux 15.9版本
- 基于Hadoop2.6、Spark2.4、Scala2.11、java8等

> 系列环境搭建相关文章，见下方
> - [【向Linux迁移记录】Deepin下java、大数据开发环境配置【一】](https://blog.csdn.net/lzw2016/article/details/86566873)
> - [【向Linux迁移记录】Deepin下Python开发环境搭建](https://blog.csdn.net/lzw2016/article/details/86567436)
> - [【向Linux迁移记录】Deepin Linux下快速Hadoop完全分布式集群搭建](https://blog.csdn.net/lzw2016/article/details/86618345)
> - [【向Linux迁移记录】基于Hadoop集群的Hive安装与配置详解](https://blog.csdn.net/lzw2016/article/details/86631115)
> - [【向Linux迁移记录】Deepin Linux下Spark本地模式及基于Yarn的分布式集群环境搭建](https://blog.csdn.net/lzw2016/article/details/86718403)
> - [eclipse安装Scala IDE插件及An internal error occurred during: "Computing additional info"报错解决](https://blog.csdn.net/lzw2016/article/details/86717728) 
> - [Deepin Linux 安装启动scala报错 java.lang.NumberFormatException: For input string: "0x100" 解决](https://blog.csdn.net/lzw2016/article/details/86618570) 
> - 更多内容见：【https://blog.csdn.net/lzw2016/ 】【https://github.com/josonle/Coding-Now 】

### 文件说明

- [Spark_With_Scala_Testing](https://github.com/josonle/Learning-Spark/tree/master/Spark_With_Scala_Testing) 存放平时练习代码
- notes存放笔记
  - [LearningSpark(1)数据来源.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(1)%E6%95%B0%E6%8D%AE%E6%9D%A5%E6%BA%90.md)
  - [LearningSpark(2)spark-submit可选参数.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(2)spark-submit%E5%8F%AF%E9%80%89%E5%8F%82%E6%95%B0.md)
  - [LearningSpark(3)RDD操作.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(3)RDD%E6%93%8D%E4%BD%9C.md)
  - [LearningSpark(4)Spark持久化操作](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(4)Spark%E6%8C%81%E4%B9%85%E5%8C%96%E6%93%8D%E4%BD%9C.md)
  - [LearningSpark(5)Spark共享变量.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(5)Spark%E5%85%B1%E4%BA%AB%E5%8F%98%E9%87%8F.md)
  - [LearningSpark(6)Spark内核架构剖析.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(6)Spark%E5%86%85%E6%A0%B8%E6%9E%B6%E6%9E%84%E5%89%96%E6%9E%90.md)
  - [LearningSpark(7)SparkSQL之DataFrame学习（含Row）.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(7)SparkSQL%E4%B9%8BDataFrame%E5%AD%A6%E4%B9%A0.md)
  - [LearningSpark(8)RDD如何转化为DataFrame](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(8)RDD%E5%A6%82%E4%BD%95%E8%BD%AC%E5%8C%96%E4%B8%BADataFrame.md)
  - [LearningSpark(9)SparkSQL数据来源](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(9)SparkSQL%E6%95%B0%E6%8D%AE%E6%9D%A5%E6%BA%90.md)
  - [RDD如何作为参数传给函数.md](https://github.com/josonle/Learning-Spark/blob/master/notes/RDD%E5%A6%82%E4%BD%95%E4%BD%9C%E4%B8%BA%E5%8F%82%E6%95%B0%E4%BC%A0%E7%BB%99%E5%87%BD%E6%95%B0.md)
  - [判断RDD是否为空](https://github.com/josonle/Learning-Spark/blob/master/notes/%E5%88%A4%E6%96%ADRDD%E6%98%AF%E5%90%A6%E4%B8%BA%E7%A9%BA)
  - [高级排序和topN问题.md](https://github.com/josonle/Learning-Spark/blob/master/notes/%E9%AB%98%E7%BA%A7%E6%8E%92%E5%BA%8F%E5%92%8CtopN%E9%97%AE%E9%A2%98.md)
  - [Spark1.x和2.x如何读取和写入csv文件](https://blog.csdn.net/lzw2016/article/details/85562172)
  - [Spark DataFrame如何更改列column的类型.md](https://github.com/josonle/Learning-Spark/blob/master/notes/Spark%20DataFrame%E5%A6%82%E4%BD%95%E6%9B%B4%E6%94%B9%E5%88%97column%E7%9A%84%E7%B1%BB%E5%9E%8B.md)
  - [使用JDBC将DataFrame写入mysql.md](https://github.com/josonle/Learning-Spark/blob/master/notes/%E4%BD%BF%E7%94%A8JDBC%E5%B0%86DataFrame%E5%86%99%E5%85%A5mysql.md)
  - Scala 语法点
    - [Scala排序函数使用.md](https://github.com/josonle/Learning-Spark/blob/master/notes/Scala%E6%8E%92%E5%BA%8F%E5%87%BD%E6%95%B0%E4%BD%BF%E7%94%A8.md)
  - [报错和问题归纳.md](https://github.com/josonle/Learning-Spark/blob/master/notes/%E6%8A%A5%E9%94%99%E5%92%8C%E9%97%AE%E9%A2%98%E5%BD%92%E7%BA%B3.md)

待续
