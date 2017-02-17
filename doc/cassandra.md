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
We use [YCSB](https://github.com/brianfrankcooper/YCSB) that is a widely adopted benchmark tools in the workload generator for Cassandra to generate the target workload. Currently, the workload adopted in our test is workoada, and we set recorecount to 17000000 and operationcount to 720000. Figure 1 is the scatter plot of performance for Cassandra under YCSB workloada workload.
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/cassandra-scatter.jpg" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The scatter plot of performance for Cassandra under YCSB workloada workload.
</td>
</tr>
</table>
Best Configuration for Cassandra
--------
We tuned 28 parameters for cassandra, the best value for each parameter is saved in [cassandra.bestconf](https://github.com/zhuyuqing/bestconf/blob/master/bestconfs/cassandra/cassandra.bestconf).<br>
The best configure file with best configurations is [cassandra.yaml]().
Script files
--------
[Script files for Cassandra node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/spark-shells-master)<br>
[Scripts files for YCSB node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/spark-shells-master)
Interface Impl
-------
The source files of [CassandraConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigReadin.java) and [CassandraConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf


