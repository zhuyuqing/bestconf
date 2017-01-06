package cn.ict.zyq.bestConf.cluster.Interface;

import java.util.HashMap;

public interface ConfigWrite {
	void initial(String server, String username, String password, String localPath, String remotePath);
	void uploadConfigFile();
	void writetoConfigfile(HashMap hm);
}
