## Standalone模式下内核架构分析

### Application 和 spark-submit

Application是编写的spark应用程序，spark-submit是提交应用程序给spark集群运行的脚本

### Driver

spark-submit在哪里提交，那台机器就会启动Drive进程，用以执行应用程序。

首先就是创建SparkContext

### SparkContext作用

sc初始化时负责创建DAGScheduler、TaskScheduler、Spark UI【先不考虑这个】

### TaskScheduler功能

sc构造的TaskScheduler有自己后台的进程，负责连接spark集群的Master节点并注册Application

#### Master

Master在接受Application注册请求后，会通过自己的资源调度算法在集群的Worker节点上启动一系列Executor进程（Master通知Worker启动Executor）

#### Worker和Executor

Worker节点启动Executor进程，Executor进程会创建线程池，并且启动后会再次向Driver的TaskScheduler反向注册，告知有哪些Executor可用。

当然不止这些作用，先在这里谈一下task任务的执行。

Executor每接收到一个task后，会调用TaskRunner对task封装（所谓封装就是对代码、算子、函数等拷贝、反序列化），然后再从线程池中取线程执行该task

#### 聊聊Task

说的task这里就顺便提下task。task分两类：ShuffleMapTask、ResultTask，每个task对应的是RDD的一个Partition分区。下面会提到DAGScheduler会把提交的job作业分为多个Stage（依据宽依赖、窄依赖），一个Stage对应一个TaskSet（系列task），而ResultTask正好对应最后一个Stage，之前的Stage都是ShuffleMapTask

### DAGScheduler

SparkContext另一重要作用就是构造DAGScheduler。程序中每遇到Action算子就会提交一个Job（想懒加载），DAGScheduler会把提交的job作业分为多个Stage。Stage就是一个TaskSet，会提交给TaskScheduler，因为之前Executor已经向TaskScheduler注册了哪些资源可用，所有TaskScheduler会分配TaskSet里的task给Executor执行（涉及task分配算法）。如何执行？向上看Executor部分

## Spark on Yarn模式下的不同