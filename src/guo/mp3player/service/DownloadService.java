package guo.mp3player.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import guo.download.HttpDownloader;
import guo.model.Mp3Info;
import guo.mp3player.AppConstant;
import guo.utils.FileUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class DownloadService extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	//每次用户点击listactivity当中的一个条目时，就会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//从Intent对象中将Mp3Info对象作为参数传递到县城对象当中
		Mp3Info mp3Info=(Mp3Info)intent.getSerializableExtra("mp3Info");
		//启动新线程
	//	System.out.println("下载服务中的mp3Info------------>"+mp3Info);
		DownloadThread downloadThread=new DownloadThread(mp3Info);
		Thread thread=new Thread(downloadThread);
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	class DownloadThread implements Runnable{
		private Mp3Info mp3Info=null;
		public DownloadThread(Mp3Info mp3Info)	{
			this.mp3Info=mp3Info;
		}
		
		@Override
		public void run() {
			// 我的服务器下载地址http://192.168.1.101:8080/mp3/missing,mp3
			//根据MP3文件的名字，生成下载地址，这里同时下载MP3和lrc文件。
				//修改下载地址存在中文的问题
			
			String mp3Url = AppConstant.URL.BASE_URL+"/"+mp3Info.getMp3Name();
			String lrcUrl=AppConstant.URL.BASE_URL+"/"+mp3Info.getLrcName();
			//生成下载文件所用的对象
			HttpDownloader httpDownloader =new HttpDownloader();
			//将文件下载下来，并存储到SDCard当中的MP3文件夹中
			int result_mp3=0;
			if(mp3Info.getMp3CnName()!=null&&mp3Info.getLrcCnName()!=null){
			result_mp3=httpDownloader.downFile(mp3Url, "mp3/", mp3Info.getMp3CnName());
			httpDownloader.downFile(lrcUrl, "mp3/", mp3Info.getLrcCnName());
			}
			else{
				result_mp3=httpDownloader.downFile(mp3Url, "mp3/", mp3Info.getMp3Name());
				httpDownloader.downFile(lrcUrl, "mp3/", mp3Info.getLrcName());
			}
			String resultMessage=null;
			if(result_mp3==1){
				resultMessage="download_isexisted";
			}
			else if(result_mp3==0){
				resultMessage="download_success";
			}
			else if(result_mp3==-1){
				resultMessage="download_fail";
			}
			else {
				resultMessage="";
			
			}
			//通过广播返回下载结果。
			Intent intent=new Intent();
			intent.setAction(AppConstant.DOWNLOAD_RESULT);
			intent.putExtra("downloadresult", resultMessage);
			sendBroadcast(intent);
			
		} 
		
	}
	

}
