##### 1. Building and running the Load Generator

###### 1.1 Building and running 

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* ```mvn compile``` to build the code

* ```mvn exec:java``` to start the server (or run the Main class from your IDE)

###### 1.2. Assumed configuration

* Parquet files are stored under ```"/tmp/staging"``` (e.g. "/tmp/staging/month=2019-10/day=29/hour=3/part-00000-1d855013-f998-4cef-aca6-2c3388e2dff8.c000.snappy.parquet")
* Spark checkpoints are written to stored under ```"/tmp/checkpoint"```
* The Spark driver will run on TCP port "31000" 
* The Kafka broker is expected to be available at "localhost:9092"
* The "iot_device_events" Kafka topic must exist. It will be the source of the events to collect.
* The window is 5 minutes by default so run the three pipeline services for 10-15 minutes to
have any data to play with at all 
* A few GBs should be made available to the service, window aggregation will like it.