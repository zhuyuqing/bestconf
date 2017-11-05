How to use BestConfig?
======================
Environment
-----------
1. Deploy environment
2. Staging environment

Precondition
------------
1. ip, user name and password of the system or cluster nodes under tuned
2. Authorization of SSH to access the system remotely
3. Download source code of [BestConfig](https://github.com/zhuyuqing/bestconf)

Deployment architecture
-----------------------
<div>
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/BestConfig.png" width = "600" height = "200" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: Deployment architecture
</td>
</tr>
</table>
</div>

How to deploy BestConfig?
-----------------------
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/Deployprocess.jpg" width = "600" height = "400" align=center />
  </div>
  <div>
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">

</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: Deployment architecture
</td>
</tr>
</table>
</div>
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

Performance Surface
-----------
We use [YCSB](https://github.com/brianfrankcooper/YCSB) that is a widely adopted benchmark tools in the workload generator for Cassandra to generate the target workload. Currently, the workload adopted in our test is workoada, and we set recorecount to 17000000 and operationcount to 720000. Figure 1 is the scatter plot of performance for Cassandra under YCSB workloada workload.
<div>
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
</div>

Test Results
--------
The test result of Cassandra under YCSB workloada [cassandraYcsba.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/cassandra/cassandraYcsba.arff).<br>

Interface Impl
-------
The source files of [CassandraConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/cassandra/cn/ict/zyq/bestConf/cluster/InterfaceImpl/CassandraConfigReadin.java) and [CassandraConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/cassandra/cn/ict/zyq/bestConf/cluster/InterfaceImpl/CassandraConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf



