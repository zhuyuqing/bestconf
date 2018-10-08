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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.ho.yaml.Yaml;

import cn.ict.zyq.bestConf.COMT2.COMT2;
import cn.ict.zyq.bestConf.COMT2.UnlabledTrainingInstanceGen2;
import cn.ict.zyq.bestConf.bestConf.optimizer.Optimization;
import cn.ict.zyq.bestConf.bestConf.sysmanipulator.ClusterManager;
import cn.ict.zyq.bestConf.cluster.Main.AutoTestAdjust;
import cn.ict.zyq.bestConf.cluster.Utils.PropertiesUtil;
import cn.ict.zyq.bestConf.util.DataIOFile;
import cn.ict.zyq.bestConf.util.LHSInitializer;

import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;
import weka.attributeSelection.PrincipalComponents;

import cn.ict.zyq.bestConf.cluster.Main.AutoTestAdjust;

public class BestConf {
	
	private static String bestconfConfig = "data/bestconf.properties";
	
	private static String configPath = "data/SUTconfig.properties";
	private static String configPathProp = "configPath";
	private static String yamlPath = "data/defaultConfig.yaml";
	private static String yamlPathProp = "yamlPath";
	
	private static int InitialSampleSetSize = 50;
	private static String InitialSampleSetSizeProp = "InitialSampleSetSize";
	
	private static int RRSMaxRounds = 5;
	private static String RRSMaxRoundsProp = "RRSMaxRounds";
	
	private static int COMT2Iteration = 20;
	private static String COMT2IterationProp = "COMT2Iteration";
	private static int COMT2MultiIteration = 10;
	private static String COMT2MultiIterationProp = "COMT2MultiIteration";
	
	private static String PerformanceAttName = "performance";
	
	private ArrayList<Attribute> atts=null;
	protected Instances defltSettings = null;
	protected Instances allInstances = null;//the first instance should be the first instance of defltSetting as well
	private ClusterManager manager; 
	
	public BestConf(){
		//load properties
		try {
			Properties pps = PropertiesUtil.GetAllProperties(bestconfConfig);
			
			if(pps.getProperty(configPathProp)!=null)
				configPath = pps.getProperty(configPathProp);
			if(pps.getProperty(yamlPathProp)!=null)
				yamlPath = pps.getProperty(yamlPathProp);
			
			if(pps.getProperty(InitialSampleSetSizeProp)!=null)
				InitialSampleSetSize = Integer.valueOf(pps.getProperty(InitialSampleSetSizeProp));
			if(pps.getProperty(RRSMaxRoundsProp)!=null)
				RRSMaxRounds = Integer.valueOf(pps.getProperty(RRSMaxRoundsProp));
			if(pps.getProperty(COMT2IterationProp)!=null)
				COMT2Iteration = Integer.valueOf(pps.getProperty(COMT2IterationProp));
			if(pps.getProperty(COMT2MultiIterationProp)!=null)
				COMT2MultiIteration = Integer.valueOf(pps.getProperty(COMT2MultiIterationProp));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//manager = new AutoTestAdjust(configPath);
		manager = new AutoTestAdjust(configPath);//new ClusterManagerStub();//
		//let's load the properties
		defltSettings = loadPropertiesAsInstances(yamlPath);
	}
	
	public void afterOptimization(){
		manager.shutdown();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param attributeToVal
	 * @return
	 */
	protected double setOptimal(Map<Attribute,Double> attributeToVal, String postfix){
		double y = manager.setOptimal(attributeToVal);
		
		//we output the result set for future debugging and testing purposes
		try {
			for(Map.Entry<Attribute, Double> ent : attributeToVal.entrySet()){
				PropertiesUtil.WriteProperties("data/trainingBestConfBest"+postfix+".properties", ent.getKey().name(), String.valueOf(ent.getValue()));
			}
			PropertiesUtil.WriteProperties("data/trainingBestConfBest"+postfix+".properties", "performance", String.valueOf(y));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return y;
	}
	
	private static String perfAttName = "performance";
	protected Instances runExp(Instances samplePoints, int round, String postfix, boolean resuming) {
		Instances retval = null;
		try {
			//DataIOFile.saveDataToArffFile("data/zyqTestRange.arff", samplePoints);
			
			if(resuming){
				samplePoints = manager.collectPerfs(samplePoints, perfAttName);
			}
			
			retval = manager.runExp(samplePoints, perfAttName);
			//we output the result set for future debugging and testing purposes
			DataIOFile.saveDataToArffFile("data/trainingBestConf"+round+"_"+postfix+".arff", samplePoints);
			
			//evict all bad configurations
			Attribute perfAtt = retval.attribute(perfAttName);
			Iterator<Instance> itr = retval.iterator();
			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			Instance next;
			while(itr.hasNext()){
				next = itr.next();
				if(next.value(perfAtt)==-1)
					toRemove.add(retval.indexOf(next));
			}
			while(!toRemove.isEmpty())
				retval.remove(toRemove.remove(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(allInstances==null){
			allInstances = new Instances(retval);
		}else{
			allInstances.addAll(retval);
		}
		
		return retval;
	}
	
	public ArrayList<Attribute> getAttributes(){
		return atts;
	}
	
	//initiate the attribute set, we only count those numeric properties
	//	 we initiate the range to be [(1-50%)*default,(1-50%)*default]
	public Instances loadPropertiesAsInstancesPre(String Path){
		HashMap<String,String> pmap = null;
		try {
			pmap = Yaml.loadType(new FileInputStream(yamlPath), HashMap.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		atts = new ArrayList<Attribute>();
		Instance dfIns = new DenseInstance(pmap.size());
		int pos = 0;
		double[] vals = new double[pmap.size()];
		for(Map.Entry<String,String> ent : pmap.entrySet()){
			try{
				double val = Double.valueOf(String.valueOf(ent.getValue()));
				vals[pos] = val;
				
				Properties p1 = new Properties();
				double upper, lower;
				if(val!=0){
					upper = val*(1.+0.5);
					lower = val*(1.-0.5);
				}else{
					lower = val;
					upper = 1;
				}
				
				p1.setProperty("range", "["+String.valueOf(lower)+","+String.valueOf(upper)+"]");
				ProtectedProperties prop1 = new ProtectedProperties(p1);
				
				atts.add(new Attribute(String.valueOf(ent.getKey()), prop1));
				pos++;
			}catch(Exception e){
			}
		}
		
		Instances dfProp = new Instances("DefaultConfig", atts, 1);
		dfProp.add(dfIns);
		dfIns.setDataset(dfProp);
		for(int i=0;i<pos;i++){
			dfIns.setValue(atts.get(i), vals[i]);
			//System.err.println(atts.get(i)+":"+vals[i]);
		}
		
		return dfProp;
	}
	
	//initiate the attribute set, we only count those numeric properties
		//	 we initiate the range to be [(1-50%)*default,(1-50%)*default]
		public Instances loadPropertiesAsInstances(String Path){
			HashMap<String,String> pmap = null;
			HashMap rangeMap = null;
			try {
				pmap = Yaml.loadType(new FileInputStream(yamlPath), HashMap.class);
				rangeMap = Yaml.loadType(new FileInputStream(yamlPath+"_range"), HashMap.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			atts = new ArrayList<Attribute>();
			int pos = 0;
			double[] vals = new double[pmap.size()];
			Object range = null;
			for(Map.Entry<String,String> ent : pmap.entrySet()){
				try{
					double val = Double.valueOf(String.valueOf(ent.getValue()));
					vals[pos] = val;
					
					Properties p1 = new Properties();
					
					range =  rangeMap.get(ent.getKey());
					if(range!=null){
						String list = (String)range;
						if(list.indexOf('[')==-1 && list.indexOf('(')==-1)
							throw new Exception("No Range for You"+ent.getKey());
						p1.setProperty("range", list.trim());
					}else{
						double upper, lower;
						if(val!=0){
							upper = val*(1.+0.5);
							lower = val*(1.-0.5);
						}else{
							lower = val;
							upper = 1;
						}
						p1.setProperty("range", "["+String.valueOf(lower)+","+String.valueOf(upper)+"]");
					}
					
					ProtectedProperties prop1 = new ProtectedProperties(p1);
					
					atts.add(new Attribute(String.valueOf(ent.getKey()), prop1));
					pos++;
				}catch(Exception e){
				}
			}
			
			Instances dfProp = new Instances("DefaultConfig", atts, 1);
			Instance dfIns = new DenseInstance(atts.size());
			for(int i=0;i<pos;i++){
				dfIns.setValue(atts.get(i), vals[i]);
				//System.err.println(atts.get(i)+":"+vals[i]);
			}
			dfProp.add(dfIns);
			dfIns.setDataset(dfProp);
			
			return dfProp;
		}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param args
	 * 				2 for COMT2
	 * 				3 for COMT2Multi
	 * 				0 for RRS
	 * 				1 for RRS with pooled points
	 * 				4 for RRS over COMT2 model
	 * 				others for a test
	 */
	public static void main(String[] args) {
		int method = 0;
		String preLoadDataPath = null;
		if(args.length>0){
			method = Integer.valueOf(args[0]);
			if(args.length>1){
				BestConf.bestconfConfig = args[1];
				if(args.length>2)
					preLoadDataPath = args[2];
			}
		}
		
		startOneTest(method, preLoadDataPath);
	}
	
	public static void startOneTest(int method, String preLoadDataPath){
		BestConf bestconf = new BestConf();
		Optimization opt;
		switch(method){
		case 0:
		default:
			opt = new RBSoDDSOptimization(bestconf, BestConf.InitialSampleSetSize, BestConf.RRSMaxRounds);
			break;
		}
		
		if(opt!=null)
			opt.optimize(preLoadDataPath);
		else
			bestconf.test();
		bestconf.afterOptimization();
	}
	
	public void test(){
		try {
			System.out.println("Testing loading data and writing data.......................");
			DataIOFile.saveDataToXrffFile("data/initialTestOnIO.xrff", DataIOFile.loadDataFromArffFile("data/train.arff"));
			Thread.sleep(1000);
			
			System.out.println("Testing cluster manipulation functions.......................");
			manager.test(2);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
//	1. find the nearest neighbor in each dimension; 2. update the sampling range
	public static ArrayList<Attribute> scaleDownDetour(Instances previousSet, Instance center){
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
					minDis = Math.min((double)((int)(Math.abs(ins.value(i)-center.value(i))*100))/100.0, minDis);
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
	
	public static Instance findBestPerf(Instances data){
		int idx = data.numAttributes()-1;
		double bestPerf = data.attributeStats(idx).numericStats.max;
		for(int i=0;i<data.numInstances();i++)
			if(data.get(i).value(idx)==bestPerf)
				return data.get(i);
		return null;//should never return NULL
	}
	
	public static int findBestPerfIndex(Instances data){
		int idx = data.numAttributes()-1;
		double bestPerf = data.attributeStats(idx).numericStats.max;
		for(int i=0;i<data.numInstances();i++)
			if(data.get(i).value(idx)==bestPerf)
				return i;
		return -1;//should never return -1
	}
	
	public static Map<Attribute, Double> instanceToMap(Instance ins){
		HashMap<Attribute, Double> retval = new HashMap<Attribute, Double>();
		Enumeration<Attribute> enu = ins.enumerateAttributes();
		while(enu.hasMoreElements()){
			Attribute temp = enu.nextElement();
			retval.put(temp, ins.value(temp));
		}
		return retval;
	}
	
	/**
	 * remove all linearly related attributes
	 * @return the set of attributes that are linearly related to the clase attributes
	 */
	private static double correlationFactorThreshold = 0.8;
	public static ArrayList<String> preprocessInstances(Instances retval){
		double[][] cMatrix;
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> deleteAttNames = new ArrayList<String>();
		PrincipalComponents pc = new PrincipalComponents();
		HashMap<Integer, ArrayList<Integer>> filter = new HashMap<Integer, ArrayList<Integer>>();
		try {
			pc.buildEvaluator(retval);
			cMatrix = pc.getCorrelationMatrix();		
			for(int i = 0; i < cMatrix.length; i++){
				ArrayList<Integer> record = new ArrayList<Integer>();
				for(int j = i + 1; j < cMatrix.length; j++)
					if(cMatrix[i][j] >= correlationFactorThreshold || cMatrix[i][j] <= -correlationFactorThreshold){
						record.add(j);
					}
				if(record.size() != 0){
					filter.put(i, record);
				}
			}
			Iterator<Map.Entry<Integer, ArrayList<Integer>>> iter = filter.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, ArrayList<Integer>> entry = iter.next();
				ArrayList<Integer> arr = entry.getValue();
				for(int i = 0; i < arr.size(); i++)
					if(arr.get(i) != cMatrix.length - 1 && !deleteAttNames.contains(retval.attribute(arr.get(i)).name())){
						deleteAttNames.add(retval.attribute(arr.get(i)).name());
					}
				if(arr.contains(cMatrix.length-1)){
					result.add(retval.attribute(Integer.parseInt(entry.getKey().toString())).name());
				}
			}
			for(int i = 0; i < deleteAttNames.size(); i++){
				retval.deleteAttributeAt(retval.attribute(deleteAttNames.get(i)).index());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void signalByFile(int method){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("data/"+System.currentTimeMillis()+"_"+method+"_finished______________"));
			bw.write(System.currentTimeMillis()+"\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	class COMT2Gen extends UnlabledTrainingInstanceGen2 {
		public Instances generateMore(int number, int existedNum,
				Instances header) {
			ArrayList<Attribute> localAtts = new ArrayList<Attribute>();
			Enumeration<Attribute> enu = header.enumerateAttributes();
			while (enu.hasMoreElements()) {
				localAtts.add(enu.nextElement());
			}
			Instances samplePoints = LHSInitializer.getMultiDimContinuous(
					localAtts, number + existedNum, false);
			samplePoints.insertAttributeAt(header.classAttribute(),
					samplePoints.numAttributes());
			samplePoints.setClassIndex(samplePoints.numAttributes() - 1);
			return samplePoints;
		}
	}
	
	public static void testCOMT2() throws Exception{
		BestConf bestconf = new BestConf();
		Instances trainingSet = DataIOFile.loadDataFromArffFile("data/trainingBestConf0.arff");
		trainingSet.setClassIndex(trainingSet.numAttributes()-1);
		
		Instances samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize, false);
		samplePoints.insertAttributeAt(trainingSet.classAttribute(), samplePoints.numAttributes());
		samplePoints.setClassIndex(samplePoints.numAttributes()-1);
		
		COMT2 comt = new COMT2(samplePoints, COMT2Iteration);
		
		comt.buildClassifier(trainingSet);
		
		Evaluation eval = new Evaluation(trainingSet);
		eval.evaluateModel(comt, trainingSet);
		System.err.println(eval.toSummaryString());
		
		Instance best = comt.getInstanceWithPossibleMaxY(samplePoints.firstInstance());
		Instances bestInstances = new Instances(trainingSet,2);
		bestInstances.add(best);
		DataIOFile.saveDataToXrffFile("data/trainingBestConf_COMT2.arff", bestInstances);
		
		//now we output the training set with the class value updated as the predicted value
		Instances output = new Instances(trainingSet, trainingSet.numInstances());
		Enumeration<Instance> enu = trainingSet.enumerateInstances();
		while(enu.hasMoreElements()){
			Instance ins = enu.nextElement();
			double[] values = ins.toDoubleArray();
			values[values.length-1] = comt.classifyInstance(ins);
			output.add(ins.copy(values));
		}
		DataIOFile.saveDataToXrffFile("data/trainingBestConf0_predict.xrff", output);
	}
	
	public static void getBestPerfFrom(String path){
		try {
			BestConf bestconf = new BestConf();
			Instances trainingSet = DataIOFile.loadDataFromArffFile(path);
			Instance best = trainingSet.firstInstance();
			//set the best configuration to the cluster
			Map<Attribute,Double> attsmap = new HashMap<Attribute,Double>();
			for(int i=0;i<best.numAttributes()-1;i++){
				attsmap.put(best.attribute(i), best.value(i));
			}
	
			double bestPerf = bestconf.setOptimal(attsmap, "getBestPerfFrom");
			System.out.println("=========================================");
			System.err.println("The actual performance for the best point is : "+bestPerf);
			System.out.println("=========================================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
