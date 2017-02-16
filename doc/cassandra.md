BestConf for Cassandra
======================
Experimental Settings
-----------
We executed Bestconf for the spark cluster with 4 nodes. The spark cluster consists of 1 master node and 3 slave nodes. All nodes used in our experiment are shown below.
<div>
    <table border="0">
      <tr>
        <th>Node</th>
        <th>OS</th>
        <th>CPU</th>
        <th>Memory</th>
      </tr>
      <tr>
        <td>Cassandra 1</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
       <td>Cassandra 2</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        
      </tr>
      <tr>
        <td>Cassandra 3</td>
        <td>CentOS</td>
         <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        
      </tr>
      <tr>
        <td>YCSB</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        
      </tr> 
    </table>
</div>

Result
-----------
We use [YCSB](https://github.com/brianfrankcooper/YCSB) that is a widely adopted benchmark tools in the workload generator for Cassandra to generate the target workload. Currently, the workload adopted in our test is workoada, and we set recorecount to 17000000 and operationcount to 720000. Figure 1 plot the highly differed performance surfaces for Cassandra workloada workload.
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/liujianxun-ict/bestconf/blob/master/pics/spark-pagerank.jpg" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The performance surface of Spark under Hibench-Pagerank workload
</td>
</tr>
</table>
Best Configuration for Spark
--------
We tuned 31 parameters for spark, the best value for each parameter is saved in [SparkPagerankConfig.txt](https://github.com/zhuyuqing/bestconf/blob/master/bestconfs/spark/bestconfForPagerank/SparkPagerankConfig.txt).<br>
The best configure files with best configurations are [spark-default.conf]() and [spark-env.sh]().
Script files
--------
[Script files for Master node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/spark-shells-master)<br>
[Scripts files for Slave node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/spark-shells-master)
Interface Impl
-------
The source files of [SparkConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigReadin.java) and [SparkConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf


