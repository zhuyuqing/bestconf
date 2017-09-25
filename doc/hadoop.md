BestConf for Hadoop+Hive
======================
Experimental Settings
-----------
We executed Bestconf for the Hadoop cluster with 4 nodes. The Hadoop cluster consists of 1 master node and 3 slave nodes. All nodes used in our experiment are shown below.
<div>
    <table border="0">
      <tr>
        <th>Node</th>
        <th>OS</th>
        <th>CPU</th>
        <th>Memory</th>
      </tr>
       <tr>
        <td>Master</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
        <td>Slave 1</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
        <td>Slave 2</td>
        <td>CentOS</td>
         <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
        <td>Slave 3</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr> 
    </table>
</div>

Performance Surface
-----------
We use [HiBench](https://github.com/intel-hadoop/HiBench) that is a widely adopted benchmark tools in the workload generator for Spark to generate the target workload. Figure 1 plot the highly differed performance surfaces for Hadoop+Hive Join workload.
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/hadoop-join.jpg" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The performance surface of Hadoop+Hive under Hibench-Join workload
</td>
</tr>
</table>

Test Results
--------
The test results of Hadoop under Join workload [hadoopJoin.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/hive/hadoopJoin.arff). <br>
The test results of Hadoop under Pagerank workload [hadoopPageRank.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/hive/hadoopPageRank.arff). <br>
The test results of Hadoop under Join workload with 500 samples [join-trainingBestConf.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/hive/500/join-trainingBestConf0_RRS0(6).arff)
and [join-BestConfig.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/hive/500/join-trainingBestConf_RRS_0_0_150.0.arff). <br>

Interface Impl
-------
The source files of [HadoopConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/BI/cn/ict/zyq/bestConf/cluster/InterfaceImpl/HadoopConfigReadin.java) and [HadoopConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/BI/cn/ict/zyq/bestConf/cluster/InterfaceImpl/HadoopConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------
http://github.com/zhuyuqing/bestconf
