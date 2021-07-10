# MultiThreaded-Array-sum

**Author:** *Yotam Dafna*

This is a Multi-Threaded Java application that calculates the sum of all Z integers, potentially using all X*Y cores in the network.
* X = Number of clients.
* Z = List of numbers in the range of 1 to Z in ascending order.
* Y = Number of cores for each client.

## Usage

#### 1. Compile & Run the ***Server***
  * Complile: ``` javac Server.java ```
  * Run: ``` java Server X Z ```

  > You can also use the Default by running: ``` java Server ``` (X=5, Z=20)
    
#### 2. Compile & Run the ***Client*** 
  * Complile: ``` javac Client.java ```
  * Run: ``` java Client X Y ``` 

  > You can also use the Default by running: ``` java Client ``` (X=5, Y=***The real number of cores available***)
  
#### <ins>Note</ins>: 
* Make sure to use the same X value both in server and Client.
