package guo.mp3player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Queue;

import lrc.LrcProcessor;

import guo.model.Mp3Info;
import guo.mp3player.service.PlayerService;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends Activity {
	
	private ImageButton beginButton=null;
	private ImageButton pauseButton=null;
	private ImageButton stopButton=null;
	private SeekBar seekbar=null;

	private TextView lrcTextView=null;
	private BroadcastReceiver receiver=null;
	private BroadcastReceiver seekbarReceiver=null;
	private Mp3Info mp3Info=null;
	private IntentFilter intentFilter;
	private IntentFilter intentFilter_seek;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		//ͨ通过intent获取 MP3info对象
		Intent intent=getIntent();
		mp3Info=(Mp3Info) intent.getSerializableExtra("mp3Info");
		beginButton =(ImageButton)findViewById(R.id.begin);
		pauseButton=(ImageButton)findViewById(R.id.pause);
		stopButton=(ImageButton)findViewById(R.id.stop);
		beginButton.setOnClickListener(new BeginButtonListener());
		pauseButton.setOnClickListener(new PauseButtonListener());
		stopButton.setOnClickListener(new StopButtonListener());
		lrcTextView=(TextView)findViewById(R.id.lrcText);
		seekbar=(SeekBar)findViewById(R.id.seekBar1);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver=new LrcMessageBroadcastReceiver();
		registerReceiver(receiver,getLrcIntentFileter());
		seekbarReceiver=new SeekbarBroadCastReceiver();
		registerReceiver(seekbarReceiver,getSeekBarIntentFileter());
	}

	class BeginButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//通过service播放MP3
			Intent intent=new Intent();
			intent.setClass(PlayerActivity.this,PlayerService.class);
			intent.putExtra("mp3Info", mp3Info);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
			startService(intent);
			}
		}

	class PauseButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//ͨservice 暂停MP3
			Intent intent=new Intent();
		//	intent.putExtra("mp3info", mp3Info);
			intent.setClass(PlayerActivity.this,PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
			startService(intent);
		}
	}
	class StopButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
		//	intent.putExtra("mp3info", mp3Info);
			intent.setClass(PlayerActivity.this,PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
			startService(intent);
		}
	}
	/**
	 * 
	 */
	class LrcMessageBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			//通过broadcast接收service发送过来的intent的数据。
			String lrcMessage=intent.getStringExtra("lrcMessage");
			lrcTextView.setText(lrcMessage);
		}
		
	}
	//接收广播用于更新播放器进度条
	class SeekbarBroadCastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			//这里写接收到的数据：通过broadcast接收service发送过来的intent的数据。
			int seekbarProgress=intent.getIntExtra("seekbarprogress",0);
			
			seekbar.setProgress(seekbarProgress);
		}
		
	}
	//歌词broadcast过滤器
	private IntentFilter getLrcIntentFileter(){
		if(intentFilter==null){
			intentFilter=new IntentFilter();
			intentFilter.addAction(AppConstant.LRC_MESSAGE_ACTION);
		}
		return intentFilter;
	}
	//进度条broadcast过滤器
	private IntentFilter getSeekBarIntentFileter(){
		if(intentFilter_seek==null){
			intentFilter_seek=new IntentFilter();
			intentFilter_seek.addAction(AppConstant.SEEKBAR_PROGRESS_ACTION);
		}
		return intentFilter_seek;
	}
}


