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
 * 文件删除、复制
 * 
 */
public class FileUtil {
	
	 private static String MESSAGE = "";  
	 private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";  
	  
	    /** 
	     * 复制单个文件 
	     *  
	     * @param srcFileName 
	     *            待复制的文件名 
	     * @param descFileName 
	     *            目标文件名 
	     * @param overlay 
	     *            如果目标文件存在，是否覆盖 
	     * @return 如果复制成功返回true，否则返回false 
	     */  
	    public static boolean copyFile(String srcFileName, String destFileName,  
	            boolean overlay) {  
	        File srcFile = new File(srcFileName);  
	  
	        // 判断源文件是否存在  
	        if (!srcFile.exists()) {  
	            MESSAGE = "源文件：" + srcFileName + "不存在！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else if (!srcFile.isFile()) {  
	            MESSAGE = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        }  
	  
	        // 判断目标文件是否存在  
	        File destFile = new File(destFileName);  
	        if (destFile.exists()) {  
	            // 如果目标文件存在并允许覆盖  
	            if (overlay) {  
	                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
	                new File(destFileName).delete();  
	            }  
	        } else {  
	            // 如果目标文件所在目录不存在，则创建目录  
	            if (!destFile.getParentFile().exists()) {  
	                // 目标文件所在目录不存在  
	                if (!destFile.getParentFile().mkdirs()) {  
	                    // 复制文件失败：创建目标文件所在目录失败  
	                    return false;  
	                }  
	            }  
	        }  
	  
	        // 复制文件  
	        int byteread = 0; // 读取的字节数  
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
	     * 复制整个目录的内容 
	     *  
	     * @param srcDirName 
	     *            待复制目录的目录名 
	     * @param destDirName 
	     *            目标目录名 
	     * @param overlay 
	     *            如果目标目录存在，是否覆盖 
	     * @return 如果复制成功返回true，否则返回false 
	     */  
	    public static boolean copyDirectory(String srcDirName, String destDirName,  
	            boolean overlay) {  
	        // 判断源目录是否存在  
	        File srcDir = new File(srcDirName);  
	        if (!srcDir.exists()) {  
	            MESSAGE = "复制目录失败：源目录" + srcDirName + "不存在！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else if (!srcDir.isDirectory()) {  
	            MESSAGE = "复制目录失败：" + srcDirName + "不是目录！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        }  
	  
	        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
	        if (!destDirName.endsWith(File.separator)) {  
	            destDirName = destDirName + File.separator;  
	        }  
	        File destDir = new File(destDirName);  
	        // 如果目标文件夹存在  
	        if (destDir.exists()) {  
	            // 如果允许覆盖则删除已存在的目标目录  
	            if (overlay) {  
	                new File(destDirName).delete();  
	            } else {  
	                MESSAGE = "复制目录失败：目的目录" + destDirName + "已存在！";  
	                JOptionPane.showMessageDialog(null, MESSAGE);  
	                return false;  
	            }  
	        } else {  
	            // 创建目的目录  
	            System.out.println("目的目录不存在，准备创建。。。");  
	            if (!destDir.mkdirs()) {  
	                System.out.println("复制目录失败：创建目的目录失败！");  
	                return false;  
	            }  
	        }  
	  
	        boolean flag = true;  
	        File[] files = srcDir.listFiles();  
	        for (int i = 0; i < files.length; i++) {  
	            // 复制文件  
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
	            MESSAGE = "复制目录" + srcDirName + "至" + destDirName + "失败！";  
	            JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else {  
	            return true;  
	        }  
	    }  
		

	    /** 
	     *  根据路径删除指定的目录或文件，无论存在与否 
	     *@param sPath  要删除的目录或文件 
	     *@return 删除成功返回 true，否则返回 false。 
	     */  
	    public static boolean deleteFolder(String sPath) {  
	        boolean flag = false;  
	       File file = new File(sPath);  
	        // 判断目录或文件是否存在  
	        if (!file.exists()) {  // 不存在返回 false  
	            return flag;  
	        } else {  
	            // 判断是否为文件  
	            if (file.isFile()) {  // 为文件时调用删除文件方法  
	                return deleteFile(sPath);  
	            } else {  // 为目录时调用删除目录方法  
	                return deleteDirectory(sPath);  
	            }  
	        }  
	    }  
	    
	    /** 
	     * 删除单个文件 
	     * @param   sPath    被删除文件的文件名 
	     * @return 单个文件删除成功返回true，否则返回false 
	     */  
	    public static boolean deleteFile(String sPath) {  
	        boolean flag = false;  
	        File file = new File(sPath);  
	        // 路径为文件且不为空则进行删除  
	        if (file.isFile() && file.exists()) {  
	            file.delete();  
	            flag = true;  
	        }  
	        return flag;  
	    }  
	    
	    /** 
	     * 删除目录（文件夹）以及目录下的文件 
	     * @param   sPath 被删除目录的文件路径 
	     * @return  目录删除成功返回true，否则返回false 
	     */  
	    private static boolean deleteDirectory(String sPath) {  
	        //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	        if (!sPath.endsWith(File.separator)) {  
	            sPath = sPath + File.separator;  
	        }  
	        File dirFile = new File(sPath);  
	        //如果dir对应的文件不存在，或者不是一个目录，则退出  
	        if (!dirFile.exists() || !dirFile.isDirectory()) {  
	            return false;  
	        }  
	        boolean flag = true;  
	        //删除文件夹下的所有文件(包括子目录)  
	        File[] files = dirFile.listFiles();  
	        for (int i = 0; i < files.length; i++) {  
	            //删除子文件  
	            if (files[i].isFile()) {  
	                flag = deleteFile(files[i].getAbsolutePath());  
	                if (!flag) break;  
	            } //删除子目录  
	            else {  
	                flag = deleteDirectory(files[i].getAbsolutePath());  
	                if (!flag) break;  
	            }  
	        }  
	        if (!flag) return false;  
	        //删除当前目录  
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
