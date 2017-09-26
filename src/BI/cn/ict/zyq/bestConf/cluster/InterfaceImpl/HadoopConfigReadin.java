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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ho.yaml.Yaml;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;
import cn.ict.zyq.bestConf.cluster.Utils.RemoteFileAccess;
import cn.ict.zyq.bestConf.cluster.Utils.SFtpConnectInfo;
import cn.ict.zyq.bestConf.util.ParseXMLToYaml;

public class HadoopConfigReadin implements ConfigReadin {

	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String filepath;
	
	public HadoopConfigReadin(){
		
	}
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		filepath= localPath + "/defaultConfig.yaml";
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
	}
	public HashMap loadFileToHashMap(String filePath) {
		HashMap hashmap = null; 
		File f = new File(filePath);
		try {
			hashmap = Yaml.loadType(new FileInputStream(f.getAbsolutePath()), HashMap.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return hashmap;
	}
	public void modify(HashMap toModify, HashMap updatedValues) {
		boolean flag;
		Iterator itrUpdated = updatedValues.entrySet().iterator();
		while(itrUpdated.hasNext()){
			Map.Entry entUpdated = (Map.Entry)itrUpdated.next();
			Object tempObj = toModify.get(entUpdated.getKey().toString());
			if(tempObj!=null){
				if (!entUpdated.getValue().toString().equals("NaN")) {
					if (Double.parseDouble(entUpdated.getValue().toString()) < 1.0 && Double.parseDouble(entUpdated.getValue().toString()) != 0.0) {
						toModify.put(entUpdated.getKey().toString(), entUpdated.getValue().toString());
					} else if(Double.parseDouble(entUpdated.getValue().toString()) == 0.0){
						 		toModify.put(entUpdated.getKey().toString(), Integer.parseInt("0"));
					} else {
							toModify.put(entUpdated.getKey().toString(), (int)Math.floor(Double.parseDouble(entUpdated.getValue().toString())));
						 }
				}
			}else
				System.out.println(entUpdated.getKey().toString() + " doesn't exist in original list!");
		}
	}
	@Override
	public HashMap modifyConfigFile(HashMap hm, String filepath) {
		HashMap ori = loadFileToHashMap(filepath);
		modify(ori, hm);
		return ori;
	}

	@Override
	public HashMap modifyConfigFile(HashMap hm) {
		HashMap ori = loadFileToHashMap(filepath);
		modify(ori, hm);
		return ori;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public static HashMap combine(String filename){
		HashMap resultYarn = ParseXMLToYaml.parseXMLToHashmap("data/yarn-default.xml");
		System.out.println("yarn size is : " + resultYarn.size());
		HashMap resultMapRed = ParseXMLToYaml.parseXMLToHashmap("data/mapred-default.xml");
		System.out.println("mapreduce size is : " + resultMapRed.size());
		HashMap combined = new HashMap();
		Iterator iterYarn = resultYarn.entrySet().iterator();
		 while(iterYarn.hasNext()){
	        Map.Entry entry = (Map.Entry) iterYarn.next();
	        String key = "yarn" + entry.getKey().toString();
	        String val = entry.getValue().toString();    
	        combined.put(key, val);
		 }
		 
		 Iterator iterMapred = resultMapRed.entrySet().iterator();
		 while(iterMapred.hasNext()){
	        Map.Entry entry = (Map.Entry) iterMapred.next();
	        String key = "mapred" + entry.getKey().toString();
	        String val = entry.getValue().toString();    
	        combined.put(key, val);
		 }
		 System.out.println("combined size is : " + combined.size());
		 File file = new File("data/" + filename);
		 try{
			if (!file.exists()) {
			    file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
				
			Iterator iter = combined.entrySet().iterator();
			 while(iter.hasNext()){
		        Map.Entry entry = (Map.Entry) iter.next();
		        String key = entry.getKey().toString();
		        String val = entry.getValue().toString();   
		        bw.write(key + ": "+ val + "\n" );
			 }
			 bw.close();
		}catch(IOException e){
			
		}
		return combined; 
	}
	
	public static void addPrefix(){
		File fileMapredRange = new File("data/mapred-site.yaml_range");
		File fileYarnRange = new File("data/yarn-site.yaml_range");
		File finalTargetFile = new File("data/combinedConfig.yaml_range");
        BufferedReader readerMapred = null;
        BufferedReader readerYarn = null;
        BufferedReader readerTarget = null;
       
        try {
        	FileWriter fw = new FileWriter(finalTargetFile.getAbsoluteFile());
     		BufferedWriter bw = new BufferedWriter(fw);
        	if (!fileMapredRange.exists()) {
        		fileMapredRange.createNewFile();
			}
        	if (!fileYarnRange.exists()) {
        		fileYarnRange.createNewFile();
			}
        	if (!finalTargetFile.exists()) {
        		finalTargetFile.createNewFile();
			}

            readerMapred = new BufferedReader(new FileReader(fileMapredRange));
            String mapredString = null;
            int lineMapred = 1;
           
            while ((mapredString = readerMapred.readLine()) != null) {
             
               
            	mapredString = "mapred" + mapredString;
            	System.out.println("line " + lineMapred + ": " + mapredString);
            	bw.write(mapredString + "\n");
            	lineMapred++;
            }
            readerMapred.close();
            readerYarn = new BufferedReader(new FileReader(fileYarnRange));
            String yarnString = null;
            int lineYarn = 1;
            
            while ((yarnString = readerYarn.readLine()) != null) {
              
                
            	yarnString = "yarn" + yarnString;
            	System.out.println("line " + lineYarn + ": " + yarnString);
            	bw.write(yarnString + "\n");
            	lineYarn++;
            }
            readerYarn.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readerMapred != null) {
                try {
                	readerMapred.close();
                } catch (IOException e1) {
                }
            }
            if (readerYarn != null) {
                try {
                	readerYarn.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
}
