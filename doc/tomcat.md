BestConf for Tomcat Server
======================
Experimental Settings
-----------
We executed Bestconf for the Tomcat server, and we applied sysbench to test the performance of Tomcat server. All nodes used in our experiment are shown below.
<div>
    <table border="0">
      <tr>
        <th>Node</th>
        <th>OS</th>
        <th>CPU</th>
        <th>Memory</th>
      </tr>
      <tr>
        <td>MySQL</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
        <td>Sysbench</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr> 
    </table>
</div>

Result
-----------

We use [JMeter](http://jmeter.apache.org) that is a widely adopted benchmark tools in the workload generator for MySQL to generate the target workload. Currently, the test type in our experiment is oltp and the test mode is simple, and we set num-threads to 16, oltp-table-size to 10000000, and max-time to 300. Figure 1 is the scatter plot of performance for MySQL under OLTP simple test mode.

<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/tomcat.png" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The performance surface of Tomcat under a page navigation workload.
</td>
</tr>
</table>
Best Configuration for Tomcat Server
--------
We tuned 13 parameters for Tomcat Server, the best value for each parameter is saved in [tomcat.bestconf](https://github.com/zhuyuqing/bestconf/blob/master/bestconfs/mysql/mysql.bestconf).<br>
The best configuration file with best configurations is [my.cnf](https://github.com/zhuyuqing/bestconf/blob/master/bestconfs/mysql/my.cnf).
Script files
--------
[Script files for Tomcat node](https://github.com/zhuyuqing/bestconf/tree/master/deploy/4Tomcat/scripts)<br>
[Scripts files for JMeter node](https://github.com/zhuyuqing/bestconf/tree/master/deploy/4Tomcat/scripts)
Interface Impl
-------
The source files of [TomcatConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/tomcat/cn/ict/zyq/bestConf/cluster/InterfaceImpl/TomcatConfigReadin.java) and [TomcatConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/tomcat/cn/ict/zyq/bestConf/cluster/InterfaceImpl/TomcatConfigWrite.java) implement the interfaces of [ConfigReadin.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite.java](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf




