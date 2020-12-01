package com.hzyw.iot.test;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.Test;
public class UploadFtp {
	public static String url="192.168.3.137";
	public static int port=2125;
	public static String username="websvr";
	public static String password="webcast";
	public static String rootPath="Product";
//	public static String url="192.168.1.108";
//	public static int port=21;
//	public static String username="public";
//	public static String password="123";
//	public static String rootPath="Product";
	
	/**
	 * Description: 向FTP服务器上传文件
	 * @Version1.0 Jul 27, 2008 4:31:09 PM by 崔红保（cuihongbao@d-heaven.com）创建
	 * @param url FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param path FTP服务器保存目录
	 * @param filename 上传到FTP服务器上的文件名
	 * @param input 输入流
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(String url,int port,String username, String password, String path, String filename, InputStream input) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			boolean flag=ftp.changeWorkingDirectory(path);
			boolean mkdFlag=ftp.makeDirectory(path);
			System.out.println("mkdFlag "+mkdFlag);
			System.out.println("changeWorkingDirectory "+path+"  "+flag);
			ftp.storeFile(filename, input);			
			input.close();
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	} 
	
	
 
	public static boolean uploadOne(String path, String filename, InputStream input) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			boolean flag=ftp.changeWorkingDirectory(path);
			
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			boolean storeFlag=ftp.storeFile(new String(filename.getBytes(),"iso8859-1"), input);			
			System.out.println("storeFlag:"+storeFlag);
			input.close();
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	} 
	
	
	
	@Test  
	public void uploadToFtpServer(String company,String product){  
//		public void uploadToFtpServer(){  
	    try {  
//			String company="xceedsoftware", product="xceedprofessionalthemesforwpf";
			company=company.toLowerCase();
			company=company.replaceAll(" ", "");
			company=company.replaceAll("[.]", "");
			product=product.toLowerCase();
			product=product.replaceAll(" ", "");
			product=product.replaceAll("[.]", "");
			
	    	String path=rootPath+"/"+company+"/"+product;
	    	boolean existsFlag=existeDir(path);
	    	if(!existsFlag)
	    	{
	    		makeDirs(path);
	    	}
	    	File source=new File("e:/t/"+company+"/"+product);
	    	for(File f:source.listFiles())
	    	{
	    		uploadOne(path, f.getName(), new FileInputStream(f));
	    	}
	    	
	    	
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	}
	
	public void makeDirs(String path)
	{
		String[] pathes=path.split( "/" );
        for(int i=0;i<pathes.length;i++)
        {
        	String tempPath="";
        	for(int j=0;j<=i;j++){
        		tempPath+="/"+pathes[j];
        	}
        	tempPath=tempPath.substring(1);
        	System.out.println(tempPath);
        	makeDir(tempPath);
        }
	}
	
	
	
	public static boolean makeDir(String path) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.mkd(path);
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	} 
	
	public boolean existeDir(String path)
	{
		boolean flag = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return flag;
			}
			flag=ftp.changeWorkingDirectory(path);
			ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return flag;
	}

}
