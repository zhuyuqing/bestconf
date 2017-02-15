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
	private String sparktargetfilePath = "data/spark-defaults.conf";
	private String remoteconffilename = "spark-defaults.conf";
	
	private String sparkenvpath="data/spark-env.sh";
	private String remoteenvfilename = "spark-env.sh";
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
	}

	public void uploadConfigFile() {
		ChannelSftp sftp = null;
		Session session;
		Channel channel;
		session = null;
		channel = null;
		try {
			removeRemoteConfigFile(remoteconffilename);
			removeRemoteConfigFile(remoteenvfilename);
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
			SFTPUtil.upload(remotePath, localPath + "/" + remoteenvfilename, sftp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();
		}
	}

	public void removeRemoteConfigFile(String filename) {
		String cmdRemove = "cd " + remotePath + "; rm -f " + filename;
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdRemove);
			System.out.println("Here is SUT start information:");
			System.out.println("…æ≥˝≈‰÷√Œƒº˛≥…π¶£°");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("…æ≥˝≈‰÷√Œƒº˛ ß∞‹£°");
		}
	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		// TODO Auto-generated method stub
		File file = new File(sparktargetfilePath);
		File fileenv = new File(sparkenvpath);
		// if file doesnt exists, then create it
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
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			fwenv = new FileWriter(fileenv.getAbsoluteFile());
			BufferedWriter bwenv = new BufferedWriter(fwenv);
		
			Iterator it_client=hm.entrySet().iterator();
			while(it_client.hasNext()){
			    Map.Entry entry=(Map.Entry)it_client.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.startsWith("Time.")){
	    			bw.write(key.substring(5) + " " + value + "ms\n");
			    }else if(key.startsWith("Size.")){
			    		  bw.write(key.substring(5) + " " + value + "k\n");
			    	  }else{
			    		  bw.write(key + " " + value + "\n");
			    	 }
			    if(key.startsWith("Size.spark.driver.memory")){
			    	bwenv.write("SPARK_DRIVER_MEMORY="+ value + "k\n");
			    }
			    if(key.startsWith("Size.spark.executor.memory")){
			    	bwenv.write("SPARK_EXECUTOR_MEMORY="+ value + "k\n");
			    }
			    if(key.startsWith("spark.executor.cores")){
			    	bwenv.write("SPARK_EXECUTOR_CORES="+ value + "\n");
			    }
			    
			 }
			bwenv.write("export JAVA_HOME=/usr/java/jdk1.7.0_79" + "\n");
			bwenv.write("export SCALA_HOME=/opt/hadoop-2.6.5/spark/scala-2.10.6" + "\n");
			bwenv.write("export SPARK_MASTER_IP=172.16.48.41" + "\n");
			bwenv.write("export HADOOP_CONF_DIR=/opt/hadoop-2.6.5/hadoop-2.6.5/etc/hadoop" + "\n");
			bw.close();
			bwenv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args){
		SparkConfigReadin sr = new SparkConfigReadin();
		SparkConfigWrite sw = new SparkConfigWrite();
		String filepath = "data/spark/spark-defaults.conf";
		String filew = "data/spark/spark.conf";
		sr.initial("23", "root", "ljx123", "data/spark", "");
		sw.initial("23", "root", "ljx123", "data/spark", "");
		HashMap hm = new HashMap();
		hm.put("spark.files.maxPartitionBytes", 3400);
		HashMap target = sr.modifyConfigFile(hm);
		sw.writetoConfigfile(target);
	}
}
