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

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigWrite;
import cn.ict.zyq.bestConf.cluster.Utils.SFTPUtil;
import com.jcraft.jsch.ChannelSftp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IoTDBConfigWrite implements ConfigWrite {

	private Connection connection;
	private String server;
	private int port = 22;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String iotdbtargetfilePath = "data/config.properties";
	private String remoteconffilename = "config.properties";
	private String shellofTransConf;
	private int sshFileTransConnTimeout;
	private int maxRoundConnection = 60;
	private int sshReconnectWatingtime = 10;

	private HashMap<String, String> enums = new HashMap();

	public Connection getConnection() {
		int round = 0;
		while (round < maxRoundConnection) {
			try {
				connection = new Connection(server, port);
				connection.connect();
				boolean isAuthenticated = connection.authenticateWithPassword(username, password);
				if (isAuthenticated == false) {
					throw new IOException("Authentication failed...");
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				connection.close();
				connection = null;
				System.err.println("================= connection is null in round " + round);
				try {
					Thread.sleep(sshReconnectWatingtime * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			round++;
		}
		return connection;
	}

	public void closeConnection() {
		try {
			if (connection != null && connection.connect() != null) {
				connection.close();
			}
		} catch (IOException e) {
			 e.printStackTrace(); 
		} finally {
			if (connection != null)
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
		this.sshFileTransConnTimeout = sshFileTransConnTimeout * 1000;
		shellofTransConf = "cd " + "/home/fit/anyanzhe" + ";./filetransfer.exp";
	}


	public void confTransfer() {
		try {
			getConnection();
			if(connection == null)
				throw new IOException("Unable to connect the server!");
			ch.ethz.ssh2.Session session = connection.openSession();
			session.execCommand(shellofTransConf);
			System.out.println("Here is SUT start information:");
			System.out.println("Configuration file had been successfully transfered！");
			if (session != null)
				session.close();
			closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to remove configuration file！");
		}
	}


	public void uploadConfigFile() {
		SFTPUtil sftpUtil = new SFTPUtil();
		try {
			ChannelSftp sftp = sftpUtil.connect(server, username, password);
			removeRemoteConfigFile(remoteconffilename);
			sftpUtil.upload(remotePath, localPath + "/" + remoteconffilename, sftp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		confTransfer();
	}

	public void removeRemoteConfigFile(String filename) {
		String cmdRemove = "cd " + remotePath + "; rm -f " + filename;
		try {
			getConnection();
			if(connection == null)
				throw new IOException("Unable to connect the server!");
			ch.ethz.ssh2.Session session = connection.openSession();
			session.execCommand(cmdRemove);
			System.out.println("Here is SUT start information:");
			System.out.println("Configuration file had been successfully removed！");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to remove configuration file！");
		}
	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		// TODO Auto-generated method stub
		File file = new File(iotdbtargetfilePath);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator it = hm.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry entry=(Map.Entry)it.next();
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				bw.write(key + "=" + value + "\n");
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		IoTDBConfigReadin sr = new IoTDBConfigReadin();
		IoTDBConfigWrite sw = new IoTDBConfigWrite();
		sr.initial("23", "root", "ljx123", "data", "");
		sw.initial("23","root", "ljx123", "data", "");
		HashMap hm = new HashMap();
		hm.put("sqlmyd_host_cache_size", -1);
		HashMap target = sr.modifyConfigFile(hm);
		sw.writetoConfigfile(target);
	}
}
