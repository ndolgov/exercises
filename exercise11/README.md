##### 1. Building and running the charging session server

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* ```mvn compile``` to build the code

* ```mvn exec:java``` to start the server

##### 2. Test requests

* ```curl -X POST http://localhost:8080/account``` - create a new account

* ```curl -X PUT http://localhost:8080/account/b0696859-4578-45ae-9153-eb8a10ce2706/100``` - deposit 100 cents to account b0696859-4578-45ae-9153-eb8a10ce2706

* ```curl -X GET http://localhost:8080/account/b0696859-4578-45ae-9153-eb8a10ce2706``` - get account balance

* ```curl -X POST http://localhost:8080/transfer/b0696859-4578-45ae-9153-eb8a10ce2706/b0696859-4578-45ae-9153-eb8a10ce2707/300``` - transfer 300 cents from account ..2706 to account ..2707