##### 0. Quick start

* Start ZK
* Start Kafka
* Start three pipeline services (ETL Pipeline, Event Gateway, Load Generator) in the given order by following their README.md files
* The streaming window is 5 minutes by default so run the three pipeline services for 10-15 minutes to
have any data to play with at all
* .. after that stop Load Generator, Event Gateway, ETL Pipeline, Kafka, ZK if you don't need more data
* Start the Query Server, run a test query (adjust the time filters to overlap with your data generation time)

##### 1. Architecture

The system consists of four services:
* Load Generator - simulates a number of IoT devices sending periodic measurement events. The event format is ```|deviceId:int|metricId:int|timestamp:long|value:double|```.
The generator sends events ny making an HTTP POST call to a configured URL. An event is sent in the request body as a JSON snippet.

* Event Gateway - a RESTful service that accepts a POST request, parses the JSON body, sends the event to a Kafka topic. 

* ETL Pipeline - a service wrapper around a continuosuly running Spark Streaming query. The query reads event messages from the Kafka topic,
batches them into windows, and periodically flushes the results to Parquet files partitioned on time (currently on month/day/hour).

* Query Server - a RESTful service that owns a SparkSQL driver. It receives a SQL query (and assumes a particular output 
shape currently for simplicity sake), executes it with Spark, collects the results back to the driver, and sends the results
back to the client. A typical query is expected to be aggregation-heavy and so produce a limited number of result rows (remember,
Spark was not designed for sending a lot of data back to the driver). 

The first three services comprise the write path that produces files that are the persistent storage of the system. The last one
is the service that can query the persistent storage. The write path can work when the query server is down. The query 
server will work without the write path services running (as long as there are some historic data in the persistent 
storage files).

Write path:
```
IoT Load Generator -(JSON)-> Event Gateway -(Kafka record)-> ETL Pipeline -(Parquet file)-> staging location
```

Read path:
```
User -(SQL)-> Query Server -(SparkSQL)-> Parquet files
```

##### 2. Technology choice and required ETL pipeline infrastructure

###### 2.0 akka-http 

Both RESTful services are based on it. It's the least painful way to implementing and testing RESTful backend services 
I know. Especially for reactive services that rely on CompletableFutures or support HTTP streaming. The DSL is pretty self-explanatory.
Nothing special needs to be known to run it. 

###### 2.1 Kafka

Kafka is such a core component of any realtime data pipeline that Spark and Flink are mostly useless without it. 
One problem with it is that oine needs to install it. And ZK too.

###### 2.2 Parquet files

It has been a default choice for a few years whenever Spark is used. My own experience confirms. The question of
compaction is important but ignored here due to limited time.

###### 2.3. Spark / Structured Streaming

I have much more experience with Spark than Flink so in the interest of saving time I went with the former. The consensus
seems to be that for windows of a few minutes Spark is good enough.


A typical private cloud Hadoop or AWS EMR deployment provide directly or help a lot with running Spark/Kafka/ZK/HDFS FS.
This prototype would be easy to migrate to such infrastructure with cosmetic changes such as file paths.

##### 3. Installation

The good news is that it's fast and easy to install and run both Kafka and ZK for development. Note that I could not make 
```io.github.embeddedkafka:embedded-kafka_2.11:2.3.1``` work with my Spark but theoretically it's an option.

###### 3.1 Zookeeper
* download [v3.4.14](https://www.apache.org/dist/zookeeper/zookeeper-3.4.14/zookeeper-3.4.14.tar.gz)
* unpack to, say, "/zookeeper3414"
* ```cp /zookeeper3414/conf/zoo_sample.cfg /zookeeper3414/conf/zoo.cfg```
* ```cd /zookeeper3414```
* execute ```./bin/zkServer.sh start``` (and later ```./bin/zkServer.sh stop```)

###### 3.2 Kafka
* download [v2.0.0](https://archive.apache.org/dist/kafka/2.0.0/kafka_2.11-2.0.0.tgz)
* unpack to, say, "/kafka200"
* ```bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic iot_device_events --partitions 1 --replication-factor 1```
* edit "config/server.properties" to add ```listeners = PLAINTEXT://localhost:9092```
* make sure our new topic is actually available with ```bin/kafka-topics.sh --list --zookeeper localhost:2181```
* execute ```./bin/kafka-server-start.sh config/server.properties &``` (and later ```./bin/kafka-server-stop.sh config/server.properties```) 

###### 3.3 Maven

It is so traditional that it is simply assumed to be installed anywhere Java development happens.


##### 4. Alternatives to consider

At first glance, the problem of collecting IoT data (and especially storing it and serving queries at different time granularity)
is solved by the Apache Druid project. But Druid is less well known and also requires running a number of different services to
support the realtime use case. 

Apache Flink is mostly interchangeable with Spark Structured Streaming in respect of windowing logic. The former has a more 
credible streaming story. The latter is better known as has more logical API.

Apache Livy is a reasonable Spark job server with a RESTful API. 

Parquet files are stored locally. It would be straightforward to use the HDFS file system API instead. That would enable
later replacement of the actual HDFS with S3 by simply using different JARs. 

##### 5. Missing but necessary

I have a lot to say about systems like this one. I have built a couple myself in the last eight years :) 

* Fine-tuning the number of files/partitions per hour

* Compaction/repartitioning of staging files into fewer larger ones in a more permanent location

* A more generic approach to executing queries. At least be able to serialize to JSON any query output. DSL parsing and 
and schema transformations are fun but time consuming though.  