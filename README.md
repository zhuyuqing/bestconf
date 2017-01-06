
Better Configurations for Large-Scale Systems (BestConf)
====================================

Links
-----
http://prof.ict.ac.cn/~yuqing
zhuyuqing@ict.ac.cn
liujianxun@ict.ac.cn

Getting Started
---------------

1. Download the [latest release of BestConf](https://github.com/zhuyuqing/bestconf/archive/master.zip):

    ```sh
    curl -O --location https://github.com/zhuyuqing/bestconf/archive/master.zip
    unzip bestconf-master.zip
    cd bestconf-master/deploy
    ```
    
2. Set up a system for tuning. Currently, bestconf has been tested on the following
   systems: Cassandra, Hive+Hadoop, Tomcat Server, MySQL and Spark. We offer two 
   deployable examples in the project: one is called 4BI(Hive+Hadoop under Hibench
   tests) and the other is called 4tomecat(Tomcat under JMeter tests).

3. Run BestConf command. 

    On Linux:
    Update all system and deployment related scripts accordingly and move them to the
    correct path on the servers.
    Move the system-specific jar file to lib. (For example, move deploy/4BI/bestconfBI.jar
    to deploy/lib)
    ```sh
    cd deploy
    bin/start.sh
    ```
Building from source
--------------------

Import the whole Bestconf project into eclipse and enjoy!~
