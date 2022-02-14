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
package cn.ict.zyq.bestConf.cluster.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {
	
	public static ChannelSftp connect(String server, String username, String password) throws JSchException{
		Session session = null;
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, server);
			session.setPassword(password);
			
			session.setConfig("StrictHostKeyChecking", "no");
			//time out
			session.connect(3000);

			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
		} catch (JSchException e) {
			e.printStackTrace();
			System.out.println("SFTPUitl connection error");
			throw e;
		}
		return sftp;
	}
	
	public static void upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception{
		
		File file = new File(uploadFile);
		if(file.exists()){
			
			try {
				Vector content = sftp.ls(directory);
				if(content == null){
					sftp.mkdir(directory);
					System.out.println("mkdir:" + directory);
				}
			} catch (SftpException e) {
				sftp.mkdir(directory);
			}
			sftp.cd(directory);
			System.out.println("directory: " + directory);
			if(file.isFile()){
				InputStream ins = new FileInputStream(file);
				
				sftp.put(ins, new String(file.getName().getBytes(),"UTF-8"));
				
			}else{
				File[] files = file.listFiles();
				for (File file2 : files) {
					String dir = file2.getAbsolutePath();
					if(file2.isDirectory()){
						String str = dir.substring(dir.lastIndexOf(file2.separator));
						directory = directory + str;
					}
					System.out.println("directory is :" + directory);
					upload(directory,dir,sftp);
				}
			}
		}
	}
	
}
