### 1. Building and running marketing analytics reports

#### 1.1 Maven
This repository is built with Maven. Please make sure your path is configured so that 
the ```mvn test``` command can be executed from the top repository directory.

For expediency, report generation is triggered by a unit test. Actual report calculations are represented by Scala 
classes that require a Spark session but are blissfully unaware of the exact way it's created. In real life it would be instantiated by something like
a main method called by an Oozie coordinator using some company-specific configuration data.   

* ```mvn test``` is the only command necessary to build and execute all the reports; the final aggregates are
printed to stdout

#### 1.2 JDK8
It's common to have conflicts between JDK11+ and Spark/Scala 2.11 . In case multiple JDKs are installed locally
the compatible one can be chosen with a command like this: 

```JAVA_HOME=-Djava.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_212.jdk/Contents/Home/ /maven361/bin/mvn test```