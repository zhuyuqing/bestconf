QuickStart
======================

Good tools make system performance tuning quicker, easier and cheaper than if everything is done manually or by experience.

Bestconfig can find better configurations for a specific large-scale system deployed for a given application workload.

* [Overview](#1)<br>
* [BestConfig Tuning -- Taking Spark as the example SUT](#2)<br>
* [Implementing your own sampling/tuing algorithms for BestConfig](#3)<br>

<h2 id='1'>Overview</h2>

<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/BestConfig.png" width = "600" height = "200" align=center />
    <p align=center>Deployment architecture </p>
</div>

Here, "deployment environment" refers to the actual running environment of your applications, while "staging environment" is some environment that is almost the same as the deployment environment but where tests are run without interfering the actual application.

<div align=center>
    <br />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/workflow.jpg" width = "640" height = "450" align=center />
</div>
<div>
<p align=center>The process of deploying BestConfig </p>
</div>

The detailed method of using BestConfig to tune practical system is as the following, which can be showed by a case of spark tuning.

<h2 id='2'>BestConfig Tuning -- Taking Spark as the example SUT</h2>

### Step 1. Deploy shells scripts for system under tune
There are 9 shell scripts in BestConfig and they are classified into two groups.<br />
<p>1. One group consists of 5 shell scripts. They are start.sh, isStart.sh, stop.sh, isClosed.sh and terminateSystem.sh and deployed on the system under tune. <br /> </p>
  <div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/shells-tune.jpg" width = "200" height = "100" align=center />
</div>
<br />
The scripts of start.sh and stop.sh deployed on worker and master node are different. <br />  
<p>(1) Shell scripts (start.sh and stop.sh) on master node</p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/start.jpg"  align=center />
</div>
<p align=center><B>start.sh(master) -- this script will start the system on the master node</B></p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/stop.jpg"  align=center />
</div>
<p align=center><B>stop.sh(master) -- this script will stop the system on the master node</B></p>
<p>(2) Shell scripts (start.sh and stop.sh) on worker node</p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/start_worker.jpg" align=center />
</div>
<p align=center><B>start.sh(worker) -- this script will start the system on the worker node</B></p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/stop_worker.jpg" align=center />
</div>
<p align=center><B>stop.sh(worker) -- this script will stop the system on the master node</B></p>
<p>(3) Identical shell scripts on master and worker node</p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isStart.jpg" align=center />
</div>
<p align=center><B>isStart.sh -- this script will return OK if the system is successfully started</B></p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/terminateSystem.jpg"  align=center />
</div>
<p align=center><B>terminateSystem.sh -- this script will terminate the system process on the server</B></p>
<div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isClosed.jpg" align=center />
</div>
<p align=center><B>isClosed.sh -- this script will return OK if the system is successfully terminated</B></p>
2. The other group consists of 4 shell scripts. They are startTest.sh, getTestResult.sh, terminateTest.sh and isFinished.sh and deployed on the test node. <br />
   <div align=center>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/shell-test.jpg"  align=center />
</div>
<br />
<div align=center>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/startTest.jpg"  align=center />
</div>
<p align=center><B>startTest.sh -- this script will start a test towards the system under tune</B></p>
<div align=center>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/isFinished.jpg"  align=center />
</div>
<p align=center><B>isFinished.sh -- this script will return OK if the test is done</B></p>
<div align=center>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/getTestResult.jpg"  align=center />
</div>
<p align=center><B>getTestResult.sh -- this script will return performance metrics regarding the test</B></p>
<div align=center>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/terminateTest.jpg"  align=center />
</div>
<p align=center><B>terminateTest.sh -- this script will terminate the testing process</B></p>

### Step 2. Implement the ConfigReadin and ConfigWrite interfaces 
As for spark tuning, we need to implement the ConfigReadin and ConfigWrite interfaces as [SparkConfigReadin](https://github.com/zhuyuqing/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigReadin.java) and [SparkConfigWrite](https://github.com/zhuyuqing/bestconf/blob/master/src/spark/cn/ict/zyq/bestConf/cluster/InterfaceImpl/SparkConfigWrite.java).
<div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/interface1.jpg"  align=center />
</div>
<br />
<div align=center>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/interface2.jpg"  align=center />
</div>
<br />
<div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/interface3.jpg"  align=center />
</div>

### Step 3. Specify the parameter set for tuning and their ranges
------------------------
<p>(1) An example of defaultConfig.yaml (specifying the parameters for tuning)  </p>
<div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/defaultConfig_yaml.jpg"  align=center />
</div>
<br />
<p>(2) An example of defaultConfig.yaml_range (the valid ranges of parameters) </p>
<div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/defaultConfig_yamlrange.jpg"  align=center />
</div>
<br />

### Step 4. Specify the resource limit and things about the tuning environment (or, sample size/round number) 
------------------------
<p>(1) bestconf.properties </p>
<div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/bestconf_propertiesNew4.jpg" width = "640" height = "90" align=center />
</div>
<br />

<p>(2) SUTconfig.properties </p>
<div >
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/SUTconfig_propertiesNew3.jpg"  align=center />
</div>
<br />

### Step 5. Start BestConfig
------------------------
Now, you can start BestConfig. BestConfig will automatically run the tuning process without any requirement for user interferences, until the tuning process ends due to resource exhaustion or unhandlable environment errors.

BestConfig will output the best configuration setting into files once the tuning is done.

<h2 id='3'>Implementing your own sampling/tuing algorithms for BestConfig</h2>

You can also choose to extend and tailor BestConfig for your specific use cases using your own sampling/tuning algorithms.

   (1) To implement your own sampling algorithms <br />
       --> Extend the abstract class of ConfigSampler <br />
 <div>
 <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/ConfigSampler1.jpg"  align=center />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/ConfigSampler2.jpg"  align=center />
 </div>
   (2) To implement your own tuning algorithms <br />
       --> Implement the interface of Optimization <br />
       <div>
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/Optimization1.jpg"  align=center />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/Optimization2.jpg"  align=center />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/Optimization3.jpg"  align=center />
    <img src="https://github.com/zhuyuqing/bestconf/blob/master/doc/pics/Optimization5.jpg"  align=center />
    </div>
