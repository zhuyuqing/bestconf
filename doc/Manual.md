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
--------------------------
Shell scripts
   There are 9 shell scripts in BestConfig and they are classified into two groups.<br />
   <P>One group consits of 5 shell scripts, start.sh, isStart.sh, stop.sh, isClosed.sh and terminateSystem.sh. These shell scripts are        deployed on the systems under test. </p>
   <p>Another group consits of 4 shell scripts, startTest.sh, getTestResult.sh, terminateTest.sh and isFinished.sh. These shell scripts       are deployed on the test node. </p>


