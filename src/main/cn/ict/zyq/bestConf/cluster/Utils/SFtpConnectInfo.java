package cn.ict.zyq.bestConf.cluster.Utils;

/**
* SFTP������Ϣ��
* 
* @author ���� <>
*/
public class SFtpConnectInfo {

private String username;

private String password;

private String host;

private int port;

public SFtpConnectInfo() {

}

public SFtpConnectInfo(String username, String password, String host, int port) {
this.username = username;
this.password = password;
this.host = host;
this.port = port;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String getPassword() {
return password;
}

public void setPassword(String password) {
this.password = password;
}

public String getHost() {
return host;
}

public void setHost(String host) {
this.host = host;
}

public int getPort() {
return port;
}

public void setPort(int port) {
this.port = port;
}

@Override
public String toString() {
return "SftpConnectInfo [username=" + username + ", password="
+ password + ", host=" + host + ", port=" + port + "]";
}

}
