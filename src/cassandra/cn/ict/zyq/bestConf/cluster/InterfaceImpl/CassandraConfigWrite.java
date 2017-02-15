package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigWrite;
import cn.ict.zyq.bestConf.cluster.Utils.SFTPUtil;

public class CassandraConfigWrite implements ConfigWrite {
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String cassandratargetfilePath;
	private String remoteconffilename = "cassandra.yaml";
	
	public CassandraConfigWrite(){
		
	}
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		// TODO Auto-generated method stub
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.cassandratargetfilePath = "data/" + server;
	}
	public Connection getConnection(){
		try{
			connection = new Connection(server);
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed...");
			}
		}catch (IOException e) {
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
	public void uploadConfigFile() {
		ChannelSftp sftp = null;
		Session session;
		Channel channel;
		session = null;
		channel = null;
		try {
			removeRemoteConfigFile(remoteconffilename);
			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload(remotePath, localPath + "/" + server, sftp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();
			changeRemoteConfFileName(server);
		}
	}
	public void changeRemoteConfFileName(String filename) {
		String cmdChangeName = "cd " + remotePath + "; mv " + filename + " cassandra.yaml";
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdChangeName);
			System.out.println("Here is SUT start information:");
			System.out.println("修改远程配置文件名字成功！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("修改远程配置文件名字失败！");
		}
	}
	public void removeRemoteConfigFile(String filename) {
		String cmdRemove = "cd " + remotePath + "; rm -f " + filename;
		try {
			ch.ethz.ssh2.Session session = this.getConnection().openSession();
			session.execCommand(cmdRemove);
			System.out.println("Here is SUT start information:");
			System.out.println("删除配置文件成功！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("删除配置文件失败！");
		}
	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		// TODO Auto-generated method stub
		try{ 
	        ArrayList seed_provider = (ArrayList)hm.get("seed_provider");    
	        HashMap seed_provider_parameters = (HashMap)seed_provider.get(0);
	        
	        ArrayList seed_provider_parameters_seeds = (ArrayList)seed_provider_parameters.get("parameters");
	        //System.out.println(seed_provider_parameters.get("class_name").getClass());
	        HashMap seed_provider_parameters_seeds_hashmap = (HashMap)seed_provider_parameters_seeds.get(0);
	        //StringBuilder seeds_value = new StringBuilder(seed_provider_parameters_seeds_hashmap.get("seeds").toString());
	        //seeds_value.append("\"");
	        //seeds_value.insert(0, '\"');
	        String seeds_value = seed_provider_parameters_seeds_hashmap.get("seeds").toString();
	        seeds_value = "\"" + seeds_value;
	        seeds_value = seeds_value + "\"";
	        seed_provider_parameters_seeds_hashmap.put("seeds", seeds_value.toString());
	        seed_provider_parameters_seeds.set(0, seed_provider_parameters_seeds_hashmap);
	        seed_provider_parameters.put("parameters", seed_provider_parameters_seeds);
	        seed_provider.set(0, seed_provider_parameters);
	        hm.put("seed_provider", seed_provider);
	        //System.out.println("seeds value = " + seeds_value );
	        //System.out.println(((HashMap)((ArrayList)ml.get("seed_provider")).get(0)).get("parameters"));
	        //System.out.println("seeds = " + seed_provider_parameters_seeds.get(0).getClass());
	        
	        
	        String enabled, optional, keystore, keystore_password;
	        HashMap client_hashmap = (HashMap)hm.get("client_encryption_options");
            enabled = client_hashmap.get("enabled").toString();
            optional = client_hashmap.get("optional").toString();
            keystore = client_hashmap.get("keystore").toString();
            keystore_password = client_hashmap.get("keystore_password").toString();
            
            String  internode_encryption, keystore_server, keystore_password_server, truststore, truststore_password;
            HashMap server_hashmap = (HashMap)hm.get("server_encryption_options");
            internode_encryption = server_hashmap.get("internode_encryption").toString();
            keystore_server = server_hashmap.get("keystore").toString();
            keystore_password_server = server_hashmap.get("keystore_password").toString();
            truststore = server_hashmap.get("truststore").toString();
            truststore_password = server_hashmap.get("truststore_password").toString();
            //System.out.println(client_hashmap.get("keystore_password").getClass());
			
            
           // String concurrent_reads = ml.get("concurrent_reads").toString();
           // ml.put("concurrent_reads", 123);
		    
            File file = new File(cassandratargetfilePath);
				// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				Iterator iter = hm.entrySet().iterator();
		        while(iter.hasNext()){
			        Map.Entry entry = (Map.Entry) iter.next();
			        Object key = entry.getKey();
			        Object val = entry.getValue();
			        boolean flag = false;
			        if(key.equals("seed_provider")){
			        	bw.write(key + ":\n");
			        	String classname = seed_provider_parameters.get("class_name").toString();
			        	bw.write("    - " + "class_name: " + classname + "\n");
			        	bw.write("      parameters:\n");
			        	bw.write("          " + "- seeds: " + seeds_value + "\n");
			        	flag = true;
			        }
			        if(key.equals("server_encryption_options")){
			        	flag = true;
			        	bw.write("server_encryption_options:\n");
			        	bw.write("    " + "internode_encryption: " + internode_encryption + "\n");
			        	bw.write("    " + "keystore: " + keystore_server + "\n");
			        	bw.write("    " + "keystore_password: " + keystore_password_server + "\n");
			        	bw.write("    " + "truststore: " + truststore + "\n");
			        	bw.write("    " + "truststore_password: " + truststore_password + "\n");
			        }
			        if(key.equals("client_encryption_options")){
			        	flag = true;
			        	bw.write("client_encryption_options:\n");
			        	bw.write("    " + "enabled: " + enabled + "\n");
			        	bw.write("    " + "optional: " + optional + "\n");
			        	bw.write("    " + "keystore: " + keystore + "\n");
			        	bw.write("    " + "keystore_password: " + keystore_password + "\n");
			        	

			        }
			        if(!flag){
			        	if(val != null)
			        		bw.write(key.toString() + ": " + val.toString() +"\n");
			        	else
			        		bw.write(key.toString() + ":" + "\n");
			        }
			    }
				//bw.write(content);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public static void main(String[] args){
		CassandraConfigWrite cassandraW = new CassandraConfigWrite();
		CassandraConfigReadin cassandraR = new CassandraConfigReadin();
		cassandraR.initial("172.16.48.41", "", "", "data", "/opt/huawei/sds/apache-cassandra-2.1.12/conf");
		cassandraW.initial("172.16.48.41", "", "", "data", "/opt/huawei/sds/apache-cassandra-2.1.12/conf");
	    String filePath = "data/cassandra.yaml";
	    HashMap hm = new HashMap();
	    hm.put("concurrent_reads", 322);
		HashMap target = cassandraR.modifyConfigFile(hm);
		//HashMap target = cassandraR.modifyConfigFile(hm);
		cassandraW.writetoConfigfile(target);
	}
}
