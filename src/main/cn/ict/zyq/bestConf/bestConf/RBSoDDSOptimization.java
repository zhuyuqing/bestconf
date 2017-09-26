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
	
	class MidParams{
		int currentround = 0;
		int subround = 0;
		double currentBest = -Double.MAX_VALUE;
		Instance currentIns =null;
		
		private HashMap saveMap = null;
		String midPath = resumeFolder+"/midParams.yaml";
		
		void loadFromFile(){
			try {
				saveMap = Yaml.loadType(new FileInputStream(midPath), HashMap.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			//now fill all members
			currentround = (Integer)saveMap.get("currentround");
			subround = (Integer)saveMap.get("subround");
			currentBest = (Double)saveMap.get("currentBest");
			
			String[] valStrs = ((String)saveMap.get("currentIns")).split("!!");
			if(valStrs.length==1 && valStrs[0].equals("null"))
				currentIns = null;
			else{
				currentIns = new DenseInstance(valStrs.length);
				for(int i=0;i<valStrs.length;i++){//the attributes must be in good order
					String[] parts = valStrs[i].split("!");
					currentIns.setValue(new Attribute(parts[0], i), Double.valueOf(parts[1]));
				}
			}
		}
		
		void saveToFile(){
			if(saveMap==null)
				saveMap = new HashMap<String,String>();
			
			saveMap.put("currentround", String.valueOf(currentround));
			saveMap.put("subround", String.valueOf(subround));
			saveMap.put("currentBest", String.valueOf(currentBest));
			
			String currentInsStr = "";
			if(currentIns!=null){
				for(int i=0;i<currentIns.numAttributes()-1;i++){
					currentInsStr+=currentIns.attribute(i).name()+"!"+currentIns.value(i)+"!!";
				}
				currentInsStr+=currentIns.attribute(currentIns.numAttributes()-1).name()+"!"+currentIns.value(currentIns.numAttributes()-1);
			}else
				currentInsStr="null";
			
			saveMap.put("currentIns", currentInsStr);
			
			writeToYaml(midPath, saveMap);
		}
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
	private void saveProps(ArrayList<Attribute> props, int round, int subround){
		try {
			HashMap propMap = new HashMap();
			for(Attribute att : props){
				propMap.put(att.name(),att.getMetadata().getProperty(propKey));
			}
			writeToYaml(resumeFolder+"/props_"+round+"_"+subround, propMap);
			
			File file = new File(resumeFolder+"/props_"+round+"_"+subround+"_OK");
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
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

	class ResumeParams{
		int propsRound=-1;
		int propsSubRound=-1;
		
		int trainingRound = -1;
		int trainingSubRound = -1;
		
		int samplePointRound = -1;
		int samplePointSubRound = -1;
		
		boolean isResuming = true;;
	}
	private ResumeParams resumePrepareTry(){
		ResumeParams retval = new ResumeParams();
		
		File rfolder = new File(resumeFolder);
		
		if(rfolder.isDirectory() && rfolder.listFiles().length>0){
			//load properties
			File[] propsFile = rfolder.listFiles(new PropsFileFilter());
			if(propsFile.length>0){
				File currentPropsFile = null;
				int maxRound = -1, maxSubRound = -1;
				String[] propsArr;
				for(int i=0;i<propsFile.length;i++){
					propsArr = propsFile[i].getName().split("_");
					if(maxRound<=Integer.valueOf(propsArr[1])){
						maxRound = Integer.valueOf(propsArr[1]);
						if(maxSubRound<Integer.valueOf(propsArr[2])){
							maxSubRound = Integer.valueOf(propsArr[2]);
							currentPropsFile = propsFile[i];
						}
					}
				}
				retval.propsRound = maxRound;
				retval.propsSubRound = maxSubRound;
				props = loadProps(currentPropsFile.getAbsolutePath().substring(0, currentPropsFile.getAbsolutePath().length()-3));//load the props
				
				//load samplePointsFile
				File[] ssFile = rfolder.listFiles(new SamplePointFileFilter());
				if(ssFile.length>0){
					maxRound = -1; maxSubRound = -1;
					File ssLoad = null;
					String[] ssArr;
					for(int i=0;i<ssFile.length;i++){
						ssArr = ssFile[i].getName().split("_");
						if(maxRound<=Integer.valueOf(ssArr[1])){
							maxRound = Integer.valueOf(ssArr[1]);
							if(maxSubRound<Integer.valueOf(ssArr[2])){
								maxSubRound = Integer.valueOf(ssArr[2]);
								ssLoad = ssFile[i];//the currentMax file to load
							}
						}
					}
					
					retval.samplePointRound = maxRound;
					retval.samplePointSubRound = maxSubRound;
					samplePoints = loadSamplePoints(ssLoad.getAbsolutePath().substring(0,ssLoad.getAbsolutePath().length()-3));
				}
				
				//load trainingSetFile
				File[] trainingFile = rfolder.listFiles(new TrainingSetFileFilter());
				if(trainingFile.length>0){
					maxRound = -1; maxSubRound = -1;
					File trainingLoad = null;
					String[] tsArr;
					for(int i=0;i<trainingFile.length;i++){
						//restore the allInstances
						if(bestconf.allInstances==null)
							bestconf.allInstances = loadTrainingSet(trainingFile[i].getAbsolutePath().substring(0,trainingFile[i].getAbsolutePath().length()-3));
						else
							bestconf.allInstances.addAll(loadTrainingSet(trainingFile[i].getAbsolutePath().substring(0,trainingFile[i].getAbsolutePath().length()-3)));
						
						tsArr = trainingFile[i].getName().split("_");
						if(maxRound<=Integer.valueOf(tsArr[1])){
							maxRound = Integer.valueOf(tsArr[1]);
							if(maxSubRound<Integer.valueOf(tsArr[2])){
								maxSubRound = Integer.valueOf(tsArr[2]);
								trainingLoad = trainingFile[i];//the currentMax file to load
							}
						}
					}
					
					retval.trainingRound = maxRound;
					retval.trainingSubRound = maxSubRound;
					trainingSet = loadTrainingSet(trainingLoad.getAbsolutePath().substring(0,trainingLoad.getAbsolutePath().length()-3));
				}
				
				//load parameters
				opParams = new MidParams();
				opParams.loadFromFile();
				if(opParams.currentIns!=null)
					opParams.currentIns.setDataset(bestconf.allInstances);
			}else
				retval.isResuming = false;
		}else
			retval.isResuming = false;

		//this is not a resuming process
		if(!retval.isResuming){
			if(rfolder.exists()){//a file with the same name exists
				//remove the file!!!
				rfolder.renameTo(new File(String.valueOf(System.nanoTime())));
			}
			rfolder.mkdirs();
			opParams = new MidParams();
			opParams.saveToFile();
		}
		
		return retval;
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
			//is it a global search
			if(samplePoints == null || rParams.propsRound<opParams.currentround){
				props = bestconf.getAttributes();
				saveProps(props, opParams.currentround, opParams.subround);//for resumability
				opParams.saveToFile();
			}
			
			if(opParams.currentround!=0 || opParams.subround!=0){
				if(!justAfterResume ||
						(justAfterResume && (rParams.samplePointRound<opParams.currentround || rParams.samplePointSubRound<opParams.subround))){
					//let's do the sampling
					((DDSSampler)sampler).setCurrentRound(opParams.currentround);
					samplePoints = sampler.getMultiDimContinuous(props, InitialSampleSetSize, false, bestconf);
					saveSamplePoints(samplePoints, opParams.currentround, opParams.subround);
				}
				
				if(!justAfterResume ||
						(justAfterResume && rParams.trainingRound<opParams.currentround || rParams.trainingSubRound<opParams.subround)){
					//traverse the set and initiate the experiments
					trainingSet = bestconf.runExp(samplePoints, opParams.currentround, "RRS"+String.valueOf(opParams.subround), justAfterResume);
					saveTrainingSet(trainingSet, opParams.currentround, opParams.subround);
				}
			}else{//(currentround==0 && subround==0)
				if(preLoadDatasetPath==null){
					
					if(samplePoints==null){
						//let's do the sampling
						((DDSSampler)sampler).setCurrentRound(opParams.currentround);
						samplePoints = sampler.getMultiDimContinuous(props, InitialSampleSetSize, false, bestconf);
						samplePoints.add(0, bestconf.defltSettings.firstInstance());
						saveSamplePoints(samplePoints, opParams.currentround, opParams.subround);
					}				
					if(trainingSet==null){
						//traverse the set and initiate the experiments
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
			
			//get the point with the best performance
			Instance tempIns = BestConf.findBestPerf(trainingSet);
			tempBest = tempIns.value(trainingSet.numAttributes()-1);
			if(tempBest>opParams.currentBest ||
					(justAfterResume && tempBest==opParams.currentBest && (rParams.propsRound<opParams.currentround || rParams.propsSubRound<opParams.subround))){
				System.err.println("Previous best is "+opParams.currentBest+"; Current best is "+tempBest);
				
				opParams.currentBest = tempBest;
				opParams.currentIns = tempIns;
				opParams.saveToFile();
				
				try {
					//output the best instance of this round
					Instances bestInstances = new Instances(samplePoints,1);
					bestInstances.add(opParams.currentIns);
					DataIOFile.saveDataToArffFile("data/trainingBestConf_RRS_"+opParams.currentround+"_"+opParams.subround+"_"+opParams.currentBest+".arff", bestInstances);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//let's search locally
				if(!justAfterResume ||
						(justAfterResume && rParams.propsRound<opParams.currentround || rParams.propsSubRound<opParams.subround)){
					props = ConfigSampler.scaleDownDetour(trainingSet, tempIns);
					saveProps(props, opParams.currentround, opParams.subround);//for resumability
				}
				
				opParams.subround++;
				opParams.saveToFile();
			}else{//let's do the restart
				samplePoints = null;
				
				opParams.currentround++; opParams.subround = 0;
				opParams.saveToFile();
				
				System.err.println("Entering into round "+opParams.currentround);
				/*if(opParams.currentround>=RRSMaxRounds)
					break;*/
			}
			
			justAfterResume = false;
		}//RRS search
		
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.err.println("We are ending the optimization experiments!");
		System.err.println("Please wait and don't shutdown!");
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		//output the best
		Map<Attribute,Double> attsmap = BestConf.instanceToMap(opParams.currentIns);
		System.out.println(attsmap.toString());

		//set the best configuration to the cluster
		System.err.println("The best performance is : "+opParams.currentBest);
		
		System.out.println("=========================================");
		TxtFileOperation.writeToFile("bestConfOutput_RRS",attsmap.toString()+"\n");
		
		System.out.println("=========================================");
		
		//output the whole trainings dataset
		try {
			DataIOFile.saveDataToArffFile("data/trainingAllRSS.arff", bestconf.allInstances);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
