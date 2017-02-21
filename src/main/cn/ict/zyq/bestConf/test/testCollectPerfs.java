package cn.ict.zyq.bestConf.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class testCollectPerfs {
    private boolean isPerfExists(String name, String path){
    	boolean result = false;
    	File file = new File(path);
    	File f[] = file.listFiles();
		if(f != null){
			for(int i = 0; i < f.length; i++){
		      if(name.equals(f[i].getName())){
		    	 result = true;
		    	 break;
		      }
		   }
		}
    	return result;
    }
    private double[] getPerf(String path, String name){
    	double[] result = new double[2];
    	File file = new File(path);
    	File f[] = file.listFiles();
		if(f != null){
			int tot = 0;
			for(int i = 0; i < f.length; i++){
			  
		      if(name.equals(f[i].getName())){
		    	 File res = new File(path+"/" + f[i].getName());
		    	 BufferedReader reader;
				 try {
					reader = new BufferedReader(new FileReader(res));
					String readline = null;
			        while ((readline = reader.readLine()) != null) {
			           result[tot++] = Double.parseDouble(readline);	
			        }
			        reader.close();
			    	break;
				 } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
		      }
		   }
		}
    	return result;
    }
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
        } catch (Exception e) {
            System.out.println("Error！");
        }
		return null;
    }
    private void writePerfstoFile(Instance ins, String filepath){
    	StringBuffer name = new StringBuffer("");
    	for(int i = 0; i < ins.numAttributes() - 2; i++)
    		name.append(ins.value(ins.attribute(i)));
    	File file = new File(filepath + "/" + getMD5(name.toString()));
    	BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(ins.value(ins.attribute(ins.numAttributes()-2))+"\n");
				writer.write(ins.value(ins.attribute(ins.numAttributes()-1))+"\n");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    private int performanceType = 3;
    private String perfsfilepath = "data/testperfsfile";
	public Instances runExp(Instances samplePoints, String perfAttName){
		Instances retVal = null;
		if(performanceType>2 && samplePoints.attribute("combinedPerf_zyq")==null){
			Attribute combinedPerf = new Attribute("combinedPerf_zyq");
			samplePoints.insertAttributeAt(combinedPerf, samplePoints.numAttributes());
		}
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		int pos = samplePoints.numInstances();
		int count = 0;
		for(int i = 0; i < 24; i++){
			Instance ins = samplePoints.get(i);
			//System.err.println(ins.toString());
			HashMap hm = new HashMap();
			int tot = 0;
			for(int j = 0; j < ins.numAttributes(); j++){
				hm.put(ins.attribute(j).name(), ins.value(ins.attribute(j)));
				//System.out.println("key is " + ins.attribute(j).name() + " value is" + ins.value(ins.attribute(j)));
			}
			boolean testRet;
			//System.out.println("initial attribute is : " + ins.value(ins.attribute(ins.numAttributes()-1)));
			//if(ins.value(ins.attribute(ins.numAttributes()-1)) == Double.NaN){
			  if(Double.isNaN(ins.value(ins.attribute(ins.numAttributes()-1)))){	
				double y = 0;
					y = -1;
					if(performanceType>2)
						ins.setValue(samplePoints.numAttributes()-2,-1);		
			ins.setValue(samplePoints.numAttributes()-1, y);
			writePerfstoFile(ins,perfsfilepath);
		}else{
			continue;
		}
		}
		retVal = samplePoints;
		retVal.setClassIndex(retVal.numAttributes()-1);
		
		return retVal;
	}

	public Instances collectPerfs(Instances samplePoints, String perfAttName) {
		// TODO Auto-generated method stub
		Instances retVal = null;
		if(performanceType>2 && samplePoints.attribute("combinedPerf_zyq")==null){
			Attribute combinedPerf = new Attribute("combinedPerf_zyq");
			samplePoints.insertAttributeAt(combinedPerf, samplePoints.numAttributes());
		}
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		int pos = samplePoints.numInstances();
		int tot = 0;
		for(int i = 0; i < 20; i++){
			Instance ins = samplePoints.get(i);
			StringBuffer perfs = new StringBuffer("");
			for(int j = 0; j < ins.numAttributes()-2; j++){
				perfs.append(ins.value(ins.attribute(j)));
			}
			String targetname = getMD5(perfs.toString());
			if(isPerfExists(targetname, perfsfilepath)){
				System.out.println("文件存在 ：" + tot++);
				double[] results = getPerf(perfsfilepath, targetname);
				ins.setValue(samplePoints.numAttributes()-2, results[0]);
				ins.setValue(samplePoints.numAttributes()-1, results[1]);
				System.out.println("num-2 is " + results[0]);
				System.out.println("num-1 is " + results[1]);
			}else
				System.out.println("not exist！");
		}
		retVal = samplePoints;
		retVal.setClassIndex(retVal.numAttributes()-1);
		return retVal;
	}
	public static void main(String[] args){
		FileReader frData;
		try {
			testCollectPerfs test = new testCollectPerfs();
			frData = new FileReader("data/trainingBestConf0_RRS0.arff");
			Instances data = new Instances(frData);// 
			System.out.println("num of attributes is : " + data.numAttributes());
			System.out.println("size of data is : " + data.numInstances());
			
			test.collectPerfs(data, "performance");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
}
