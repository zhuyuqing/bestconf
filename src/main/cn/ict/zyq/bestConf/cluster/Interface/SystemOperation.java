package cn.ict.zyq.bestConf.cluster.Interface;

public interface SystemOperation {
    void initial(String server, String username, String password, String shellsPath, int timeout, int maxRoundConnection, int sshReconnectWatingtime);
	void start();
	void shutdown();
	void stopSystem();
	boolean isClosed();
	boolean isStarted();
	void terminateSystem();
}
