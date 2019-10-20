##### 1. Building and running the charging session server

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* ```mvn compile``` to build the code

* ```mvn exec:java``` to start the server

##### 2. Test requests

* ```curl -d '{"stationId":"ABC-12345"}' -H "Content-Type: application/json" -X POST http://localhost:8080/chargingSessions```

* ```curl -X PUT http://localhost:8080/chargingSessions/b0696859-4578-45ae-9153-eb8a10ce2706```

* ```curl -X GET http://localhost:8080/chargingSessions```

* ```curl -X GET http://localhost:8080/chargingSessions/summary```