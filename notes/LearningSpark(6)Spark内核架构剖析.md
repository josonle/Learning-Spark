## Standalone模式下内核架构分析

### Application 和 spark-submit

Application是编写的spark应用程序，spark-submit是提交应用程序给spark集群运行的脚本

### Driver

spark-submit在哪里提交，那台机器就会启动Drive进程，用以执行应用程序。

像我们编写程序一样首先做的就是创建SparkContext

### SparkContext作用

sc负责上下文环境（下文sc也指代SparkContext），sc初始化时负责创建DAGScheduler、TaskScheduler、Spark UI【先不考虑这个】

主要就是DAGScheduler、TaskScheduler，下面一一提到

### TaskScheduler功能

TaskScheduler任务调度器，从字面可知和任务执行有关

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

## Spark on Yarn模式下的不同之处

之前提到过on Yarn有yarn-client和yarn-cluster两种模式，在spark-submit脚本中通过`--master`、`--deploy-mode`来区分以哪种方式运行 【具体可见：[LearningSpark(2)spark-submit可选参数.md](https://github.com/josonle/Learning-Spark/blob/master/notes/LearningSpark(2)spark-submit%E5%8F%AF%E9%80%89%E5%8F%82%E6%95%B0.md)】

其中，官方文档中所提及`--deploy-mode` 指定部署模式，是在 worker 节点（cluster）上还是在本地作为一个外部的客户端（client）部署您的 driver（默认 : client），这和接下来所提及的内容有关

因为是运行在Yarn集群上，所有没有什么Master、Worker节点，取而代之是ResourceManager、NodeManager（下文会以RM、NM代替）

### yarn-cluster运行模式

首先spark-submit提交Application后会向RM发送请求，请求启动ApplicationMaster（同standalone模式下的Master，但同时该节点也会运行Drive进程【这里和yarn-client有区别】）。RM就会分配container在某个NM上启动ApplicationMaster

要执行task就得有Executor，所以ApplicationMaster要向RM申请container来启动Executor。RM分配一些container（就是一些NM节点）给ApplicationMaster用来启动Executor，ApplicationMaster就会连接这些NM（这里NM就如同Worker）。NM启动Executor后向ApplicationMaster注册

### yarn-client运行模式

如上所提的，这种模式的不同在于Driver是部署在本地提交的那台机器上的。过程大致如yarn-cluster，不同在于ApplicationMaster实际上是ExecutorLauncher，而申请到的NodeManager所启动的Executor是要向本地的Driver注册的，而不是向ApplicationMaster注册