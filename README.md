# MultiThreaded-Array-sum

**Author:** *Yotam Dafna*

This is a Multi-Threaded Java application that calculates the sum of all Z integers, potentially using all X*Y cores in the network.
* X = Number of clients.
* Z = List of numbers in the range of 1 to Z in ascending order.
* Y = Number of cores for each client.

## Run
Open two cmd windows directed into the project's src folder
#### 1. Compile & Run the ***Server***
  * cmd window 1
    * Complile: ``` javac Server.java ```
    * Run: ``` java Server X Z ```

  > You can also use the Default by running: ``` java Server ```. <br> 
  > X=10 <br>
  > Z=100
    
#### 2. Compile & Run the ***Client*** 
 * cmd window 2
   * Complile: ``` javac Client.java ```
   * Run: ``` java Client X Y ``` 

  > You can also use the Default by running: ``` java Client ```. <br> 
  > X=10 <br> 
  > Y=***The real number of cores available*** / X <br> 
  > <br>
  >  Divide by X because we want to simulate multiple clients. <br>
  >  So if we take all the existing processors in a running computer we get that each client will try <br>
  >  to use all the existing cores, which will result in a shortage of cores for the other clients. <br>
  >  (Of course when using real external clients this is not necessary, since each client uses its own CPU.)
  
**Note:** Make sure to use the same X value both in server and Client.
