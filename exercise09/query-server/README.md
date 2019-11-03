##### 1. Building and running the Query Server

###### 1.1 Building and running 

The Query Server is basically a poor man's Apache Livy.

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* ```mvn compile``` to build the code

* ```mvn exec:java``` to start the server (or run the Server class from your IDE)

###### 1.2. Assumed configuration

* Parquet files are located under ```"/tmp/staging"``` (and partitioned on month and day for test queries to work)
* The Query Server runs on TCP port "8080"
* The Spark driver will run on TCP port "31010" 
* At least a couple GBs should be made available to the service

##### 2. Test requests

* ```curl -d '{"sqlQuery":"select hour, device, metric, min(value) as min, max(value) as max, avg(value) as avg, count(*) as count from staged_iot_events where month=\"2019-10\" and day=28 group by device, metric, hour order by hour, device, metric"}' -H "Content-Type: application/json" -X GET http://localhost:8080/executeQuery```
