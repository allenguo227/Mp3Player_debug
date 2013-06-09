package guo.download;

import guo.mp3player.AppConstant;
import guo.mp3player.RemoteMp3ListActivity;
import guo.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.widget.Toast;
/**
 * 
 * 
 * @author Allenguo
 *下载工具
 */
public class HttpDownloader {
	private URL url=null;
	/**
	 * 根据URL下载文件，前提是这个文件是文本的，函数的返回值就是文件当中的内容
	 * 1.创建一个URL对象
	 * 2.通过URL对象，创建一个HttpURLConnection对象
	 * 3.得到InputStream
	 * 4.从InputStream得到数据
	 */ 
	public String download(String urlStr)  
	{
		System.out.println("download");
		StringBuffer sb=new StringBuffer();
		String line=null;
		BufferedReader buffer=null;
		try {
			//创建一个url链接
			URL url=new URL(urlStr);
			//创建一个http连接
			HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
			//设置网络连接超时时间为5秒
			urlConn.setConnectTimeout(1*1000);
			//读取数据到缓冲流中
			buffer=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			while((line=buffer.readLine())!=null) {
				sb.append(line);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		// 如果网络连接超时，返回timeout标识
		return "timeout";
		}finally{
			try {
				buffer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return   sb.toString();
	}
	/**
	 * 该函数返回整形-1，代表下载文件出错； 返回1 代表文件存在； 返回0 代表文件下载成功
	 */
	public int downFile(String urlStr,String path,String fileName)	{
		InputStream inputStream =null;
		System.out.println("downfile");
		try {
			FileUtils fileUtils=new FileUtils();
			if(fileUtils.isFileExist(fileName, path)) {
				
				return 1;
			}
			else {
				inputStream=getInputStreamFromUrl(urlStr);
				File resultFile=fileUtils.write2SDFromInput(path, fileName, inputStream);
				if(resultFile==null) {
					return -1;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return -1;		
		}
		finally {
			try{
				inputStream.close();
			}catch(Exception e) 	{
				e.printStackTrace();
			}
		}
		return 0;
	}
	/**
	 * 根据URL得到输入流
	 */
	private InputStream getInputStreamFromUrl(String urlStr) {
		InputStream inputStream=null;
		try{
		url=new URL(urlStr);
		HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
		System.out.println("urlConn-------->"+urlConn );
		inputStream=urlConn.getInputStream();//中文不能下载问题所在
		System.out.println("读取到Conn.getInputStream--------->"+inputStream);
		}catch(Exception e)	{
			e.printStackTrace();
			System.out.println("读取到Conn.getInputStream时报异常--------->");
		}
		return inputStream;
	}
}
