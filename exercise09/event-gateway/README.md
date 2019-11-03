##### 1. Building and running the Event Gateway

###### 1.1 Building and running 

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* ```mvn compile``` to build the code

* ```mvn exec:java``` to start the server (or run the Server class from your IDE)

###### 1.2. Assumed configuration

* Parquet files are located under ```"/tmp/staging"``` (and partitioned on month and day for test queries to work)
* The Event Gateway runs on TCP port "8090"
* The Kafka broker is expected to be available at "localhost:9092" 
* The "iot_device_events" Kafka topic must exist. It will be the destination for events received by the Gateway.  
* The service is unlikely to be sensitive to available memory
* The number of processed events can be retrieved over HTTP for monitoring

##### 2. Test requests

* ```curl -d '{"device" : 500, "metric" :  10000, "timestamp" : 1572270214870, "value" : 0.8030342025611376}' -H "Content-Type: application/json" -X POST http://localhost:8090/event```
* ```curl -X GET http://localhost:8090/stats```