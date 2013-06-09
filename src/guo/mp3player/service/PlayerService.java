package guo.mp3player.service;
/*
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import lrc.LrcProcessor;
import guo.model.Mp3Info;
import guo.mp3player.AppConstant;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

public class PlayerService extends Service {
	private MediaPlayer mediaPlayer=null;
	private boolean isPlaying=false;
	private boolean isPause=false;
	private boolean isReleased=false;
	private Handler handler=new Handler();
	private UpdateTimeCallback updateTimeCallback=null;
	private ArrayList<Queue> queues = null;
	private long begin=0;
	private long nextTimeMill=0;
	private long currentTimeMill=0;
	private long pauseTimeMills=0;
	private String message=null;
	Mp3Info mp3Info=null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
		}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mp3Info=(Mp3Info)intent.getSerializableExtra("mp3Info");
		int MSG=intent.getIntExtra("MSG", 0);
		if(mp3Info!=null){  
			if(MSG==AppConstant.PlayerMsg.PLAY_MSG) {	
			play(mp3Info);
			}
		}else{
				if(MSG==AppConstant.PlayerMsg.PAUSE_MSG) {
					pause();
				}else if(MSG==AppConstant.PlayerMsg.STOP_MSG) {
					stop();
				}
			}
			return super.onStartCommand(intent, flags, startId);
	}

	private void play(Mp3Info mp3Info)
	{
		if(!isPlaying){
				String path=getMp3Path(mp3Info);
				mediaPlayer=MediaPlayer.create(this, Uri.parse("file://"+path));
				mediaPlayer.setLooping(false);
				mediaPlayer.start();
				prepareLrc(mp3Info.getLrcName());
				handler.postDelayed(updateTimeCallback, 5);
				isPlaying=true;
				isReleased=false;
		
		}
	}
	private void pause(){
		if(mediaPlayer!=null){
			if(!isPause){
				mediaPlayer.pause();
				handler.removeCallbacks(updateTimeCallback);
				pauseTimeMills=System.currentTimeMillis();
			}
			else{
				mediaPlayer.start();
				handler.postDelayed(updateTimeCallback, 5);
				begin=System.currentTimeMillis()-pauseTimeMills+begin;
			}
			//��Ŀ������Զ����״̬
			isPlaying=isPlaying?false:true;
			isPause=isPause?false:true;
		}
	}
	private void stop(){
		if(mediaPlayer!=null){
			if(isPlaying){
				if(!isReleased){
					handler.removeCallbacks(updateTimeCallback);
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased=true;
				}
				isPlaying=false;			
			}
		}
	}
	private void prepareLrc(String lrcName)
	{
		try{
			InputStream inputStream =new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()
					+File.separator+"mp3/"+lrcName);
			LrcProcessor lrcProcessor=new LrcProcessor();
			queues=lrcProcessor.process(inputStream);
			//����һ��UpdateTimeCallback����
			updateTimeCallback=new UpdateTimeCallback(queues);
			begin=0;
			currentTimeMill=0;
			nextTimeMill=0;
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	private String getMp3Path(Mp3Info mp3Info){
		String SDCardRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
		String path=SDCardRoot+File.separator+"mp3"+File.separator+mp3Info.getMp3Name();
		return path;
	}

	class UpdateTimeCallback implements Runnable{
		Queue times = null;
		Queue messages = null;
		public UpdateTimeCallback(ArrayList<Queue> queues){
			//��ArrayList����ȡ����Ӧ�Ķ���
			times = queues.get(0);
			messages = queues.get(1);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//����ƫ������Ҳ���Ǵ�mp3���ſ�ʼ�����ڣ�������˶���ʱ�䣬��λ����
			long offset = System.currentTimeMillis() - begin;
			//��һ�β���
			if(currentTimeMill == 0){
				//��timesȡ����һ��Ҫ���¸�ʵ�ʱ���
				nextTimeMill = (Long)times.poll();
				//��һ��ʱ����Ӧ�ĸ��
				message = (String)messages.poll();
			}
			//�Ѿ����ŵ�ʱ����ڵ�����һ��Ҫ���¸�ʵ�ʱ��
			if(offset >= nextTimeMill){
				Intent intent = new Intent();
				intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
				intent.putExtra("lrcMessage", message);
				sendBroadcast(intent);
				message = (String)messages.poll();
				nextTimeMill = (Long)times.poll();
				
			}
			currentTimeMill = currentTimeMill + 10;
			//10������ٴ���handlerִ��updateTimeCallback
			handler.postDelayed(updateTimeCallback, 10);
		}		
	}
}
*/


import guo.model.Mp3Info;
import guo.mp3player.AppConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import lrc.LrcProcessor;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
/**
 * @author Administrator
 *��̨�������ֵķ���
 */
public class PlayerService extends Service {

	private boolean isPlaying=false;
	private boolean isPause=false;
	private boolean isReleased=false;
	private MediaPlayer mediaPlayer=null;
	@SuppressWarnings("rawtypes")
	ArrayList<Queue> queues = null;
	private Handler handler = new Handler();
	private UpdateTimeCallback updateTimeCallback = null;
	private UpdateSeekbarProgress updateSeekbarProgress=null;
	private long begin = 0;
	private long currentTimeMill = 0;
	private long nextTimeMill = 0;
	private long pauseTimeMills = 0;
	private Mp3Info mp3Info=null;
	private String message = null;
	private int seekbarprogress=0;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//启动service时的回调函数（生命周期）。判断当前MP3播放状态
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mp3Info=(Mp3Info) intent.getSerializableExtra("mp3Info");
		int MSG=intent.getIntExtra("MSG", 0);
		if(mp3Info!=null){
			if(MSG==AppConstant.PlayerMsg.PLAY_MSG){
				play(mp3Info);
			}
		}else{
				if(MSG==AppConstant.PlayerMsg.PAUSE_MSG){
					pause();
				}else if(MSG==AppConstant.PlayerMsg.STOP_MSG){
					stop();
				}
		}		
		return super.onStartCommand(intent, flags, startId);
	}

	private void play(Mp3Info mp3Info)
	{
		if(!isPlaying){
				mediaPlayer=MediaPlayer.create(this, Uri.parse("file://"+getMp3Path(mp3Info)));
				mediaPlayer.setLooping(true);
				mediaPlayer.start();
				prepareLrc(mp3Info.getLrcName());
				prepareSeekbar();
				begin=System.currentTimeMillis();
				handler.postDelayed(updateTimeCallback, 5);
				handler.postDelayed(updateSeekbarProgress, 5);
				isPlaying=true;
				isReleased=false;
		
		}
	}
	private void pause(){
		System.out.println("pause()---------->");
		if(mediaPlayer!=null){
			if(!isReleased){
				if(!isPause){
					mediaPlayer.pause();
					handler.removeCallbacks(updateTimeCallback);
					handler.removeCallbacks(updateSeekbarProgress);
					pauseTimeMills = System.currentTimeMillis() ;
				}else{
					mediaPlayer.start();
					handler.postDelayed(updateTimeCallback, 5);
					handler.postDelayed(updateSeekbarProgress, 5);
					begin = System.currentTimeMillis() - pauseTimeMills + begin;
				}
				isPause=isPause?false:true;
			}
		}
	}
	private void stop(){
		System.out.println("stop()---------->");
		if(mediaPlayer!=null){
			if(isPlaying){
				if(!isReleased){
					handler.removeCallbacks(updateTimeCallback);
					handler.removeCallbacks(updateSeekbarProgress);
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased=true;
				}
				isPlaying=false;			
			}
		}
	}
	
	private void prepareLrc(String lrcName){
		try{
			InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()
					+ File.separator + "mp3/" + lrcName);
			LrcProcessor lrcProcessor = new LrcProcessor();
			queues = lrcProcessor.process(inputStream);
			//����һ��UpdateTimeCallback����
			updateTimeCallback = new UpdateTimeCallback(queues);
			begin = 0;
			currentTimeMill = 0;
			nextTimeMill = 0;
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	private void prepareSeekbar(){	
		updateSeekbarProgress=new UpdateSeekbarProgress();
	}
	//��ȡMP3·��
	private String getMp3Path(Mp3Info mp3infos){
		String path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mp3/"+mp3infos.getMp3Name();
		return path;
	}
	class UpdateTimeCallback implements Runnable{
		@SuppressWarnings("rawtypes")
		Queue times = null;
		@SuppressWarnings("rawtypes")
		Queue messages = null;
		@SuppressWarnings("rawtypes")
		public UpdateTimeCallback(ArrayList<Queue> queues){
			//��ArrayList����ȡ����Ӧ�Ķ���
			times = queues.get(0);
			messages = queues.get(1);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long offset = System.currentTimeMillis() - begin;
			//
			if(currentTimeMill == 0){
				//
				nextTimeMill = (Long)times.poll();
				//
				message = (String)messages.poll();
			}
			//
			if(offset >= nextTimeMill){
				Intent intent = new Intent();
				intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
				intent.putExtra("lrcMessage", message);
				sendBroadcast(intent);
				message = (String)messages.poll();
				nextTimeMill = (Long)times.poll();
				
			}
			currentTimeMill = currentTimeMill + 10;
			//每隔10毫秒执行一次更新
			handler.postDelayed(updateTimeCallback, 10);
		}		
	}
	class UpdateSeekbarProgress implements Runnable{
		int seekbarprogress;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//循环获取当前播放时间
			int seekbarprogress_duration=mediaPlayer.getDuration();
			int seekbarprogress_currentt=mediaPlayer.getCurrentPosition();
			int seekbarprogress_time=seekbarprogress_currentt*100/seekbarprogress_duration;
			System.out.println("mediaPlayer.seekbarprogress_currentt-->"+seekbarprogress_currentt);
			System.out.println("mediaPlayer.seekbarprogress_duration-->"+seekbarprogress_duration);
			System.out.println("mediaPlayer.seekbarprogress_time-->"+seekbarprogress_time);
			Intent intent=new Intent();
			intent.setAction(AppConstant.SEEKBAR_PROGRESS_ACTION);
			intent.putExtra("seekbarprogress",seekbarprogress_time);
			sendBroadcast(intent);
			handler.postDelayed(updateSeekbarProgress, 10);
		}
	}


}
