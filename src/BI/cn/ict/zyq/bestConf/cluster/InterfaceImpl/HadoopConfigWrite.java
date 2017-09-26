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

import java.io.IOException;
import java.util.HashMap;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigWrite;
import cn.ict.zyq.bestConf.cluster.Utils.SFTPUtil;
import cn.ict.zyq.bestConf.util.ParseXMLToYaml;

public class HadoopConfigWrite implements ConfigWrite {
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String Config_mapred;
	private String Config_yarn;

	public HadoopConfigWrite() {

	}

	@Override
	public void initial(String server, String username, String password,
			String localPath, String remotePath) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		Config_mapred = "mapred-site.xml";
		Config_yarn = "yarn-site.xml";
	}

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

	public void uploadConfigFile() {
		ChannelSftp sftp = null;
		Session session;
		Channel channel;
		session = null;
		channel = null;
		try {
			removeRemoteConfigFile(Config_mapred);
			removeRemoteConfigFile(Config_yarn);
			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload(remotePath, localPath + "/" + Config_mapred, sftp);

			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();

			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;

			SFTPUtil.upload(remotePath, localPath + "/" + Config_yarn, sftp);
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
			System.out.println("ɾ�������ļ��ɹ���");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ɾ�������ļ�ʧ�ܣ�");
		}

	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		String sourceYarn = "data/yarn-site-initial.xml";
		String destinationYarn = "data/yarn-site.xml";
		String sourceMapred = "data/mapred-site-initial.xml";
		String destinationMapred = "data/mapred-site.xml";
		ParseXMLToYaml.split(hm, sourceYarn, destinationYarn, sourceMapred, destinationMapred);
	}
    
}
