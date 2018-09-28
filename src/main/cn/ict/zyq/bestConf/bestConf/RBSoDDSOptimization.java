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
package cn.ict.zyq.bestConf.bestConf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.ho.yaml.Yaml;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;
import cn.ict.zyq.bestConf.bestConf.optimizer.Optimization;
import cn.ict.zyq.bestConf.bestConf.sampler.ConfigSampler;
import cn.ict.zyq.bestConf.bestConf.sampler.DDSSampler;
import cn.ict.zyq.bestConf.bestConf.sampler.LHSSampler;
import cn.ict.zyq.bestConf.util.DataIOFile;
import cn.ict.zyq.bestConf.util.TxtFileOperation;

public class RBSoDDSOptimization implements Optimization {
	
	private BestConf bestconf;
	
	private int InitialSampleSetSize;
	
	private int RRSMaxRounds;
	
	private ConfigSampler sampler;//LHSSampler();//GriddingSampler();//
	
	////////////////////////////////////////////////////////
	///////for a resumable optimization process/////////////
	private static final String resumeFolder = "data/rlogs";
	
	private MidParams opParams;
	////////////////////////////////////////////////////////
	
	public RBSoDDSOptimization(BestConf bestconf, int InitialSampleSetSize, int RRSMaxRounds){
		this.bestconf = bestconf;
		this.InitialSampleSetSize = InitialSampleSetSize; 
		this.RRSMaxRounds = RRSMaxRounds;
		
		this.sampler = new DDSSampler(this.RRSMaxRounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void saveTrainingSet(Instances trainingSet, int round, int subround){
		try {
			DataIOFile.saveDataToArffFile(resumeFolder+"/training_"+round+"_"+subround+"_.arff", samplePoints);
			
			File file = new File(resumeFolder+"/training_"+round+"_"+subround+"_.arff"+"_OK");
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Instances loadTrainingSet(String path){
		try {
			return DataIOFile.loadDataFromArffFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	class TrainingSetFileFilter implements FileFilter{
		@Override
		public boolean accept(File file) {
			if(file.getName().indexOf("training_")!=-1 && file.getName().indexOf("OK")!=-1)
				return true;
			return false;
		}
	}
	
	private void saveSamplePoints(Instances samplePoints, int round, int subround){
		try {
			DataIOFile.saveDataToArffFile(resumeFolder+"/samples_"+round+"_"+subround+"_.arff", samplePoints);
			
			File file = new File(resumeFolder+"/samples_"+round+"_"+subround+"_.arff"+"_OK");
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Instances loadSamplePoints(String path){
		try {
			return DataIOFile.loadDataFromArffFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	class SamplePointFileFilter implements FileFilter{
		@Override
		public boolean accept(File file) {
			if(file.getName().indexOf("samples_")!=-1 && file.getName().indexOf("OK")!=-1)
				return true;
			return false;
		}
	}
	
	private static String propKey = "range";
	class PropsFileFilter implements FileFilter{
		@Override
		public boolean accept(File file) {
			if(file.getName().indexOf("props")!=-1 && file.getName().indexOf("OK")!=-1)
				return true;
			return false;
		}
	}
	private ArrayList<Attribute> loadProps(String path){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		try {
			HashMap<String,ArrayList> rangeMap = Yaml.loadType(new FileInputStream(path), HashMap.class);
			
			for(Map.Entry<String,ArrayList> ent : rangeMap.entrySet()){
				try{
					Properties p1 = new Properties();
					p1.setProperty("range", rangeArrayToStr(ent.getValue()));
					ProtectedProperties prop1 = new ProtectedProperties(p1);
					atts.add(new Attribute(String.valueOf(ent.getKey()), prop1));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return atts;
	}
	private String rangeArrayToStr(ArrayList list){
		String retval="";
		retval="["+list.get(0)+","+list.get(1)+"]";
		return retval;
	}
	
	public void writeToYaml(String path, HashMap target){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
				
			Iterator iter = target.entrySet().iterator();
			 while(iter.hasNext()){
		        Map.Entry entry = (Map.Entry) iter.next();
		        Object key = entry.getKey();
		        Object val = entry.getValue();       
				bw.write(key + ": "+ val + "\n" );
			 }
			 bw.close();
		}catch(IOException e){
		}	
	}
	
	private ArrayList<Attribute> props = null;
	Instances samplePoints = null, trainingSet = null;
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void optimize(String preLoadDatasetPath) {
		ResumeParams rParams = resumePrepareTry();
		boolean justAfterResume = rParams.isResuming;
		
		//detect whether we need to resume
		if(rParams.isResuming)
			preLoadDatasetPath = null;
		
		double tempBest;
		
		while(opParams.currentround<RRSMaxRounds){
			if(samplePoints == null || rParams.propsRound<opParams.currentround){
				props = bestconf.getAttributes();
			}
			
			if(opParams.currentround!=0 || opParams.subround!=0){
				if(!justAfterResume){
					((DDSSampler)sampler).setCurrentRound(opParams.currentround);
					samplePoints = sampler.getMultiDimContinuous(props, InitialSampleSetSize, false, bestconf);
					saveSamplePoints(samplePoints, opParams.currentround, opParams.subround);
				}
				
				if(!justAfterResume ||
						(justAfterResume && rParams.trainingRound<opParams.currentround || rParams.trainingSubRound<opParams.subround)){
					trainingSet = bestconf.runExp(samplePoints, opParams.currentround, "RRS"+String.valueOf(opParams.subround), justAfterResume);
					saveTrainingSet(trainingSet, opParams.currentround, opParams.subround);
				}
			}else{
				if(preLoadDatasetPath==null){
					
					if(samplePoints==null){
						samplePoints = sampler.getMultiDimContinuous(props, InitialSampleSetSize, false, bestconf);
						samplePoints.add(0, bestconf.defltSettings.firstInstance());
						saveSamplePoints(samplePoints, opParams.currentround, opParams.subround);
					}				
					if(trainingSet==null){
						trainingSet = bestconf.runExp(samplePoints, opParams.currentround, "RRS"+String.valueOf(opParams.subround), justAfterResume);
						saveTrainingSet(trainingSet, opParams.currentround, opParams.subround);
					}
				}else{
					try {
						bestconf.allInstances = DataIOFile.loadDataFromArffFile(preLoadDatasetPath);
						bestconf.allInstances.setClassIndex(bestconf.allInstances.numAttributes()-1);
						samplePoints = trainingSet = new Instances(bestconf.allInstances);
						
						saveSamplePoints(samplePoints, opParams.currentround, opParams.subround);
						saveTrainingSet(trainingSet, opParams.currentround, opParams.subround);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			Instance tempIns = BestConf.findBestPerf(trainingSet);
			tempBest = tempIns.value(trainingSet.numAttributes()-1);
			if(tempBest>opParams.currentBest){
				
				opParams.currentBest = tempBest;
				opParams.currentIns = tempIns;
				
				try {
					Instances bestInstances = new Instances(samplePoints,1);
					bestInstances.add(opParams.currentIns);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(!justAfterResume ||
						(justAfterResume && rParams.propsRound<opParams.currentround || rParams.propsSubRound<opParams.subround)){
					props = ConfigSampler.scaleDownDetour(trainingSet, tempIns);
				}
				
				opParams.subround++;
			}else{
			}
		}
		
		try {
			DataIOFile.saveDataToArffFile("data/trainingAllRSS.arff", bestconf.allInstances);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
