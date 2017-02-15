package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.ho.yaml.Yaml;

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;
import cn.ict.zyq.bestConf.cluster.Utils.PropertiesUtil;

public class MySQLConfigReadin implements ConfigReadin {
	
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String filepath;
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		// TODO Auto-generated method stub
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		filepath = localPath + "/my_default.cnf";
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
	public void downLoadConfigFile(String fileName) {
		// TODO Auto-generated method stub
		
	}
	public HashMap loadFileToHashMap(String filePath) {
		HashMap hashmap = null; 
		try {
			 Properties pps = PropertiesUtil.GetAllProperties(filePath);
			 hashmap = new HashMap((Map) pps); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashmap;
	}
	public HashMap modify(HashMap original, HashMap config) {
		boolean flag;
		Iterator ithm_config = config.entrySet().iterator();
		while(ithm_config.hasNext()){
			Map.Entry entry_hm_config = (Map.Entry) ithm_config.next();
			String key_hm_config = entry_hm_config.getKey().toString();
			Iterator ithm_ori = original.entrySet().iterator();
			flag = false;
			while(ithm_ori.hasNext()){
				Map.Entry entry_hm_ori = (Map.Entry)ithm_ori.next();
				String key_hm_ori = entry_hm_ori.getKey().toString();
				String value_hm_config = entry_hm_config.getValue().toString();
				if(key_hm_ori.equals(key_hm_config)){
					if(!value_hm_config.equals("NaN")){
						if(Double.parseDouble(value_hm_config) < 1.0){
					original.put(key_hm_ori,value_hm_config);
							flag = true;
					break;
						}else{
							original.put(key_hm_ori, Math.round(Double.parseDouble(value_hm_config)));
							flag = true;
							break;
						}
					}
				}
			}
			if(!flag){
				System.out.println(key_hm_config + "在配置文件中没找到!");
				String valueofconfig = entry_hm_config.getValue().toString();
				if(Double.parseDouble(valueofconfig) < 1.0){
					original.put(key_hm_config, valueofconfig);
				}else{
					original.put(key_hm_config, Math.round(Math.floor(Double.parseDouble(valueofconfig))));
				}
			}
		}
		return original;
	}
	@Override
	public HashMap modifyConfigFile(HashMap hm, String filepath) {
		HashMap ori = loadFileToHashMap(filepath);
		HashMap result = modify(ori, hm);
		return result;
	}

	@Override
	public HashMap modifyConfigFile(HashMap hm) {
		HashMap ori = loadFileToHashMap(filepath);
		HashMap result = modify(ori, hm);
		return result;
	}
	
}
