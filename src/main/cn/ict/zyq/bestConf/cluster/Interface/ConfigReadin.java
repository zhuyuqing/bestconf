package cn.ict.zyq.bestConf.cluster.Interface;

import java.util.HashMap;

public interface ConfigReadin {
	void initial(String server, String username, String password, String localPath, String remotePath);
	void downLoadConfigFile(String fileName);
	HashMap modifyConfigFile(HashMap hm, String filepath);
	HashMap modifyConfigFile(HashMap hm);
}
