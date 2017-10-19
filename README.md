
Better Configurations for Large-Scale Systems (BestConfig)
====================================

Bestconfig can find better configurations for a specific large-scale system under a given application workload.

Given the diverse application workloads, a large-scale system with hundreds of configuration parameters, such as Spark, 
Hadoop, Cassandra, MySQL and Tomcat, does not necessarily offer the best performance under their default configurations.

Currently, Bestconfig has been tested on the following systems. It has also been applied to the Huawei Cloud+ applications. 

-------->>>[Spark: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/spark.md)

-------->>>[Cassandra: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/cassandra.md)

-------->>>[Hive+Hadoop: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/hadoop.md)

-------->>>[Tomcat Server: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/tomcat.md)

-------->>>[MySQL: results](https://github.com/zhuyuqing/bestconf/blob/master/doc/mysql.md)

Getting Started
---------------

1. Download the [latest release of BestConfig](https://github.com/zhuyuqing/bestconf/archive/master.zip):

    ```sh
    curl -O --location https://github.com/zhuyuqing/bestconf/archive/master.zip
    unzip bestconf-master.zip
    cd bestconf-master/deploy
    ```
    
2. Set up a system for tuning. In the project, we offer deployable examples for 6 systems, including Spark, 
   Hive+Hadoop, Cassandra, MySQL, and Tomcat. We also specify the workload generators to be used for tuning
   the systems.
   
3. Run BestConfig. 

    On Linux:
    Update all system and deployment related scripts accordingly and move them to the
    correct path on the servers.
    Move the system-specific jar file to lib. (For example, move deploy/4BI/bestconfBI.jar
    to deploy/lib)
    ```sh
    cd deploy
    bin/start.sh
    ```

Slides (in Chinese) on how to use BestConfig can also be downloaded following this [link](https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnx6aHV5dXFpbmd8Z3g6NmI1ZTYxMDc2N2FiOWMwNQ).

Building from source
--------------------

Import the whole Bestconfig project into eclipse and enjoy!~


Examples of tuning results
--------------------
We provide the tuning results on our cluster under the testResults folder for a quick reference.

Related Publications
--------------------
[1] Yuqing ZHU, Jianxun Liu, Mengying Guo, Yungang Bao, Wenlong Ma, Zhuoyue Liu, Kunpeng Song, Yingchun Yang. BestConfig: Tapping the Performance Potential of Systems via Automatic Configuration Tuning. Proceedings of the ACM Symposium on Cloud Computing 2017 (SoCC’17) ([pdf](https://arxiv.org/abs/1710.03439), [slides](https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnx6aHV5dXFpbmd8Z3g6N2MwMjQxOWJjMzE5ZmMzMw))

[2] Yuqing ZHU, Jianxun Liu, Mengying Guo, Yungang Bao. ACTS in Need: Automatic Configuration Tuning with Scalability. Proceedings of the 8th ACM SIGOPS Asia-Pacific Workshop on Systems (APSys’17) ([pdf](https://arxiv.org/abs/1708.01349))

Acknowledgements
--------------------

We thank Huawei for supporting this work. This work is also supported in part by the State Key Development Program for Basic Research of China (Grant No. 2014CB340402) and the National Natural Science Foundation of China (Grant No. 61303054).


Notes
-----

If you have any question, please contact us at:

   zhuyuqing@ict.ac.cn
   
   liujianxun@ict.ac.cn
