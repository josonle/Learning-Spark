DataFrame说白了就是RDD+Schema（元数据信息），spark1.3之前还叫SchemaRDD

Spark-SQL 可以以 RDD 对象、Parquet 文件、JSON 文件、Hive 表，
以及通过JDBC连接到其他关系型数据库表作为数据源来生成DataFrame对象