package guo.mp3player.service;


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
	private int SEEKBARCHANGE;
	int seekbar_duration;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	//启动service时的回调函数（生命周期）。通过intent获取到的常量信息，判断当前MP3播放状态
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mp3Info=(Mp3Info) intent.getSerializableExtra("mp3Info");
		int MSG=intent.getIntExtra("MSG", 0);
		SEEKBARCHANGE=intent.getIntExtra("progress_change", 0);
		switch(MSG){
		case AppConstant.PlayerMsg.PLAY_MSG:
			play(mp3Info);
			break;
		case AppConstant.PlayerMsg.PAUSE_MSG:
			pause();
			break;
		case AppConstant.PlayerMsg.SEEK_MSG:
			seekbarControl();
			break;
		default:
			System.out.println("default--->");
			break;
		}
		/*if(MSG==AppConstant.PlayerMsg.PLAY_MSG){
			nextMusic(mp3Info);
		}else	if(MSG==AppConstant.PlayerMsg.PAUSE_MSG){
			pause();
		}else if(MSG==AppConstant.PlayerMsg.PRIVIOUS_MSG){
			
		}else if(MSG==AppConstant.PlayerMsg.NEXT_MSG){
			nextMusic(mp3Info);
		}else if(MSG==AppConstant.PlayerMsg.SEEK_MSG){
			seekbarControl();
		}*/
		return super.onStartCommand(intent, flags, startId);
	}
	//通过seekbar改变传过来的值，改变歌曲播放位置
	private void seekbarControl(){
		seekbar_duration=mediaPlayer.getDuration();
		mediaPlayer.seekTo(SEEKBARCHANGE*seekbar_duration/100);
	}
	//播放歌曲的方法
	private void play(Mp3Info mp3Info){
		//如果不为空，创建MP3
		if(mediaPlayer==null){
			mediaPlayer=MediaPlayer.create(this, Uri.parse("file://"+getMp3Path(mp3Info)));
		}
		//播放mediaplayer
		mediaPlayer.start();
		//创建通知歌词改变的对象
		prepareLrc(mp3Info.getLrcName());
		//创建通知seekbar改变的对象
		prepareSeekbar();
		begin=System.currentTimeMillis();
		//每5毫秒post一次，更新
		handler.postDelayed(updateTimeCallback, 5);
		handler.postDelayed(updateSeekbarProgress, 5);
	}
	private void pause(){
			mediaPlayer.pause();
			handler.removeCallbacks(updateTimeCallback);
			handler.removeCallbacks(updateSeekbarProgress);
			pauseTimeMills = System.currentTimeMillis() ;
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
	//获取MP3所在路径
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
			Intent intent=new Intent();
			intent.setAction(AppConstant.SEEKBAR_PROGRESS_ACTION);
			intent.putExtra("seekbarprogress",seekbarprogress_time);
			sendBroadcast(intent);
			handler.postDelayed(updateSeekbarProgress, 10);
		}
	}


}
