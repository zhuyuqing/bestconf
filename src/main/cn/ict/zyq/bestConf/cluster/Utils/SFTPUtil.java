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

/**
 * SFTP帮助�?
 * @author wangbailin
 *
 */
public class SFTPUtil {
	
	
	/**
	 * 连接sftp服务�?
	 * @param host 远程主机ip地址
	 * @param port sftp连接端口，null 时为默认端口
	 * @param user 用户�?
	 * @param password 密码
	 * @return
	 * @throws JSchException 
	 */
	public static Session connect(String host, Integer port, String user, String password) throws JSchException{
		Session session = null;
		try {
			JSch jsch = new JSch();
			if(port != null){
				session = jsch.getSession(user, host, port.intValue());
			}else{
				session = jsch.getSession(user, host);
			}
			session.setPassword(password);
			//设置第一次登陆的时�?提示，可选�?:(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			//30秒连接超�?
			session.connect(3000);
		} catch (JSchException e) {
			e.printStackTrace();
			System.out.println("SFTPUitl 获取连接发生错误");
			throw e;
		}
		return session;
	}
	
	/**
	 * sftp上传文件(�?
	 * @param directory
	 * @param uploadFile
	 * @param sftp
	 * @throws Exception 
	 */
	public static void upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception{
		//System.out.println("sftp upload file [directory] : "+ directory);
		//System.out.println("sftp upload file [uploadFile] : "+ uploadFile);
		File file = new File(uploadFile);
		if(file.exists()){
			//这里有点投机取巧，因为ChannelSftp无法去判读远程linux主机的文件路�?无奈之举
			try {
				Vector content = sftp.ls(directory);
				if(content == null){
					sftp.mkdir(directory);
					System.out.println("mkdir:" + directory);
				}
			} catch (SftpException e) {
				sftp.mkdir(directory);
			}
			//进入目标路径
			sftp.cd(directory);
			System.out.println("directory: " + directory);
			if(file.isFile()){
				InputStream ins = new FileInputStream(file);
				//中文名称�?
				sftp.put(ins, new String(file.getName().getBytes(),"UTF-8"));
				//sftp.setFilenameEncoding("UTF-8");
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
	
	public static void main(String[] args){
		ChannelSftp sftp = null;
		Session session = null;
		try {
			session = SFTPUtil.connect("172.16.48.209", 22, "root", "ljx123");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			SFTPUtil.upload("/opt/huawei", "f:/liujianxun/workspace/test/cassandra2.yaml", sftp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(sftp != null)sftp.disconnect();
			if(session != null)session.disconnect();
		}
	}
}