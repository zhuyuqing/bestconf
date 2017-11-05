How to use BestConfig?
======================
Environment
-----------
1. Deploy environment
2. Staging environment

Precondition
------------
1. ip, user name and password of the system or cluster nodes under test
2. Authorization of SSH to access the system remotely
3. Download source code of [BestConfig](https://github.com/zhuyuqing/bestconf)

Deployment architecture
-----------------------

<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/BestConfig.png" width = "600" height = "200" align=center />
    <p align=center> Figure 1: Deployment architecture </p>
</div>

How to deploy BestConfig?
-----------------------

<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/deploy_process.jpg" width = "600" height = "340" align=center />
</div>
<div>
<p align=center>Figure 2: The process of deploying BestConfig </p>
</div>

The following part illustrates the detailed method of using BestConfig to tune practical systems by a case of spark tuning.

Spark tuning by BestConfig
==========================
Shell scripts
-------------
There are 9 shell scripts in BestConfig and they are classified into two groups.<br />
  (1) One group consits of 5 shell scripts, start.sh, isStart.sh, stop.sh, isClosed.sh and terminateSystem.sh, which are deployed on the       systems under test. <br />
  <div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/shells-tune.jpg" width = "200" height = "100" align=center />
</div>
<br />
The scripts of start.sh and stop.sh deployed on worker and Master node are different. <br />  
1. Shell scripts (start.sh and stop.sh) on Master node
<br />
<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/start.jpg" width = "270" height = "130" align=center />
</div>
<p align=center>start.sh</p>
<br />
<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/stop.jpg" width = "310" height = "110" align=center />
</div>
<p align=center>stop.sh</p>
<br />
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/start_worker.jpg" width = "200" height = "100" align=center />
</div>
<p align=center>start.sh</p>
<br />
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/stop_worker.jpg" width = "200" height = "100" align=center />
</div>
<br />
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isStart.jpg" width = "200" height = "100" align=center />
</div>
<br />
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/terminateSystem.jpg" width = "200" height = "100" align=center />
</div>
<br />
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isClosed.jpg" width = "200" height = "100" align=center />
</div>
<br />
  (2) Another group consits of 4 shell scripts, startTest.sh, getTestResult.sh, terminateTest.sh and isFinished.sh, which are deployed         on the test node. <br />
   <div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/shell-test.jpg" width = "200" height = "100" align=center />
</div>
<br />
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/startTest.jpg" width = "200" height = "100" align=center />
</div>
<br />
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isFinished.jpg" width = "200" height = "100" align=center />
</div>
<br />
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/getTestResult.jpg" width = "200" height = "100" align=center />
</div>
<br />
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/terminateTest.jpg" width = "200" height = "100" align=center />
</div>
<br />


