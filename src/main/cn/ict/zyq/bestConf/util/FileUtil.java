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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

/**
 * file delete、copy
 * 
 */
public class FileUtil {
	
	 private static String MESSAGE = "";  
	 private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";  
	  
	    /** 
	     * single file copy
	     *  
	     * @param srcFileName 
	     *            file name to copy 
	     * @param descFileName 
	     *            target name
	     * @param overlay 
	     *            if target file exists, then override 
	     * @return 
	     */  
	    public static boolean copyFile(String srcFileName, String destFileName,  
	            boolean overlay) {  
	        File srcFile = new File(srcFileName);  
	    
	        if (!srcFile.exists()) {  
	            MESSAGE = "source file：" + srcFileName + "doesn't exist！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else if (!srcFile.isFile()) {  
	            MESSAGE = "failed：" + srcFileName + "not a single file！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        }  
	  
	        // judge whether target file exists  
	        File destFile = new File(destFileName);  
	        if (destFile.exists()) {  
	            if (overlay) {  
	                new File(destFileName).delete();  
	            }  
	        } else {  
	            if (!destFile.getParentFile().exists()) {  
	            
	                if (!destFile.getParentFile().mkdirs()) {  
	                
	                    return false;  
	                }  
	            }  
	        }  
	        int byteread = 0; 
	        InputStream in = null;  
	        OutputStream out = null;  
	  
	        try {  
	            in = new FileInputStream(srcFile);  
	            out = new FileOutputStream(destFile);  
	            byte[] buffer = new byte[1024];  
	  
	            while ((byteread = in.read(buffer)) != -1) {  
	                out.write(buffer, 0, byteread);  
	            }  
	            return true;  
	        } catch (FileNotFoundException e) {  
	            return false;  
	        } catch (IOException e) {  
	            return false;  
	        } finally {  
	            try {  
	                if (out != null)  
	                    out.close();  
	                if (in != null)  
	                    in.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	  
	    /** 
	     * 
	     *  
	     * @param srcDirName 
	     *           
	     * @param destDirName 
	     *           
	     * @param overlay 
	     *           
	     * @return 
	     */  
	    public static boolean copyDirectory(String srcDirName, String destDirName,  
	            boolean overlay) {  
	        
	        File srcDir = new File(srcDirName);  
	        if (!srcDir.exists()) {  
	            MESSAGE = "failed" + srcDirName + "doesn't exist！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else if (!srcDir.isDirectory()) {  
	            MESSAGE = "failed " + srcDirName + "not category！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        }  
	  
	        
	        if (!destDirName.endsWith(File.separator)) {  
	            destDirName = destDirName + File.separator;  
	        }  
	        File destDir = new File(destDirName);  
	        
	        if (destDir.exists()) {  
	           
	            if (overlay) {  
	                new File(destDirName).delete();  
	            } else {  
	                MESSAGE = "failed to copy category " + destDirName + "already exist！";  
	                JOptionPane.showMessageDialog(null, MESSAGE);  
	                return false;  
	            }  
	        } else {  
	           
	            System.out.println("prepare to create target category。。。");  
	            if (!destDir.mkdirs()) {  
	                System.out.println("failed to copy category and create target category！");  
	                return false;  
	            }  
	        }  
	  
	        boolean flag = true;  
	        File[] files = srcDir.listFiles();  
	        for (int i = 0; i < files.length; i++) {  
	           
	            if (files[i].isFile()) {  
	                flag = FileUtil.copyFile(files[i].getAbsolutePath(),  
	                        destDirName + files[i].getName(), overlay);  
	                if (!flag)  
	                    break;  
	            } else if (files[i].isDirectory()) {  
	                flag = FileUtil.copyDirectory(files[i].getAbsolutePath(),  
	                        destDirName + files[i].getName(), overlay);  
	                if (!flag)  
	                    break;  
	            }  
	        }  
	        if (!flag) {  
	            MESSAGE = "copy category" + srcDirName + "to" + destDirName + "failed！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else {  
	            return true;  
	        }  
	    }  
		

	    /** 
	     *  
	     *@param sPath  
	     *@return 
	     */  
	    public static boolean deleteFolder(String sPath) {  
	        boolean flag = false;  
	       File file = new File(sPath);  
	     
	        if (!file.exists()) { 
	            return flag;  
	        } else {  
	            
	            if (file.isFile()) {  
	                return deleteFile(sPath);  
	            } else { 
	                return deleteDirectory(sPath);  
	            }  
	        }  
	    }  
	    
	    /** 
	     * 
	     * @param   sPath    
	     * @return 
	     */  
	    public static boolean deleteFile(String sPath) {  
	        boolean flag = false;  
	        File file = new File(sPath);  
	       
	        if (file.isFile() && file.exists()) {  
	            file.delete();  
	            flag = true;  
	        }  
	        return flag;  
	    }  
	    
	    /** 
	     * 
	     * @param   sPath 
	     * @return  
	     */  
	    private static boolean deleteDirectory(String sPath) {  
	       
	        if (!sPath.endsWith(File.separator)) {  
	            sPath = sPath + File.separator;  
	        }  
	        File dirFile = new File(sPath);  
	      
	        if (!dirFile.exists() || !dirFile.isDirectory()) {  
	            return false;  
	        }  
	        boolean flag = true;  
	   
	        File[] files = dirFile.listFiles();  
	        for (int i = 0; i < files.length; i++) {  
	            if (files[i].isFile()) {  
	                flag = deleteFile(files[i].getAbsolutePath());  
	                if (!flag) break;  
	            } 
	            else {  
	                flag = deleteDirectory(files[i].getAbsolutePath());  
	                if (!flag) break;  
	            }  
	        }  
	        if (!flag) return false;  
	        if (dirFile.delete()) {  
	            return true;  
	        } else {  
	            return false;  
	        }  
	    }  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String srcDirName = "cassandra/data";  
        String destDirName = "/root/apache-cassandra-2.0.16/data";  
        FileUtil.deleteFolder(destDirName);
        FileUtil.copyDirectory(srcDirName, destDirName, true);  

	}

}
