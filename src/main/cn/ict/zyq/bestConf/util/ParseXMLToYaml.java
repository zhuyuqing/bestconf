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
package cn.ict.zyq.bestConf.util;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.*;
import org.dom4j.io.*;

public class ParseXMLToYaml {

	public static HashMap parseXMLToHashmap(String filePath) {
		HashMap result = new HashMap();
		try {
			File f = new File(filePath);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element foo;
			for (Iterator i = root.elementIterator("property"); i.hasNext();) {
				foo = (Element) i.next();
				String value;
				if (foo.elementText("value") != null) {
					value = foo.elementText("value");
					Pattern pattern = Pattern.compile("^-?[0-9]\\d*$");
					Matcher matcher = pattern.matcher(value);
					if (matcher.find()) {
						result.put(foo.elementText("name"), value);
					} else if (
							Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$")
							.matcher(value).find()) {
						result.put(foo.elementText("name"), value);
					} else {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void writeToYaml(String filepath, HashMap target) {
		File file = new File(filepath);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator iter = target.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				bw.write(key + ": " + val + "\n");
			}
			bw.close();
		} catch (IOException e) {

		}
	}

	public static void appendXMLByDOM4J(HashMap target, String source,
			String destination) throws IOException {
		
		SAXReader reader = new SAXReader();
		try {
			
			Document document = reader.read(new File(source));
			Element configStore = document.getRootElement();
			Iterator iter = target.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				String val = entry.getValue().toString();
				Element property = configStore.addElement("property");
				Element name = property.addElement("name");
				name.setText(key);
				Element value = property.addElement("value");
				value.setText(val);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileOutputStream(destination),
					format);
			writer.write(document);
			writer.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static void extractYamlRange(String infilepath, String outfilepath) {
		File filer = new File(infilepath);
		File filew = new File(outfilepath);
		// if file doesn't exist, then create it
		try {
			if (!filer.exists()) {
				filer.createNewFile();
			}
			FileWriter fw = new FileWriter(filew.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedReader reader = new BufferedReader(new FileReader(filer));
			String line = null;

			while ((line = reader.readLine()) != null) {

				String args[] = line.split(" ");
				bw.write(args[0] + " " + args[2] + "\n");
			}
			reader.close();
			bw.close();
		} catch (IOException e) {

		}
	}

	public static void split(HashMap target, String sourceYarn,
			String destinationYarn, String sourceMapred,
			String destinationMapred) {
		HashMap mapred = new HashMap();
		HashMap yarn = new HashMap();
		Iterator iter = target.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			if (key.startsWith("yarn"))
				yarn.put(key.substring(4), val);
			else
				mapred.put(key.substring(6), val);
		}
		try {
			appendXMLByDOM4J(yarn, sourceYarn, destinationYarn);
			appendXMLByDOM4J(mapred, sourceMapred, destinationMapred);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
