package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ho.yaml.Yaml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;

public class TomcatConfigReadin implements ConfigReadin {

	private String server;
	private String username;
	private String password;
	
	private String remotePath;
	private String localPath;
	private String filepath;
	
	@Override
	public void initial(String server, String username, String password,
			String localPath, String remotePath) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		filepath= localPath + "/defaultConfig.yaml";
	}

	@Override
	public void downLoadConfigFile(String fileName) {
		// TODO Auto-generated method stub
	}

	@Override
	public HashMap modifyConfigFile(HashMap updatedVals, String filepath) {
		HashMap ori = loadYamlToHashMap(filepath);
		modify(ori, updatedVals);
		return ori;
	}

	@Override
	public HashMap modifyConfigFile(HashMap updatedVals) {
		HashMap ori = loadYamlToHashMap(filepath);
		modify(ori, updatedVals);
		return ori;
	}
	
	private static HashMap loadYamlToHashMap(String filePath) {
		HashMap hashmap = null; 
		File f = new File(filePath);
		try {
			hashmap = Yaml.loadType(new FileInputStream(f.getAbsolutePath()), HashMap.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return hashmap;
	}
	
	private void modify(HashMap toModify, HashMap updatedValues) {
		boolean flag;
		Iterator itrUpdated = updatedValues.entrySet().iterator();
		while(itrUpdated.hasNext()){
			Map.Entry entUpdated = (Map.Entry)itrUpdated.next();
			Object tempObj = toModify.get(entUpdated.getKey().toString());
			if(tempObj!=null){
				if (!entUpdated.getValue().toString().equals("NaN")) {
					if (Double.parseDouble(tempObj.toString()) < 1.0) {
						toModify.put(entUpdated.getKey().toString(), entUpdated.getValue().toString());
					} else {
						toModify.put(entUpdated.getKey().toString(), Math.round(Double.parseDouble(entUpdated.getValue().toString())));
					}
				}
			}else
				System.out.println(entUpdated.getKey().toString() + "Doesn't exit in original list£¡");
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String[] configs = {"acceptCount", "acceptorThreadCount", "asyncTimeout", 
		"connectionTimeout", "executorTerminationTimeoutMillis", "keepAliveTimeout", "maxConnections", 
		"maxKeepAliveRequests", "maxThreads", "minSpareThreads", "processorCache", "socketBuffer"};
	public static void writeIniYamlFile(){
		String dfltConf = "data/defaultConfig.yaml";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(dfltConf));
			for(int i=0;i<configs.length;i++){
				bw.write(configs[i]+":"+"0\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		//TomcatConfigReadin.writeIniYamlFile();
		String dfltConf = "data/defaultConfig.yaml";
		writetoConfigfile(loadYamlToHashMap(dfltConf));
	}
	
	private static void writetoConfigfile(HashMap hm) {
		String iniConf = "data/server-initial.xml", updatedConf="data/server.xml";
		
		try {
			//we first read in the base line
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder;
			dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(new File(iniConf));
			
			//now we find the place to change
			NodeList connList = doc.getElementsByTagName("Connector");
			boolean done = false;
			for (int i = 0; i < connList.getLength() && !done; i++) {
				Node connNode = connList.item(i);
				if (connNode.getNodeType() == Node.ELEMENT_NODE) {
					 NamedNodeMap map = connNode.getAttributes();
					 for(int j=0;j<map.getLength() && !done;j++){
						 if(map.item(j).getNodeName().equals("port") &&
								 map.item(j).getNodeValue().equals("8080")){
							 /** now we update the config file as needed*/
							 Element ele = (Element)connNode;
							 for(Object obj : hm.entrySet()){
								 Map.Entry ent = (Map.Entry)obj;
								 ele.setAttribute(ent.getKey().toString(), ent.getValue().toString());
							 }
							 done=true;
						 }
					 }
				}
			}
			
			//updated! we write the config out to file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(updatedConf));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
