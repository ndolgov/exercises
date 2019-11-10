##### 1. Building and running

This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn``` command can be executed from the top repository directory. 

* Create the test dataset in postgres following [the instructions](https://github.com/OSBI/foodmart-data)
* ```mvn scala:run``` to execute test queries and print out their plans and execution results
* ```mvn test``` to execute unit tests; the tests will run the same queries and quietly assert the results are correct

##### 2. Calcite schema configuration

The [RelRunners](https://github.com/apache/calcite/blob/master/core/src/main/java/org/apache/calcite/tools/RelRunners.java) 
class fails to execute custom queries. You can see your logical plan tree rendered on screen without doing anything else 
with ```RelOptUtil.toString()```. But if you want to see the results of running your RelNode tree against postgres 
you must explicitly register your schema the way RelNodeCompiler does it.

##### 3. FAQ

The following two JVM arguments can help with debugging weird Calcite errors 
* ```-Dcalcite.debug=true```
* ```-Djava.util.logging.config.file=logging.properties```

##### 4. References

* https://github.com/twilmes/incubator-calcite/blob/master/doc/HOWTO.md
* https://github.com/michaelmior/calcite-notebooks/blob/master/query-optimization.ipynb
* https://www.slideshare.net/JordanHalterman/introduction-to-apache-calcite
* "http://mail-archives.apache.org/mod_mbox/calcite-dev/201608.mbox/%3cDC4D16BC-6607-4A29-B9F9-7DDD67D3EEAD@gmail.com%3e