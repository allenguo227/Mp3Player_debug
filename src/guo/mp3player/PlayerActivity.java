package guo.mp3player;


import java.util.List;
import guo.model.Mp3Info;
import guo.mp3player.service.PlayerService;
import guo.utils.FileUtils;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * 
 * MP3播放界面
 * @author Administrator
 *
 */
public class PlayerActivity extends Activity {
	private List<Mp3Info> mp3Infos=null;
	private Button playButton;
	private Button nextButton;
	private Button preButton;
	private SeekBar seekbar;
	private TextView lrcTextView;
	private BroadcastReceiver receiver;
	private BroadcastReceiver seekbarReceiver;
	private Mp3Info mp3Info;
	private IntentFilter intentFilter;
	private IntentFilter intentFilter_seek;
	private boolean isPlaying=false;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		viewInit();
		listenerSetOn();
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
		//动态注册广播
		receiver=new LrcMessageBroadcastReceiver();
		registerReceiver(receiver,getLrcIntentFileter());
		seekbarReceiver=new SeekbarBroadCastReceiver();
		registerReceiver(seekbarReceiver,getSeekBarIntentFileter());
		
	}
	//初始化组件
	public void viewInit()
	{
		FileUtils fileUtils=new FileUtils();
		//获取MP3目录下所有歌曲信息
		mp3Infos=fileUtils.getMp3Files("mp3/");
		Intent intent=getIntent();
		//接收position，获取该mp3所在的位置，如果无法接收，默认值为0
		position=intent.getIntExtra("position", 0);
		playButton =(Button)findViewById(R.id.play_music);
		nextButton=(Button)findViewById(R.id.next_music);
		preButton=(Button)findViewById(R.id.previous_music);
		seekbar=(SeekBar)findViewById(R.id.audioTrack);
		lrcTextView=(TextView)findViewById(R.id.lrcTextView);
	}
	//绑定相关按钮
	public void listenerSetOn(){
		playButton.setOnClickListener(new PlayButtonListener());
		seekbar.setOnSeekBarChangeListener(new  SeekbarChangeListener());
	}
	class PlayButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//通过service播放MP3
			if(isPlaying==false){
			//通过该MP3位置获取该MP3信息
			mp3Info=mp3Infos.get(position);
			Intent intent=new Intent();
			intent.setClass(PlayerActivity.this,PlayerService.class);
			//传递该MP3信息到服务
			intent.putExtra("mp3Info", mp3Info);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
			//启动服务
			startService(intent);
			isPlaying=true;
			//点击播放后，播放器play按钮将改变为pause按钮
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			}
			else{
				//暂停播放
				Intent intent=new Intent();
				intent.setClass(PlayerActivity.this,PlayerService.class);
				//传递该动作的状态到服务，服务接收后，判断状态，做出暂停的响应
				intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
				startService(intent);
				isPlaying=false;
				//点击播放后，播放器pause按钮将改变为play按钮
				playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_selector));
			}
			}
		}

	//进度条监听器
	class SeekbarChangeListener implements OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			//如果进度被手动改变，把进度值发给服务，服务接收判断后，修改mediaplayer进度
			if(fromUser==true)
			{
				Intent intent=new Intent();
				 	intent.putExtra("progress_change", progress);
					intent.setClass(PlayerActivity.this,PlayerService.class);
					intent.putExtra("MSG", AppConstant.PlayerMsg.SEEK_MSG);
					startService(intent);
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
	}
	/**
	 * 接收来至service的广播，并动态改变歌词
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


