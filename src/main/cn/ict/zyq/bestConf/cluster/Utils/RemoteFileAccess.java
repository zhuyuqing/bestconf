package cn.ict.zyq.bestConf.cluster.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class RemoteFileAccess {

	public void download(SFtpConnectInfo connectInfo, String localPath, final String remotePath) {
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp channel = null;
		OutputStream outs = null;
		try {
			session = jsch.getSession(connectInfo.getUsername(), connectInfo.getHost(), connectInfo.getPort());
			session.setPassword(connectInfo.getPassword());
			Properties props = new Properties();
			props.put("StrictHostKeyChecking", "no");
			session.setConfig(props);
			session.connect(5000); 
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(5000);
			outs = new BufferedOutputStream(new FileOutputStream(new File(localPath)));
			channel.get(remotePath, outs, new SftpProgressMonitor() {

				

				private long current = 0;

				@Override
				public void init(int op, String src, String dest, long max) {
					
				}

				@Override
				public void end() {
					
				}

				@Override
				public boolean count(long count) {
					current += count;

					return true;
				}
			});
		} catch (JSchException e) {
			System.err.println(String.format("connect remote host[%s:%d] occurs error "+connectInfo.getHost()+
					+connectInfo.getPort()));
			e.printStackTrace();
		} catch (SftpException e) {
			System.err.println(String.format("get remote file:%s occurs error", remotePath));
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println(String.format("can not find local file:%s", localPath));
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(outs);
			if (null != channel) {
				channel.disconnect();
			}
			if (null != session) {
				session.disconnect();
			}
		}
	}
	public static void main(String[] args){
		
	}
}
