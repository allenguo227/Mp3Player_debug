package guo.mp3player.service;


import guo.download.HttpDownloader;
import guo.model.Mp3Info;
import guo.mp3player.AppConstant;
import guo.mp3player.LocalMp3ListActivity;
import guo.mp3player.NotificationActivity;
import guo.mp3player.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {
	private String mp3name_downloading;
	private int downloadNum=0x123;
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
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("downloadservice has destoryed");
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
			if(mp3Info.getMp3CnName()!=null){
				mp3name_downloading= mp3Info.getMp3CnName();
			}else{
				mp3name_downloading= mp3Info.getMp3Name();
			}
			if(result_mp3==1){
				resultMessage="download_isexisted";
			}
			else if(result_mp3==0){
				resultMessage="download_success";
				downLoadResultNotification(mp3name_downloading+"<----->下载成功！");
			}
			else if(result_mp3==-1){
				resultMessage="download_fail";
				downLoadResultNotification(mp3name_downloading+"<----->下载失败！");
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
	private void  downLoadResultNotification(String notifyInfo){
		Intent intent=new Intent(DownloadService.this,NotificationActivity.class);
		//在创建PendingIntent的时候需要注意参数PendingIntent.FLAG_CANCEL_CURRENT
		//这个标志位用来指示：如果当前的Activity和PendingIntent中设置的intent一样，那么就先取消当前的Activity，用PendingIntent中指定的Activity取代之。
		PendingIntent pi=PendingIntent.getActivity(DownloadService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notify=new Notification();
		notify.icon=R.drawable.ic_launcher;
		//notify.tickerText=notifyInfo;
		notify.flags |= Notification.FLAG_AUTO_CANCEL;
		notify.when=System.currentTimeMillis();
		notify.defaults=Notification.DEFAULT_ALL;
		notify.setLatestEventInfo(DownloadService.this, "下载通知", notifyInfo,pi);
		NotificationManager notifyManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notifyManager.notify(downloadNum++, notify);
	}

}
