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
package cn.ict.zyq.bestConf.bestConf.sysmanipulator;

import java.util.Map;

import weka.core.Attribute;
import weka.core.Instances;

public interface ClusterManager {
	public void shutdown();
	public void test(int timeToTest);
	
	//only run those instance without the performance attribute
	public Instances runExp(Instances samplePoints, String perfAttName);
	public double setOptimal(Map<Attribute,Double> attributeToVal);
	
	/**collect the performances for part of samplePoints*/
	//fill the performance attribute of samplePoints
	public Instances collectPerfs(Instances samplePoints, String perfAttName);
}
