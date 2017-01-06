package cn.ict.zyq.bestConf.cluster.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import cn.ict.zyq.bestConf.bestConf.ClusterManager;
import cn.ict.zyq.bestConf.cluster.Interface.Performance;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigWrite;
import cn.ict.zyq.bestConf.cluster.Interface.SystemOperation;
import cn.ict.zyq.bestConf.cluster.Interface.Test;
import cn.ict.zyq.bestConf.cluster.InterfaceImpl.SUTPerformance;
import cn.ict.zyq.bestConf.cluster.InterfaceImpl.SUTSystemOperation;
import cn.ict.zyq.bestConf.cluster.InterfaceImpl.SUTTest;
import cn.ict.zyq.bestConf.cluster.Utils.PropertiesUtil;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class AutoTestAdjust implements ClusterManager{
	private Properties pps;
	private String systemName;
	private String shellsPath;
	private String configFilePath;
	private String[] servers;
	private int numServers;
	private String[] users;
	private String[] passwords;
	private String serverAll;
	private String configFileStyle;
	private String interfacePath;
	private String localDataPath;
	private List<SystemOperation> cluster;
	private Test sutTest;
	private List<ConfigReadin> ConfR_cluster;
	private List<ConfigWrite> ConfW_cluster;
	private Performance systemPerformance;
	private boolean isInterrupt = false;
	private int maxRoundConnection;
	private int sutStartTimeout;
	private long testDurationTimeOutInSec;
	
	private int performanceType;
	private String targetTestServer;
	private String targetTestUsername;
	private String targetTestPassword;
	private String targetTestPath;
	private int targetTestErrorNum;
	private String perfsfilepath;
	private String configfilename;
	private String remoteConfigFilePath;
	private double performance;
	private int sshReconnectWatingtime;

	public AutoTestAdjust(String configFilePath){
		this.configFilePath = configFilePath;
		cluster = new ArrayList<SystemOperation>();
		//cluster_statusFileOps = new ArrayList<StartStatusFileOperation>();
		ConfR_cluster = new ArrayList<ConfigReadin>();
		ConfW_cluster = new ArrayList<ConfigWrite>();
		this.getProperties();
		
		perfsfilepath = localDataPath + "/perfsfile";
		
		for(int i = 0; i < numServers; i++){
			try {
				ConfigReadin readin = (ConfigReadin)Class.forName(interfacePath + "." + systemName + "ConfigReadin").newInstance();
			    readin.initial(servers[i], users[i], passwords[i], localDataPath, remoteConfigFilePath);
				ConfR_cluster.add(readin);
				
				ConfigWrite write = (ConfigWrite)Class.forName(interfacePath + "." + systemName + "ConfigWrite").newInstance();
			    write.initial(servers[i], users[i], passwords[i], localDataPath, remoteConfigFilePath);
				ConfW_cluster.add(write);
				
				SystemOperation so = new SUTSystemOperation();//Class.forName(interfacePath + "." + systemName + "SystemOperation");
				//SystemOperation so = (SystemOperation)SystemOperation.newInstance();
				
				so.initial(servers[i], users[i], passwords[i], shellsPath, sutStartTimeout, maxRoundConnection, sshReconnectWatingtime);
				cluster.add(so);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
						
		sutTest = new SUTTest(testDurationTimeOutInSec);//(HibenchTest)Class.forName(interfacePath + "." + systemName + "HibenchTest").newInstance();
		sutTest.initial(targetTestServer, targetTestUsername, targetTestPassword, targetTestPath, maxRoundConnection, sshReconnectWatingtime);
		systemPerformance = new SUTPerformance();//(Performance)Class.forName(interfacePath + "." + systemName + "Performance").newInstance();
		
	};	
	
	private void getProperties(){
		try{
			pps = PropertiesUtil.GetAllProperties(configFilePath);
		}catch(IOException e){
			e.printStackTrace();
		}
		sshReconnectWatingtime = Integer.parseInt(pps.getProperty("sshReconnectWatingtime"));
		maxRoundConnection = Integer.parseInt(pps.getProperty("maxRoundConnection"));
		targetTestPath = pps.getProperty("targetTestPath");
		remoteConfigFilePath = pps.getProperty("remoteConfigFilePath");
		
		performanceType = Integer.parseInt(pps.getProperty("performanceType"));
		performanceType=performanceType>2?2:performanceType;
		
		targetTestErrorNum = Integer.valueOf(pps.getProperty("targetTestErrorNum"));
		targetTestServer = pps.getProperty("targetTestServer");
		targetTestUsername = pps.getProperty("targetTestUsername");
		targetTestPassword = pps.getProperty("targetTestPassword");
		systemName = pps.getProperty("systemName");
		numServers = Integer.valueOf(pps.getProperty("numServers"));
		servers = new String[numServers];
        users = new String[numServers];
        passwords = new String[numServers];
		for(int i = 0; i < numServers; i++){
			servers[i] = pps.getProperty("server" + i);
			users[i] = pps.getProperty("username" + i);
			passwords[i] = pps.getProperty("password" + i);
		}
		shellsPath = pps.getProperty("shellsPath");
		serverAll = "";
		interfacePath = pps.getProperty("interfacePath");
		for(int i = 0; i < numServers; i++){
			if(i != numServers - 1){
				serverAll += this.servers[i];
				serverAll += ",";
			}
			else
				serverAll += servers[i];
		}
		localDataPath = pps.getProperty("localDataPath");
		sutStartTimeout = Integer.parseInt(pps.getProperty("sutStartTimeoutInSec"));
		testDurationTimeOutInSec = Long.parseLong(pps.getProperty("testDurationTimeoutInSec"));
	}
	
	private static int maxTry = 3;
    public boolean startTest(HashMap hmTarget, int num, boolean isInterrupt) {
		boolean flag = false;
		int crntTry = 0;
		while (!flag && crntTry < maxTry) {
			crntTry++;
			
			for (int i = 0; i < numServers; i++) {
				cluster.get(i).stopSystem();
			}
			int timesofstop = 0;
			int maxtimesofstop = 30;
			boolean boolofstop;
			boolean[] flagclosedofstop = new boolean[numServers];
			while(true){
				try {
					timesofstop++;
					Thread.sleep(1000);
					if(timesofstop > maxtimesofstop)
						break;
					boolofstop = true;
					for (int i = 0; i < numServers; i++) {
						flagclosedofstop[i] = false;
					}
					for (int i = 0; i < numServers; i++) {
						flagclosedofstop[i] = cluster.get(i).isClosed();
					}
					for (int i = 0; i < numServers; i++) {
						if (flagclosedofstop[i])
							continue;
						else
							boolofstop = false;
					}
					if(boolofstop){
						System.out.println("集群已经被终止了！");
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// ------------修改集群配置文件-------------
			for (int i = 0; i < numServers; i++) {
				HashMap hm = ConfR_cluster.get(i).modifyConfigFile(hmTarget);
				ConfW_cluster.get(i).writetoConfigfile(hm);
				ConfW_cluster.get(i).uploadConfigFile();
			}
			// ------------启动集群---------------------
			for (int i = 0; i < numServers; i++) {
				cluster.get(i).start();
			}
			flag = true;//the default value before judging
			// ------------判断集群是否启动成功----------
			boolean[] flags = new boolean[numServers];
			for (int i = 0; i < numServers; i++) {
				flags[i] = false;
			}
			//flag = cluster.get(0).isHadoopStarted();
			for (int i = 0; i < numServers; i++) {
				flags[i] = cluster.get(i).isStarted();
				//cluster.get(i).killTail();
			}
			for (int i = 0; i < numServers; i++) {
				if (flags[i])
					continue;
				else
					flag = false;
			}
			if(flag){
				// ---------集群启动成功，开始测试-----------------
				sutTest.terminateTest();
				sutTest.startTest();
				performance = sutTest.getResultofTest(num, isInterrupt);
                System.out.println("performance is : " + performance);
				//----------关闭集群---------------
				for(int i = 0; i < numServers; i++)
					cluster.get(i).stopSystem();
				
				int times = 0;
				int maxtimes = 30;
				boolean bool;
				boolean[] flagclosed = new boolean[numServers];
				while(true){
					try {
						times++;
						Thread.sleep(1000);
						if(times > maxtimes)
							break;
						bool = true;
						for (int i = 0; i < numServers; i++) {
							flagclosed[i] = false;
						}
						for (int i = 0; i < numServers; i++) {
							flagclosed[i] = cluster.get(i).isClosed();
						}
						for (int i = 0; i < numServers; i++) {
							if (flagclosed[i])
								continue;
							else
								bool = false;
						}
						if(bool){
							System.out.println("集群已经关闭了！");
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				systemPerformance.initial(performance,1.0/(Math.abs(performance)+1));
				return true;
			}	
		} 
		return false;
	}
    private double[] getPerf(String filePath){
    	double[] result = new double[2];
		File res = new File(filePath);
		try {
			int tot=0;
			BufferedReader reader = new BufferedReader(new FileReader(res));
			String readline = null;
			while ((readline = reader.readLine()) != null) {
				result[tot++] = Double.parseDouble(readline);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
    public static String getMD5(Instance ins){
    	StringBuffer name = new StringBuffer("");
    	for(int i = 0; i < ins.numAttributes() - 2; i++){
    		name.append(Math.round(ins.value(ins.attribute(i)))+",");
    	}
    	return getMD5(name.toString());
    }
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            System.out.println("出现错误！");
        }
		return null;
    }
    private void writePerfstoFile(Instance ins){
    	File perfFolder = new File(perfsfilepath);
    	if(!perfFolder.exists())
    		perfFolder.mkdirs();
    	
    	File file = new File(perfsfilepath + "/" + getMD5(ins));
    	BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				//writer.write(ins.value(ins.attribute(ins.numAttributes()-2))+"\n");
				writer.write(ins.value(ins.attribute(ins.numAttributes()-1))+"\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
	public Instances runExp(Instances samplePoints, String perfAttName){
		Instances retVal = null;
		/*if(performanceType>2 && samplePoints.attribute("combinedPerf_zyq")==null){
			Attribute combinedPerf = new Attribute("combinedPerf_zyq");
			samplePoints.insertAttributeAt(combinedPerf, samplePoints.numAttributes());
		}*/
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		int pos = samplePoints.numInstances();
		int count = 0;
		for(int i = 0; i < pos; i++){
			Instance ins = samplePoints.get(i);
			//System.err.println(ins.toString());
			HashMap hm = new HashMap();
			int tot = 0;
			for(int j = 0; j < ins.numAttributes(); j++){
				hm.put(ins.attribute(j).name(), ins.value(ins.attribute(j)));
			}
			
			boolean testRet;
			  if(Double.isNaN(ins.value(ins.attribute(ins.numAttributes()-1)))){	
				  testRet = this.startTest(hm, i, isInterrupt);
				double y = 0;
				if(!testRet){//the setting does not work, we skip it
					y = -1;
					count++;
					if(count>=targetTestErrorNum){
						System.out.println("出现异常，需要恢复系统启动状态！");
						System.exit(1);
					}
					/*if(performanceType>2)
						ins.setValue(samplePoints.numAttributes()-2,-1);*/
					
				
				}else{
					/*if(performanceType>2){
						ins.setValue(samplePoints.numAttributes()-2,getPerformanceByType(performanceType));
						y = getPerformanceByType(2);//the throughput
					}else*/
					y = getPerformanceByType(performanceType);
					count = 0;
				}
			
			ins.setValue(samplePoints.numAttributes()-1, y);
			writePerfstoFile(ins);
		}else{
			continue;
		}
			//		将配置修改于Yaml文件中，启动实验，并获各结果
		}
		retVal = samplePoints;
		//必须设定好y值所在的属性
		retVal.setClassIndex(retVal.numAttributes()-1);
		
		return retVal;
	}
	public void shutdown(){
		for(int i = 0; i < numServers; i++){
			cluster.get(i).shutdown();
		}
	}
	
	public double getPerformanceByType(int type){
		double performance = 0.0;
		switch(type){
			case 1:{   //indicates latency
				performance = systemPerformance.getPerformanceOfLatency();
				break;
			}
			case 2:{   //indicates throughput
				performance = systemPerformance.getPerformanceOfThroughput();
				System.out.println("throughput is " + performance);
				break;
			}
			/*case 3:
				performance = systemPerformance.getPerformanceOfThroughput()*1000/systemPerformance.getPerformanceOfLatency();
				break;*/
			default:
				break;
		}
		return performance;
	}

	/**
	 * set the bestConf to cluster and get the running performance
	 * @param attributeToVal
	 * @return
	 */
	public double setOptimal(Map<Attribute,Double> attributeToVal){
		HashMap hm = new HashMap();
		for(Attribute key : attributeToVal.keySet()){
			Double value = attributeToVal.get(key);
			hm.put(key.name(), value);
		}
		this.startTest(hm, 0, false);
		double y = 0;
		y = performance;
		return y;
	}
	
	public void test(int timeToTest, boolean isInterrupt){
		HashMap yamlModify = new HashMap();
		yamlModify.put("concurrent_reads", 234);
		int tot = timeToTest;
		while(tot-->0){
			startTest(yamlModify,0, false);
			//System.out.println("performance is : " + getPerformance());
			//cym.setOptimal();
			//cym.runExp(samplePoints);
			System.out.println("tot = " + tot);
		}
	}
	
	public static void main(String[] args){
		AutoTestAdjust cym = new AutoTestAdjust("data/SUTconfig.properties");
		HashMap yamlModify = new HashMap();
		//yamlModify.put("concurrent_reads", 234);
		int tot = 4;
		while(tot-->0){
			cym.startTest(yamlModify,0,false);
			System.out.println("performance is : " + cym.getPerformanceByType(3));
			//cym.setOptimal();
			//cym.runExp(samplePoints);
			System.out.println("tot = " + tot);
			//System.out.println("latency is : " + cym.getPerformance());
		}
		
        
		cym.shutdown();
	}

	@Override
	public Instances collectPerfs(Instances samplePoints, String perfAttName) {
		Instances retVal = null;
		
		/*if(performanceType>2 && samplePoints.attribute("combinedPerf_zyq")== null){
			Attribute combinedPerf = new Attribute("combinedPerf_zyq");
			samplePoints.insertAttributeAt(combinedPerf, samplePoints.numAttributes());
		}*/
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		
		File perfFolder = new File(perfsfilepath);
		int tot=0;
		if(perfFolder.exists()){
			//let's get all the name set for the sample points
			Iterator<Instance> itr = samplePoints.iterator();
			TreeSet<String> insNameSet = new TreeSet<String>();
			HashMap<String, Integer> mapping = new HashMap<String, Integer>();
			int pos=0;
			while(itr.hasNext()){
				String mdstr = getMD5(itr.next());
				insNameSet.add(mdstr);
				mapping.put(mdstr, new Integer(pos++));
			}
			
			//now we collect
			File[] perfFiles = perfFolder.listFiles(new PerfsFileFilter(insNameSet));
			tot = perfFiles.length;
			if(tot > 0) isInterrupt = true;
			for(int i=0;i<tot;i++){
				Instance ins = samplePoints.get(mapping.get(perfFiles[i].getName()));
				double[] results = getPerf(perfFiles[i].getAbsolutePath());
				if(results!=null){
					//ins.setValue(samplePoints.numAttributes()-2, results[0]);
					ins.setValue(samplePoints.numAttributes()-1, results[0]);
				}
				
			}
		}
		retVal = samplePoints;
		retVal.setClassIndex(retVal.numAttributes()-1);
		System.out.println("Total number of collected performances is : "+tot);
		return retVal;
	}
	
	class PerfsFileFilter implements FileFilter{
		Set<String> nameSet=null;
		public PerfsFileFilter(Set<String> nameSet){
			this.nameSet = nameSet;
		}
		@Override
		public boolean accept(File file) {
			if(nameSet!=null && nameSet.contains(file.getName()))
					return true;
			return false;
		}
	}

	@Override
	public void test(int timeToTest) {
		// TODO Auto-generated method stub
		
	}
}
