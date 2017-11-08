BestConf for MySQL
======================
Experimental Settings
-----------
We executed Bestconf for the MySQL system, and we applied sysbench to test the performance of MySQL. All nodes used in our experiment are shown below.
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

Performance Surface
-----------

We use [Sysbench](https://github.com/nuodb/sysbench) that is a widely adopted benchmark tools in the workload generator for MySQL to generate the target workload. Currently, the test type in our experiment is oltp and the test mode is simple, and we set num-threads to 16, oltp-table-size to 10000000, and max-time to 300. Figure 1 is the scatter plot of performance for MySQL under OLTP simple test mode.

<table border="0" cellspacing="0" cellpadding="0" frame=void rows=none cols=none rules=none>
<tr border="0">
<td border="0">
<img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/mysql-simple.jpg" width = "800" height = "500" align=center />
</td>
</tr>
<tr border="0">
<td border="0" align=center>
Figure 1: The scatter plot of performance for MySQL under OLTP simple test mode.
</td>
</tr>
</table>

Test Results
--------
The result of MySQL under the zipfian read-write workload [MySQL_zipfian_readwrite.arff](https://github.com/zhuyuqing/bestconf/edit/master/testResults/mysql/MySQL_zipfian_readwrite.arff).<br>
The result of MySQL under OLTP simple test mode [MySQL_OLTP_simple.arff](https://github.com/zhuyuqing/bestconf/blob/master/testResults/mysql/MySQL_OLTP_simple.arff).


Interface Impl
-------
The source files of [MySQLConfigReadin](https://github.com/zhuyuqing/bestconf/blob/master/src/mysql/cn/ict/zyq/bestConf/cluster/InterfaceImpl/MySQLConfigReadin.java) and [MySQLConfigWrite](https://github.com/zhuyuqing/bestconf/blob/master/src/mysql/cn/ict/zyq/bestConf/cluster/InterfaceImpl/MySQLConfigWrite.java) implement the interfaces of [ConfigReadin](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigReadin.java) and [ConfigWrite](https://github.com/zhuyuqing/bestconf/blob/master/src/main/cn/ict/zyq/bestConf/cluster/Interface/ConfigWrite.java) respectively.  

Download 
-------

http://github.com/zhuyuqing/bestconf



