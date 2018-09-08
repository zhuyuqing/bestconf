/**
 * Copyright (c) 2017 Institute of Computing Technology, Chinese Academy of Sciences, 2017 
 * Institute of Computing Technology, Chinese Academy of Sciences contributors. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package cn.ict.zyq.bestConf.cluster.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
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

import cn.ict.zyq.bestConf.bestConf.sysmanipulator.ClusterManager;
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
				
				SystemOperation so = new SUTSystemOperation();
				
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
						
		sutTest = new SUTTest(testDurationTimeOutInSec);
		sutTest.initial(targetTestServer, targetTestUsername, targetTestPassword, targetTestPath, maxRoundConnection, sshReconnectWatingtime);
		systemPerformance = new SUTPerformance();
		
	};	
	
	private void getProperties(){
		try{
			pps = PropertiesUtil.GetAllProperties(configFilePath);
		}catch(IOException e){
			e.printStackTrace();
		}
		sshReconnectWatingtime = Integer.parseInt(pps.getProperty("sshReconnectWatingtimeInSec"));
		maxRoundConnection = Integer.parseInt(pps.getProperty("maxConnectionRetries"));
		targetTestPath = pps.getProperty("targetTestPath");
		remoteConfigFilePath = pps.getProperty("remoteConfigFilePath");
		
		performanceType = Integer.parseInt(pps.getProperty("performanceType"));
		performanceType=performanceType>2?2:performanceType;
		
		targetTestErrorNum = Integer.valueOf(pps.getProperty("maxConsecutiveFailedSysStarts"));
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
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
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
				writer.write(ins.value(ins.attribute(ins.numAttributes()-1))+"\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
	public Instances runExp(Instances samplePoints, String perfAttName){
		Instances retVal = null;
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		int pos = samplePoints.numInstances();
		int count = 0;
		for (int i = 0; i < pos; i++) {
			Instance ins = samplePoints.get(i);
			HashMap hm = new HashMap();
			int tot = 0;
			for (int j = 0; j < ins.numAttributes(); j++) {
				hm.put(ins.attribute(j).name(), ins.value(ins.attribute(j)));
			}

			boolean testRet;
			if (Double.isNaN(ins.value(ins.attribute(ins.numAttributes() - 1)))) {
				testRet = this.startTest(hm, i, isInterrupt);
				double y = 0;
				if (!testRet) {// the setting does not work, we skip it
					y = -1;
					count++;
					if (count >= targetTestErrorNum) {
						System.out.println("There must be somthing wrong with the system. Please check and restart.....");
						System.exit(1);
					}
				} else {
					y = getPerformanceByType(performanceType);
					count = 0;
				}

				ins.setValue(samplePoints.numAttributes() - 1, y);
				writePerfstoFile(ins);
			} else {
				continue;
			}
		}
		retVal = samplePoints;
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
			System.out.println("tot = " + tot);
		}
	}
	
	public static void main(String[] args){
		AutoTestAdjust cym = new AutoTestAdjust("data/SUTconfig.properties");
		HashMap yamlModify = new HashMap();
		int tot = 4;
		while(tot-->0){
			cym.startTest(yamlModify,0,false);
			System.out.println("performance is : " + cym.getPerformanceByType(3));
			System.out.println("tot = " + tot);
		}
		
        
		cym.shutdown();
	}

	@Override
	public Instances collectPerfs(Instances samplePoints, String perfAttName) {
		Instances retVal = null;
		
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
	}
}
