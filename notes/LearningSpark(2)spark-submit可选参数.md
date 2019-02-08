可以选择local模式下运行来测试程序，但要是在集群上运行还需要通过spark-submit脚本来完成。官方文档上的示例是这样写的（其中表明哪些是必要参数）：
```
./bin/spark-submit \
  --class <main-class> \
  --master <master-url> \
  --deploy-mode <deploy-mode> \
  --conf <key>=<value> \
  ... # other options
  <application-jar> \
  [application-arguments]
```

常用参数如下：
- `--master` 参数来设置 SparkContext 要连接的集群，默认不写就是local[*]【可以不用在SparkContext中写死master信息】
- `--jars` 来设置需要添加到 classpath 中的 JAR 包，有多个 JAR 包使用逗号分割符连接
- `--class` 指定程序的类入口
- `--deploy-mode` 指定部署模式，是在 worker 节点（cluster）上还是在本地作为一个外部的客户端（client）部署您的 driver（默认 : client）
- `application-jar` : 包括您的应用以及所有依赖的一个打包的 Jar 的路径。该Jar包的 URL 在您的集群上必须是全局可见的，例如，一个 hdfs:// path 或者一个 file:// path 在所有节点是可见的。
- `application-arguments` : 传递到您的 main class 的 main 方法的参数
- `driver-memory`是 driver 使用的内存，不可超过单机的最大可使用的
- `num-executors`是创建多少个 executor
- `executor-memory`是各个 executor 使用的最大内存，不可超过单机的最大可使用内存
- `executor-cores`是每个 executor 最大可并发执行的 Task 数目

```
#如下是spark on yarn模式下运行计算Pi的测试程序
# 有一点务必注意，每行最后换行时务必多敲个空格，否则解析该语句时就是和下一句相连的，不知道会爆些什么古怪的错误
[hadoop@master spark-2.4.0-bin-hadoop2.6]$ ./bin/spark-submit \
> --master yarn \
> --class org.apache.spark.examples.SparkPi \
> --deploy-mode client \
> --driver-memory 1g \
> --num-executors 2 \
> --executor-memory 2g \
> --executor-cores 2 \
> examples/jars/spark-examples_2.11-2.4.0.jar \
> 10
```
每次提交都写这么多肯定麻烦，可以写个脚本

更多内容可参考文档：[提交应用](http://cwiki.apachecn.org/pages/viewpage.action?pageId=3539265) ，[Spark-Submit 参数设置说明和考虑](https://www.alibabacloud.com/help/zh/doc-detail/28124.htm)

