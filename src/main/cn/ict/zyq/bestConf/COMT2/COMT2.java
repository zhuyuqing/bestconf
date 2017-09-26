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
package cn.ict.zyq.bestConf.COMT2;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Range;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.m5.PreConstructedLinearModel;
import weka.classifiers.trees.m5.RuleNode;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class COMT2 implements Classifier {
	
	private static final int ModelNum = 2;
	
	private Instances labeledInstances, unlabeledInstances, unlabeldPool;
	private M5P[] models;
	private int[] M = {3,7};//Minimal number of examples in the leaf of model trees
	
	private int comtIterations;
	private int indexOfClass;
	private Random rand;
	
	public COMT2(Instances unlabeledIns, int numOfIterations){
		rand = new Random();
		
		this.unlabeldPool = unlabeledIns;
		this.comtIterations = numOfIterations;
		
		int initialPool = this.unlabeldPool.size()/this.comtIterations;
		this.unlabeledInstances = new Instances(this.unlabeldPool,0);
		for(int i=0;i<initialPool;i++)
			this.unlabeledInstances.add(this.unlabeldPool.remove(rand.nextInt(this.unlabeldPool.size())));
		indexOfClass = this.unlabeledInstances.classIndex();
		
		models = null;
	}

	/**
	 * @param samplePoint : some attributes are flexible; for such attributes, we use values of the samplepoint
	 * @return
	 * @throws Exception 
	 */
	public Instance getInstanceWithPossibleMaxY(Instance samplePoint) throws Exception{
		Instance retval = null;
		
		//we actually have the model
		if(models!=null){
			ArrayList<Branch2>[] branchLists = new ArrayList[ModelNum];
			for(int m=0;m<ModelNum;m++){
				branchLists[m] = getLeavesInfoForM5P(models[m]);
			}
			
			//now we intersect each leaf
			ArrayList<Branch2> combined = branchLists[0];
			for(int m=1;m<ModelNum;m++){
				combined = intersectBranch2Lists(combined, branchLists[m]);
			}
			
			//now we find the best in the combined list
			Instance temp;
			for(Branch2 branch : combined){
				temp = branch.maxPoint(samplePoint.dataset());
				if(retval==null || retval.classValue()<temp.classValue()){
					retval = temp;
					System.out.println("Current best performance is : "+retval.classValue());
				}
			}
		}
		return retval;
	}
	
	private ArrayList<Branch2> intersectBranch2Lists(ArrayList<Branch2> list1, ArrayList<Branch2> list2){
		ArrayList<Branch2> retval = new ArrayList<Branch2>();
		
		//generally, a new branch for each br1*br2 combination, 
		//		but things get changed when they have specifications on the same attributes
		for(Branch2 br1 : list1){
			for(Branch2 br2 : list2){
				HashMap<Attribute,Range<Double>> map1 = br1.getRangeMap();
				HashMap<Attribute,Range<Double>> map2 = br2.getRangeMap();
				ArrayList<Attribute> intersectAtt = new ArrayList<Attribute>();
				for(Attribute att : map1.keySet())
					if(map2.containsKey(att))
						intersectAtt.add(att);
				
				//no intersection, attributes added; with intersection, decide accordingly
				Branch2 toAdd = null;
				if(intersectAtt.size()==0){
					toAdd = new Branch2((HashMap<Attribute,Range<Double>>)map1.clone(), 
							(ArrayList<PreConstructedLinearModel>)br1.getLinearModels().clone());
					
					//we add models and attributes from br2
					toAdd.getLinearModels().addAll(br2.getLinearModels());
					toAdd.getRangeMap().putAll(map2);
				}else{
					//now we check each intersection
					ArrayList<Range<Double>> intersectRanges = new ArrayList<Range<Double>>();
					for(Attribute att : intersectAtt){
						try{
							intersectRanges.add(map1.get(att).intersection(map2.get(att)));
						}catch(IllegalArgumentException e){
							//nothing happened if no intersection existed
						}
					}//traverse the intersected attributes
					
					//add the branch only when all intersected attributes have intersected ranges
					if(intersectRanges.size()==intersectAtt.size()){
						toAdd = new Branch2((HashMap<Attribute,Range<Double>>)map1.clone(), 
								(ArrayList<PreConstructedLinearModel>)br1.getLinearModels().clone());
						
						//we add models and attributes from br2
						toAdd.getLinearModels().addAll(br2.getLinearModels());
						toAdd.getRangeMap().putAll(map2);
						
						//now we update the rangemap
						for(int i=0;i<intersectAtt.size();i++){
							toAdd.getRangeMap().put(intersectAtt.get(i), intersectRanges.get(i));
						}
					}//else not more work is needed
				}//attribute intersection?
				
				//add a new branch when needed
				if(toAdd!=null)
					retval.add(toAdd);
			}
		}
		
		return retval;
	}
	
	private ArrayList<Branch2> getLeavesInfoForM5P(M5P model){
		ArrayList<Branch2> retval = new ArrayList<Branch2>();
		ArrayList<RuleNode> leafNodes = new ArrayList<RuleNode>();
		model.getM5RootNode().returnLeaves(new ArrayList[]{leafNodes});
		
		for(RuleNode leaf : leafNodes){
			Branch2 branch = new Branch2();
			ArrayList<PreConstructedLinearModel> lmodel = new ArrayList<PreConstructedLinearModel>();
			lmodel.add(leaf.getModel());
			branch.setLinearModels(lmodel);
			
			Map<Attribute,Range<Double>> rangeMap = branch.getRangeMap();
			RuleNode parent = leaf, child;
			while(parent.parentNode()!=null){
				child = parent;
				parent = parent.parentNode();
				
				Attribute att = this.labeledInstances.attribute(parent.splitAtt());
				Range<Double> previous = null;
				if(parent.leftNode()==child)
					previous = rangeMap.put(att,Range.atMost(parent.splitVal()));
				else
					previous = rangeMap.put(att, Range.greaterThan(parent.splitVal()));
				//the attribute is visited previously
				if(previous!=null){
					 previous = rangeMap.get(att).intersection(previous);
					 rangeMap.put(att, previous);
				}
			}
			
			retval.add(branch);
		}
		
		return retval;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private void train() throws Exception{
		models = new M5P[ModelNum];
		for(int i=0;i<ModelNum;i++){
			models[i] = buildModel(labeledInstances, M[i]);
		}
		
		for(int i=0;i<this.comtIterations;i++){
			ArrayList<Instance>[] InstancePiSet = new ArrayList[ModelNum];
			for(int j=0;j<ModelNum;j++)
				InstancePiSet[j] = new ArrayList<Instance>();
			
			for(int m=0;m<ModelNum;m++){
				double maxDelta = 0;
				Instance maxDeltaXY = null;
				Enumeration<Instance> enu = this.unlabeledInstances.enumerateInstances();
				
				while(enu.hasMoreElements()){
					Instance ulIns = enu.nextElement();
					Instances omega = getSiblings(models[m], ulIns);
					double y = models[m].classifyInstance(ulIns);
					if(indexOfClass==-1)
						indexOfClass = labeledInstances.classIndex();
					ulIns.setValue(indexOfClass, y);
					
					Instances instancesPi = new Instances(models[m].getM5RootNode().zyqGetTrainingSet());
					instancesPi.add(ulIns);
					M5P modelPi = buildModel(instancesPi, M[m]);
					double delta = computeOmegaDelta(models[m],modelPi,omega);
					if(maxDelta<delta){
						maxDelta = delta;
						maxDeltaXY = ulIns;
					}
				}
				
				//now check facts about delta
				if(maxDelta>0){
					InstancePiSet[m].add(maxDeltaXY);
					this.unlabeledInstances.delete(this.unlabeledInstances.indexOf(maxDeltaXY));
				}
			}//check for both model
			
			boolean toExit = true;
			for(int m=0;m<ModelNum;m++){
				if(InstancePiSet[m].size()>0){
					toExit = false;
					break;
				}
			}
			
			if(toExit)
				break;
			else{
				//update the models
				int toGen = 0;
				for(int m=0;m<ModelNum;m++){
					Instances set = models[m].getM5RootNode().zyqGetTrainingSet();
					toGen += InstancePiSet[m].size();
					for(Instance ins : InstancePiSet[m])
						set.add(ins);
					
					models[m] = buildModel(set, M[m]);
				}
				
				//Replenish pool U' to size p
				Instances toAdd = retrieveMore(toGen);
				unlabeledInstances.addAll(toAdd);
			}//we will go to another round of iteration
		}//iterate for a number of rounds or break out on empty InstancesPiSets
		
		//now we have the model as y = 0.5*sum(models[m].predict(x))
	}
	
	private Instances retrieveMore(int toGen){
		Instances retval = new Instances(this.unlabeldPool, toGen);
		for(int i=0;i<toGen;i++){
			retval.add(this.unlabeldPool.remove(rand.nextInt(this.unlabeldPool.size())));
		}
		return retval;
	}
	
	private static M5P buildModel(Instances modelInstances, int numOfInstanceInLeaf) throws Exception{
		M5P retval = new M5P();
		retval.setSaveInstances(true);
		retval.setOptions(Utils.splitOptions("-N -L -M "+numOfInstanceInLeaf));
		retval.buildClassifier(modelInstances);
		return retval;
	}
	
	private static Instances getSiblings(M5P modelTree, Instance ins){
		RuleNode node = modelTree.getM5RootNode();
		
		while(!node.isLeaf()){
			if(ins.value(node.splitAtt())<=node.splitVal()){
				node = node.leftNode();
			}else {
				node = node.rightNode();
			}
		}
		
		return node.zyqGetTrainingSet();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void buildClassifier(Instances data) throws Exception {
		this.labeledInstances = data;
		train();
	}
	
	@Override
	public double classifyInstance(Instance ins) throws Exception{
		double sum = 0;
		for(int m=0;m<ModelNum;m++)
			sum += models[m].classifyInstance(ins);
		return sum/(double)ModelNum;
	}
	
	private static double computeOmegaDelta(M5P model, M5P modelPi, Instances omega) throws Exception{
		double retval = 0., y;
		Enumeration<Instance> enu = omega.enumerateInstances();
		int idxClass = omega.classIndex();
		Instance ins;
		while(enu.hasMoreElements()){
			ins = enu.nextElement();
			y = ins.value(idxClass);
			retval += Math.pow(y-model.classifyInstance(ins), 2)-Math.pow(y-modelPi.classifyInstance(ins), 2);
		}
		return retval;
	}

	@Override
	public Capabilities getCapabilities() {
		return new LinearRegression().getCapabilities();
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] retval = new double[1];
		retval[0] = this.classifyInstance(instance);
		return retval;
	}
	
	/*public static void main(String[] args){
		String jnilib = JniNamer.getJniName("netlib-native_system");  
        String natives = System.getProperty("com.github.fommil.netlib.NativeSystemBLAS.natives", jnilib);  
        JniLoader.load(natives.split(","));  
        System.out.println(BLAS.getInstance().getClass().getName());
	}*/
	
}
