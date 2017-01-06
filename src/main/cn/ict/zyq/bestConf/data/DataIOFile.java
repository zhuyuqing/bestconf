package cn.ict.zyq.bestConf.data;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.XRFFLoader;
import weka.core.converters.XRFFSaver;

public class DataIOFile {
	
	/**
	 * Return the data set loaded from the CSV file at @param path
	 */
	public static Instances loadDataFromCsvFile(String path) throws IOException{
	    CSVLoader loader = new CSVLoader();
	    loader.setSource(new File(path));
	    Instances data = loader.getDataSet();
	    
	    System.out.println("\nHeader of dataset:\n");
	    System.out.println(new Instances(data, 0));
	    return data;
	}
	
	/**
	 * Save @param data to the CSV file at @param path
	 */
	public static void saveDataToCsvFile(String path, Instances data) throws IOException{
		    System.out.println("\nSaving to file " + path + "...");
		    CSVSaver saver = new CSVSaver();
		    saver.setInstances(data);
		    saver.setFile(new File(path));
		    saver.writeBatch();
	}
	
	/**
	 * Return the data set loaded from the Arff file at @param path
	 */
	public static Instances loadDataFromArffFile(String path) throws IOException{
		ArffLoader loader = new ArffLoader();
	    loader.setSource(new File(path));
	    Instances data = loader.getDataSet();
	    
	    System.out.println("\nHeader of dataset:\n");
	    System.out.println(new Instances(data, 0));
	    return data;
	}
	
	/**
	 * Save @param data to the Arff file at @param path
	 */
	public static void saveDataToArffFile(String path, Instances data) throws IOException{
		    System.out.println("\nSaving to file " + path + "...");
		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(data);
		    saver.setFile(new File(path));
		    saver.writeBatch();
	}
	
	/**
	 * Return the data set loaded from the Xrff file at @param path
	 */
	public static Instances loadDataFromXrffFile(String path) throws IOException{
		XRFFLoader loader = new XRFFLoader();
	    loader.setSource(new File(path));
	    Instances data = loader.getDataSet();
	    
	    System.out.println("\nHeader of dataset:\n");
	    System.out.println(new Instances(data, 0));
	    return data;
	}
	
	/**
	 * Save @param data to the Xrff file at @param path
	 */
	public static void saveDataToXrffFile(String path, Instances data) throws IOException{
		    System.out.println("\nSaving to file " + path + "...");
		    XRFFSaver saver = new XRFFSaver();
		    saver.setInstances(data);
		    saver.setFile(new File(path));
		    saver.writeBatch();
	}
	
	public static void convertFromXrffToArff(String dir) throws IOException{
		File file = new File(dir);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				String nPath = f.getAbsolutePath();
				nPath = nPath.substring(0, nPath.indexOf(".xrff"));
				saveDataToArffFile(nPath+".arff", loadDataFromXrffFile(f.getAbsolutePath()));
			}
		}else if(file.isFile()){
			String nPath = dir.substring(0, dir.indexOf(".xrff"));
			saveDataToArffFile(nPath+".arff", loadDataFromXrffFile(dir));
		}
	}
	
	public static void main(String[] args){
		try {
			
			convertFromXrffToArff("COMT2_Round1");
			
			//Instances data = loadDataFromCsvFile("data/aloja.csv");
			/*Instances data = loadDataFromArffFile("data/train.arff");
			
			System.err.println(data.numAttributes());
			System.err.println(data.size());
			
			saveDataToXrffFile("data/trainingBestConf0_COMT.xrff", data);*/
			
			/*for(int i=0;i<data.numAttributes();i++){
				if(data.attribute(i).isNumeric()){
					System.err.println(data.attribute(i).name()+":"+data.attribute(i).getUpperNumericBound()+":"+data.attribute(i).getLowerNumericBound());
				}
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
