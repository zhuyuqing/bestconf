BestConf for Hadoop+Hive
======================
Experimental Settings
-----------
We executed Bestconf for the Hadoop cluster with 4 nodes. The Hadoop cluster consists of 1 master node and 3 slave nodes. All nodes used in our experiment are shown below.
<div>
    <table border="0">
      <tr>
         <th>IP address</th>
        <th>OS</th>
        <th>CPU</th>
        <th>Memory</th>
        <th>Disk</th>
        <th>Node Type</th>
      </tr>
       <tr>
        <td>172.16.48.208</td>
        <td>CentOS 6.0</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        <td>50G</td>
        <td>Master</td>
      </tr>
      <tr>
        <td>172.16.48.203</td>
        <td>CentOS 5.5</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        <td>508G</td>
        <td>Slave</td>
      </tr>
      <tr>
        <td>172.16.48.206</td>
        <td>CentOS 6.0</td>
         <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        <td>808G</td>
        <td>Slave</td>
      </tr>
      <tr>
        <td>172.16.48.39</td>
        <td>CentOS 6.4</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
        <td>808G</td>
        <td>Slave</td>
      </tr> 
    </table>
</div>

Result
-----------
We use [HiBench](https://github.com/intel-hadoop/HiBench) that is a widely adopted benchmark tools in the workload generator for Spark to generate the target workload. Currently, the workload adopted in our test is Join. Figure 1 plot the highly differed performance surfaces for Hadoop+Hive Join workload.
<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/liujianxun-ict/bestconf/blob/master/pics/hadoop-join.jpg" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The performance surface of Hadoop+Hive under Hibench-Join workload
</td>
</tr>
</table>
Best Configuration for Hadoop+Hive
--------
We tuned 110 parameters for Hadoop+Hive, the best value for each parameter is saved in [mapred.bestconf](https://github.com/liujianxun-ict/bestconf/blob/master/bestconfs/hadoop/bestconfForJoin/mapred.yaml) and [yarn.bestconf](https://github.com/liujianxun-ict/bestconf/blob/master/bestconfs/hadoop/bestconfForJoin/yarn.yaml).
Script files
--------
[Script files for Master node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/hadoop-shells-master)<br>
[Scripts files for Slave node](https://github.com/liujianxun-ict/bestconf/tree/master/scripts/hadoop-shells-slave)
Interface Impl
-------
The source files of [HadoopConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/BI/cn/ict/zyq/bestConf/cluster/InterfaceImpl/HadoopConfigReadin.java) and [HadoopConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/BI/cn/ict/zyq/bestConf/cluster/InterfaceImpl/HadoopConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/liujianxun-ict/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------
https://github.com/liujianxun-ict/bestconf.git
