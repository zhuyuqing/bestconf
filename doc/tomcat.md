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
        <td>Tomcat Server</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr>
      <tr>
        <td>JMeter</td>
        <td>CentOS</td>
        <td>16 Intel(R) Xeon(R) CPU E5620 @ 2.40GHz</td>
        <td>32G</td>
      </tr> 
    </table>
</div>

Performance Surface
-----------

We use [JMeter](http://jmeter.apache.org) that is a widely adopted benchmark tools in the workload generator for Tomcat to generate the target workload. 

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

Test Results
--------
All the test resuls of Tomcat under different workloads -> [Tomcat_Results](https://github.com/zhuyuqing/bestconf/tree/master/testResults/tomcat). <br>

Script files
--------
[Script files for Tomcat node](https://github.com/zhuyuqing/bestconf/tree/master/deploy/4Tomcat/scripts/scripts%20for%20Tomcat%20node)<br>
[Scripts files for JMeter node](https://github.com/zhuyuqing/bestconf/tree/master/deploy/4Tomcat/scripts/scripts%20for%20JMeter%20node)

Interface Impl
-------
The source files of [TomcatConfigReadin](https://github.com/zhuyuqing/bestconf/blob/master/src/tomcat/cn/ict/zyq/bestConf/cluster/InterfaceImpl/TomcatConfigReadin.java) and [TomcatConfigWrite](https://github.com/zhuyuqing/bestconf/blob/master/src/tomcat/cn/ict/zyq/bestConf/cluster/InterfaceImpl/TomcatConfigWrite.java) implement the interfaces of [ConfigReadin](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf




