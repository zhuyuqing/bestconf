
Better Configurations for Large-Scale Systems (BestConf)
====================================

Bestconf can find better configurations for a specific large-scale system under a given application workload.

Given the diverse application workloads, a large-scale system with hundreds of configuration parameters, such as Spark, 
Hadoop, Cassandra, MySQL and Tomcat, does not necessarily offer the best performance under their default configurations.

Currently, Bestconf has been tested on the following systems. It has also been applied to the Huawei Cloud+ applications. 

-------->>>[Spark: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/spark.md)

-------->>>[Cassandra: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/cassandra.md)

-------->>>[Hive+Hadoop: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/hadoop.md)

-------->>>[Tomcat Server: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/tomcat.md)

-------->>>[MySQL: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/mysql.md)


Links
-----

If you have any question, please contact:

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
    
2. Set up a system for tuning. In the project, we offer deployable examples for 6 systems, including Spark, 
   Hive+Hadoop, Cassandra, MySQL, and Tomcat. We also specify the workload generators to be used for tuning
   the systems.
   
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


Tuning results
--------------------
We provide the tuning results on our cluster under the testResults folder for a quick reference.


Acknowledgements
--------------------

We thank Huawei for supporting this work. This work is also supported in part by the State Key Development Program for Basic Research of China (Grant No. 2014CB340402) and the National Natural Science Foundation of China (Grant No. 61303054).
