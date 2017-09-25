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

public class MySQLConfigWrite implements ConfigWrite {
	
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String mysqltargetfilePath = "data/my.cnf";
	private String remoteconffilename = "my.cnf";
	
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
			session = SFTPUtil.connect(server, 22, username, password);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload(remotePath, localPath + "/" + remoteconffilename, sftp);
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
			System.out.println("É¾³ýÅäÖÃÎÄ¼þ³É¹¦£¡");
			if (session != null)
				session.close();
			closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("É¾³ýÅäÖÃÎÄ¼þÊ§°Ü£¡");
		}
	}

	@Override
	public void writetoConfigfile(HashMap hm) {
		// TODO Auto-generated method stub
		File file = new File(mysqltargetfilePath);
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
		
			Iterator it_client=hm.entrySet().iterator();
			while(it_client.hasNext()){
			    Map.Entry entry=(Map.Entry)it_client.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().equals("client")){
			    		
			    		bw.write("[" + key + "]" + "\n");
			    		break;
			    }
			}
			it_client=hm.entrySet().iterator();
			while(it_client.hasNext()){
			    Map.Entry entry=(Map.Entry)it_client.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().startsWith("client")){
			    	if(!key.equals("client")){
			    		
			    		bw.write(key.substring(6) + "=" + value + "\n");
			    	}
			    }
			}
			Iterator it_mysql=hm.entrySet().iterator();
			while(it_mysql.hasNext()){
			    Map.Entry entry=(Map.Entry)it_mysql.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().equals("mysql")){
			    		
			    		bw.write("[" + key + "]" + "\n");
			    		break;
			    }
			}
			it_mysql=hm.entrySet().iterator();
			while(it_mysql.hasNext()){
			    Map.Entry entry=(Map.Entry)it_mysql.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().startsWith("mysql")){
			    	if(!key.equals("mysql")){
			    		
			    		bw.write(key.substring(5) + "=" + value + "\n");
			    	}
			    }
			}
			Iterator it_sqlmyd=hm.entrySet().iterator();
			while(it_sqlmyd.hasNext()){
			    Map.Entry entry=(Map.Entry)it_sqlmyd.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().equals("sqlmyd")){
			    	if(key.equals("sqlmyd")){
			    		bw.write("[mysqld]" + "\n");
			    	    break;
			        }
			    }
			}
			it_sqlmyd=hm.entrySet().iterator();
			while(it_sqlmyd.hasNext()){
			    Map.Entry entry=(Map.Entry)it_sqlmyd.next();
			    String key = entry.getKey().toString();
			    String value = entry.getValue().toString();
			    if(key.toString().startsWith("sqlmyd")){
			    	if(!key.equals("sqlmyd")){
			    		if(key.startsWith("sqlmyd_K")){
			    			
			    			if(value.charAt(value.length()-1) != 'K')
			    				bw.write(key.substring(8) + "=" + value + "K\n");
			    			else
			    				bw.write(key.substring(8) + "=" + value + "\n");
			    		}else if(key.startsWith("sqlmyd_Bool")){
			    			double valueofdouble = Double.parseDouble(value);
			    			if(valueofdouble >= 0 && valueofdouble < 0.5){ 
			    				
			    				bw.write(key.substring(11) + "=" + "false\n");
			    			}else{
			    				
			    				bw.write(key.substring(11) + "=" + "true\n");
			    			}
			    		}else if(key.startsWith("sqlmyd_Type")){
			    			double valueofdouble = Double.parseDouble(value);
			    			long type = Math.round(Math.floor(valueofdouble));
			    			
			    			bw.write(key.substring(11) + "=" + type + "\n");
			    		}else{
			    			
			    			bw.write(key.substring(6) + "=" + value + "\n");
			    		}
			    	}
			    }
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
