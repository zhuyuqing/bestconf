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
import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;

import java.util.HashMap;

public class HadoopConfigReadin implements ConfigReadin {
	
	public HadoopConfigReadin(){
		
	}
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		
	}

	@Override
	public void downLoadConfigFile(String fileName) {
		
	}

	@Override
	public HashMap modifyConfigFile(HashMap hm, String filepath) {
		return null;
	}

	@Override
	public HashMap modifyConfigFile(HashMap hm) {
		return null;
	}
}
