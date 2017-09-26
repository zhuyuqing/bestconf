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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TxtFileOperation {

	       
	    public static void writeToFile(String fileName, String content) {  
	        	          
	        File dirFile = null;  
	        try {  
	            dirFile = new File("data/");  
	            if (!(dirFile.exists()) && !(dirFile.isDirectory())) {  
	                boolean creadok = dirFile.mkdirs();  
	                if (creadok) {  
	                    System.out.println(" ok:folder successfully created！ ");  
	                } else {  
	                    System.out.println(" err:failed to create folder！ ");  
	                }  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        String fullPath = dirFile + "/" + fileName + ".txt";  
	        write(fullPath, content);  
	    }  
	  
	    /** 
	     * write file
	     *  
	     * @param path 
	     * @param content
	     */  
	    public static boolean write(String path, String content) {  
	        String s = new String();  
	        String s1 = new String();  
	        BufferedWriter output = null;  
	        try {  
	            File f = new File(path);  
	            if (f.exists()) {  
	            } else {  
	                System.out.println("file doesn't exist and is being creating...");  
	                if (f.createNewFile()) {  
	                    System.out.println("file successfully created！");  
	                } else {  
	                    System.out.println("file failed to create！");  
	                }  
	            } 
	           
	            s1 = content;  
	            output = new BufferedWriter(new FileWriter(f,true));  
	            output.write(s1);  
	            output.flush();  
	            return true;  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            return false;  
	        } finally {  
	            if (output != null) {  
	                try {  
	                    output.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
	    
	    
	    
	    public static List<String> readTxt(String str) {  
	        File file = new File(str);  
	        String line;
	        int flag = 0;
	        InputStreamReader isr= null; 
            List<String> slist = new ArrayList<String>();
	        try {  
	            isr = new InputStreamReader(new FileInputStream(file), "utf-8");    
	            BufferedReader in = new BufferedReader(isr);
	            StringBuilder sb = new StringBuilder();
  
	            while ((line = in.readLine()) != null) {  
	                if(flag<2)
	                	slist.add(line);
	                else{
	                	sb.append(line+"\n");
	                }	
	                flag++;
	            }  
	            
	            slist.add(sb.toString());
	            return slist;  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	            return null;  
	        }
	        catch (IOException e) {  
	            e.printStackTrace();  
	            return null;  
	        } finally {  
	            if (isr != null) {  
	                try {  
	                    isr.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
	      
	      
	    public static String readFile(String str) {  
	        File file = new File(str);  
	        String line;  
	        InputStreamReader isr= null;  
	        try {  
	            isr = new InputStreamReader(new FileInputStream(file), "utf-8");    
	            BufferedReader in = new BufferedReader(isr);
	            StringBuilder sb = new StringBuilder();  
	            while ((line = in.readLine()) != null) {  
	                sb.append(line);  
	                sb.append("\n");
	                  
	            }  
	            return sb.toString();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	            return null;  
	        }
	        catch (IOException e) {  
	            e.printStackTrace();  
	            return null;  
	        } finally {  
	            if (isr != null) {  
	                try {  
	                    isr.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  

}
