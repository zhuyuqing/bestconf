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
package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigWrite;
import cn.ict.zyq.bestConf.cluster.Utils.PropertiesUtil;
import cn.ict.zyq.bestConf.cluster.Utils.SFTPUtil;

public class SparkConfigWrite implements ConfigWrite {
	
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	
	private String sparktargetfilePath = "data/kmeans.conf";
	private String remoteconffilename = "kmeans.conf";

	
	private String sparkenvpath="data/spark-env";
	private String remoteenvfilename = "spark-env.sh";
	
	private String remoteEnvPath = "/opt/hadoop-2.6.5/spark/spark-1.6.1/conf";
	
	private HashMap<String, String[]> enums = new HashMap();
	private String[] spark_io_compression_codec = {"lz4", "lzf", "snappy"};
	private String[] spark_serializer = {"org.apache.spark.serializer.JavaSerializer", "org.apache.spark.serializer.KryoSerializer"};
	private String[] spark_shuffle_manager = {"hash", "sort"};
	public Connection getConnection() {
		try {
			connection = new Connection(server);
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(
					username, password);
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public void closeConnection() {
		try {
			if (connection.connect() != null) {
				connection.close();
			}
		} catch (IOException e) {

		} finally {
			connection.close();
		}
	}
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		// TODO Auto-generated method stub
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		enums.put("spark_io_compression_codec", spark_io_compression_codec);
		enums.put("spark_serializer", spark_serializer);
		enums.put("spark_shuffle_manager", spark_shuffle_manager);
	}

	public void uploadConfigFile() {
		ChannelSftp sftp = null;
		Session session;
		Channel channel;
		session = null;
		channel = null;
		try {
			removeRemoteConfigFile(remoteconffilename);
			removeRemoteEnvConfigFile(remoteenvfilename);
			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload(remotePath, localPath + "/" + remoteconffilename, sftp);
			
			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();
			
			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload(remoteEnvPath, localPath + "/" + "spark-env", sftp);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();
			filenameChange(remoteenvfilename);
		}
	}
	
	public void filenameChange(String filename) {
		String cmdchange = "cd " + remoteEnvPath + "; mv spark-env " + filename;
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdchange);
			System.out.println("Here is SUT start information:");
			System.out.println("Succeed in changing filename of spark-env！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to change filename of spark-env！");
		}
	}
	
	public void removeRemoteConfigFile(String filename) {
		String cmdRemove = "cd " + remotePath + "; rm -f " + filename;
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdRemove);
			System.out.println("Here is SUT start information:");
			System.out.println("Succeed in removing configuration file！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to remove configuration file！");
		}
	}
	public void removeRemoteEnvConfigFile(String filename) {
		String cmdRemove = "cd " + remoteEnvPath + "; rm -f " + filename;
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdRemove);
			System.out.println("Here is SUT start information:");
			System.out.println("Succeed in removing configuration file of env！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to remove configuration file of env！");
		}
	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		// TODO Auto-generated method stub
		File file = new File(sparktargetfilePath);
		File fileenv = new File(sparkenvpath);
		// if file doesn't exist, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!fileenv.exists()) {
			try {
				fileenv.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw = null;
		FileWriter fwenv = null;
		BufferedWriter bw = null;
		BufferedWriter bwenv = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			fwenv = new FileWriter(fileenv.getAbsoluteFile());
		    bwenv = new BufferedWriter(fwenv);
			
			StringBuffer targetString = new StringBuffer();
			
			Iterator it_client=hm.entrySet().iterator();
			while(it_client.hasNext()){
			    Map.Entry entry = (Map.Entry)it_client.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
		       
			    if(key.startsWith("Time.")){
	    			bw.write(key.substring(5) + " " + value + "ms\n");
			    }else if(key.startsWith("Size.")){
			    		  bw.write(key.substring(5) + " " + value + "k\n");
			    	  }else if(key.startsWith("Bool.")){
			    		  		double valueofdouble = Double.parseDouble(value);
			    		  		if(valueofdouble >= 0 && valueofdouble < 0.5){ 
			    		  			bw.write(key.substring(5) + " " + "false\n");
			    		  		}else{
			    		  			bw.write(key.substring(5) + " " + "true\n");
			    		  		}	
					        }else if(key.startsWith("Type.")){
				    	  		    String targetkey = key.substring(5);
				    	  		    int index = Double.valueOf(value).intValue();
				    	  		    String enumKey = targetkey.replace('.', '_'); 
				    	  		    bw.write(targetkey + " " + enums.get(enumKey)[index] + "\n");
				    	  		}else{
				    	  			bw.write(key + " " + value + "\n");
				    	  		}
					        
			    if(key.startsWith("Size.spark.driver.memory")){
			    	bwenv.write("export SPARK_DRIVER_MEMORY="+ value + "k\n");
			    }
			    if(key.startsWith("Size.spark.executor.memory")){
			    	bwenv.write("export SPARK_EXECUTOR_MEMORY="+ value + "k\n");
			    }
			    if(key.startsWith("hibench.yarn.executor.cores")){
			    	bwenv.write("export SPARK_EXECUTOR_CORES="+ value + "\n");
			    }
			}
				bwenv.write("export JAVA_HOME= " + "\n");
				bwenv.write("export SCALA_HOME= " + "\n");
				bwenv.write("export SPARK_MASTER_IP= " + "\n");
				bwenv.write("export HADOOP_CONF_DIR= " + "\n"); 
			
			bw.close();
			bwenv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
