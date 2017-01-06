package cn.ict.zyq.bestConf.cluster.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesUtil {

	public PropertiesUtil() {
		// TODO Auto-generated constructor stub
	}
	
	//根据Key读取Value
    public static String GetValueByKey(String filePath, String key) {
        Properties pps = new Properties();
        try {
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));  
            pps.load(in);
            String value = pps.getProperty(key);
            System.out.println(key + " = " + value);
            return value;
            
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //读取Properties的全部信�?
    public static Properties GetAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
/*        Enumeration en = pps.propertyNames(); //得到配置文件的名�?
        
        while(en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            String strValue = pps.getProperty(strKey);
            System.out.println(strKey + "=" + strValue);
        }*/
        return pps;
    }
    
    //写入Properties信息
    public static void WriteProperties (String filePath, String pKey, String pValue) throws IOException {
        Properties pps = new Properties();
        
        //make sure the file exists
        File propFile = new File(filePath);
        if(!propFile.exists())
        	propFile.createNewFile();
        
        InputStream in = new FileInputStream(filePath);
        //从输入流中读取属性列表（键和元素对） 
        pps.load(in);
        //调用 Hashtable 的方�?put。使�?getProperty 方法提供并行性�?  
        //强制要求为属性的键和值使用字符串。返回�?�?Hashtable 调用 put 的结果�?
        OutputStream out = new FileOutputStream(filePath);
        pps.setProperty(pKey, pValue);
        //以�?合使�?load 方法加载�?Properties 表中的格式，  
        //将此 Properties 表中的属性列表（键和元素对）写入输出�? 
        pps.store(out, "Update " + pKey + " name");
    }
    
    public static void main(String [] args) throws IOException{
        //String value = GetValueByKey("Test.properties", "name");
        //System.out.println(value);
        //GetAllProperties("data/comtConfig.properties");
        WriteProperties("data/Test.properties","long", "212");
    }


}
