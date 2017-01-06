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
	                    System.out.println(" ok:创建文件夹成功！ ");  
	                } else {  
	                    System.out.println(" err:创建文件夹失败！ ");  
	                }  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        String fullPath = dirFile + "/" + fileName + ".txt";  
	        write(fullPath, content);  
	    }  
	  
	    /** 
	     * 写文件 
	     *  
	     * @param path 文件路径
	     * @param content 写入内容
	     */  
	    public static boolean write(String path, String content) {  
	        String s = new String();  
	        String s1 = new String();  
	        BufferedWriter output = null;  
	        try {  
	            File f = new File(path);  
	            if (f.exists()) {  
	            } else {  
	                System.out.println("文件不存在，正在创建...");  
	                if (f.createNewFile()) {  
	                    System.out.println("文件创建成功！");  
	                } else {  
	                    System.out.println("文件创建失败！");  
	                }  
	            } 
	            
	            /*BufferedReader input = new BufferedReader(new FileReader(f));  
	            while ((s = input.readLine()) != null) {  
	                s1 += s + "\n";  
	            }  
	            System.out.println("原文件内容：" + s1);  
	            input.close(); */ 
	            
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
	    
	    
	    
	    /**
	     * 读取TXT文件
	     * @param str	文件路径
	     * @return	返回字符串列表，分别对应 参数区间、参数名、样本
	     */
	    public static List<String> readTxt(String str) {  
	        File file = new File(str);  
	        String line;
	        int flag = 0;
	        InputStreamReader isr= null; 
            List<String> slist = new ArrayList<String>();
	        try {  
	            isr = new InputStreamReader(new FileInputStream(file), "utf-8");    
	            BufferedReader in = new BufferedReader(isr);// 包装文件输入流，可整行读取  
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
	        }// 创建文件输入流  
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
	            BufferedReader in = new BufferedReader(isr);// 包装文件输入流，可整行读取  
	            StringBuilder sb = new StringBuilder();  
	            while ((line = in.readLine()) != null) {  
	                sb.append(line);  
	                sb.append("\n");
	                  
	            }  
	            return sb.toString();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	            return null;  
	        }// 创建文件输入流  
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
