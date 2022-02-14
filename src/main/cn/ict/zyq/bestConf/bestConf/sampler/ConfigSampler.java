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
package cn.ict.zyq.bestConf.bestConf.sampler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import cn.ict.zyq.bestConf.bestConf.BestConf;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public abstract class ConfigSampler {
	
	private static String PerformanceAttName = "performance";
	private static int scaleDownChoice = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	abstract Instances sampleMultiDimContinuous(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid);
	
	public Instances getMultiDimContinuous(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid, BestConf bestconf){
		Instances retval = sampleMultiDimContinuous(atts, sampleSetSize, useMid), temp;
		while(retval.size()<sampleSetSize){
			temp = sampleMultiDimContinuous(atts, sampleSetSize, useMid);
			retval.addAll(temp);
		}
		
		//make sure the set size is equal to the setting
		while(retval.size()>sampleSetSize)
			retval.remove(retval.size()-1);
		
		for (int i = 0; i < retval.size(); i++) {
			long device_number = (long)retval.get(i).value(2);
			long group_number = (long)retval.get(i).value(3);
			long memtable_size_threshold = 15_461_882_260l / group_number;
			retval.get(i).setValue(1, memtable_size_threshold);
			retval.get(i).setValue(0, memtable_size_threshold * 4);
			if (device_number <= group_number) {
				retval.get(i).setValue(2, group_number);
			}
		}
		
		return retval;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void setScaleDownChoice(int val){
		scaleDownChoice = val;
	}
	
	public static ArrayList<Attribute> scaleDownDetour(Instances previousSet, Instance center){
		switch(scaleDownChoice){
		case 0:
			return scaleDownMindists(previousSet,center);
		default:
			return scaleDownNeighbordists(previousSet,center);
		}
	}
	
	// 1. find the nearest neighbor in each dimension; 2. update the sampling range
	private static ArrayList<Attribute> scaleDownNeighbordists(Instances previousSet, Instance center){
		ArrayList<Attribute> localAtts = new ArrayList<Attribute>();
		int attNum = center.numAttributes();
		
		int pos = -1;
		if(previousSet.attribute(PerformanceAttName)!=null)
			pos = previousSet.attribute(PerformanceAttName).index();
		
		//traverse each dimension
		Enumeration<Instance> enu;
		double[] minDists = new double[2];
		double val;
		for(int i=0;i<attNum;i++){
			if(i==pos)
				continue;
			
			enu = previousSet.enumerateInstances();
			minDists[0] = 1-Double.MAX_VALUE;
			minDists[1] = Double.MAX_VALUE;
			
			while(enu.hasMoreElements()){
				Instance ins = enu.nextElement();
				if(!ins.equals(center)){
					val = ins.value(i)-center.value(i);
					if(val<0)
						minDists[0] = Math.max((double)((int)((ins.value(i)-center.value(i))*1000))/1000.0, minDists[0]);
					else
						minDists[1] = Math.min((double)((int)((ins.value(i)-center.value(i))*1000))/1000.0, minDists[1]);
				}
			}
			
			//now we set the range
			Properties p1 = new Properties();
			double upper = center.value(i)+minDists[1], lower=center.value(i)+minDists[0];
			
			TreeSet<Double> detourSet = new TreeSet<Double>();
			detourSet.add(upper);
			detourSet.add(lower);
			detourSet.add(previousSet.attribute(i).getUpperNumericBound());
			detourSet.add(previousSet.attribute(i).getLowerNumericBound());
			switch(detourSet.size()){
			case 1:
				upper=lower=detourSet.first();
				break;
			case 2:
				upper = detourSet.last();
				lower = detourSet.first();
				break;
			case 3:
				upper=lower=detourSet.higher(detourSet.first());
				break;
			default://case 4:
				upper=detourSet.lower(detourSet.last());
				lower=detourSet.higher(detourSet.first());
				break;
			}
			
			p1.setProperty("range", "["+String.valueOf(lower)+","+String.valueOf(upper)+"]");
			ProtectedProperties prop1 = new ProtectedProperties(p1);
			
			localAtts.add(new Attribute(previousSet.attribute(i).name(), prop1));
		}
		
		return localAtts;
	}
 
    // 1. find the nearest neighbor in each dimension; 2. update the sampling range
	private static ArrayList<Attribute> scaleDownMindists(Instances previousSet, Instance center){
		ArrayList<Attribute> localAtts = new ArrayList<Attribute>();
		int attNum = center.numAttributes();
		
		int pos = previousSet.attribute(PerformanceAttName).index();
		
		//traverse each dimension
		Enumeration<Instance> enu;
		double minDis;
		for(int i=0;i<attNum;i++){
			if(i==pos)
				continue;
			
			enu = previousSet.enumerateInstances();
			minDis = Double.MAX_VALUE;
			
			while(enu.hasMoreElements()){
				Instance ins = enu.nextElement();
				if(!ins.equals(center))
					minDis = Math.min((double)((int)(Math.abs(ins.value(i)-center.value(i))*1000))/1000.0, minDis);
			}
			
			//now we set the range
			Properties p1 = new Properties();
			double upper = center.value(i)+minDis, lower=center.value(i)-minDis;
			
			TreeSet<Double> detourSet = new TreeSet<Double>();
			detourSet.add(upper);
			detourSet.add(lower);
			detourSet.add(previousSet.attribute(i).getUpperNumericBound());
			detourSet.add(previousSet.attribute(i).getLowerNumericBound());
			switch(detourSet.size()){
			case 1:
				upper=lower=detourSet.first();
				break;
			case 2:
				upper = detourSet.last();
				lower = detourSet.first();
				break;
			case 3:
				upper=lower=detourSet.higher(detourSet.first());
				break;
			default://case 4:
				upper=detourSet.lower(detourSet.last());
				lower=detourSet.higher(detourSet.first());
				break;
			}
			
			p1.setProperty("range", "["+String.valueOf(lower)+","+String.valueOf(upper)+"]");
			ProtectedProperties prop1 = new ProtectedProperties(p1);
			
			localAtts.add(new Attribute(previousSet.attribute(i).name(), prop1));
		}
		
		return localAtts;
	}
}

	
