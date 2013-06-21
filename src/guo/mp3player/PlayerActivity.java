package guo.mp3player;


import java.util.List;
import guo.model.Mp3Info;
import guo.mp3player.RemoteMp3ListActivity.ExitAppBroadcastReceiver;
import guo.mp3player.service.PlayerService;
import guo.utils.FileUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * 
 * MP3播放界面
 * @author Administrator
 *
 */
@SuppressLint("WorldReadableFiles")
public class PlayerActivity extends Activity implements OnTouchListener{
	private List<Mp3Info> mp3Infos=null;
	private Button playButton;
	private Button nextButton;
	private Button preButton;
	private Button menuButton;
	private Button exitButton;
	private SeekBar seekbar;
	private TextView lrcTextView;
	private TextView musicName;
	private BroadcastReceiver receiver;
	private BroadcastReceiver seekbarReceiver;
	private Mp3Info mp3Info;
	private IntentFilter intentFilter;
	private IntentFilter intentFilter_seek;
	private boolean isPlaying=true;
	private boolean isPlaying_lastState;
	private int position;
	private int lastPosition;
	private int restartPosition=-1;
	private IntentFilter exitApp_IntentFilter;
	private BroadcastReceiver exitReceiver;
	private PopupWindow popupMenu;
	private View exit_menu;
	private int seekbarProgress=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("PlayerActivity----->onCreate--->");
		System.out.println("onCreate---seekbarProgress--->"+seekbarProgress);
		setContentView(R.layout.activity_player);
		viewInit();
		listenerSetOn();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("PlayerActivity重新开始isPlaying----->"+isPlaying);
		System.out.println("onRestart---position--->"+position);
		//临时存储restart回来position的值
		restartPosition=position;
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("PlayerActivity---->onStart");
		System.out.println("PlayerActivity开始isPlaying----->"+isPlaying);
		System.out.println("onStart---seekbarProgress--->"+seekbarProgress);
		super.onStart();
		FileUtils fileUtils=new FileUtils();
		//获取MP3目录下所有歌曲信息
		mp3Infos=fileUtils.getMp3Files("mp3/");
		Intent intent=getIntent();
		//接收position，获取该mp3所在的位置，如果无法接收，默认值为0
		position=intent.getIntExtra("position", 0);
		//判断restartPosition是否为初始值，如果为否，把该值重新赋给position
		if(restartPosition!=-1){
			position=restartPosition;
		}
		//动态注册广播
		setMusicName();
		receiver=new LrcMessageBroadcastReceiver();
		registerReceiver(receiver,getLrcIntentFileter());
		seekbarReceiver=new SeekbarBroadCastReceiver();
		registerReceiver(seekbarReceiver,getSeekBarIntentFileter());
		exitReceiver=new ExitAppBroadcastReceiver();
		registerReceiver(exitReceiver,getExitApp_IntentFilter());
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("PlayerActivity---->onPause");
		System.out.println("onPause---position--->"+position);
		System.out.println("onPause---seekbarProgress--->"+seekbarProgress);
		saveActivityPreferences();
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("PlayerActivity---->onResume");
		lastPosition=getActivityPreferences();
		isPlaying_lastState=getPlayingLastState();
		int seekbarProgressone=getSeekbarProgress();
		seekbar.setProgress(seekbarProgressone);
		isPlaying=isPlaying_lastState;
		System.out.println("onResume---position--->"+position);
		System.out.println("onResume---seekbarProgress--->"+seekbarProgress);
		System.out.println("onResume---seekbarProgressone--->"+seekbarProgressone);
		autoPlay();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("PlayerActivity---->onDestroy");
		System.out.println("PlayerActivity退出该ACtivity,isPlaying----->"+isPlaying);
		System.out.println("PlayerActivity退出该ACtivity,seekbarProgress----->"+seekbarProgress);
		super.onDestroy();
		popupMenu=null;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("PlayerActivity---->onStop");
		System.out.println("PlayerActivity-seekbarProgress--->onStop--->"+seekbarProgress);
		super.onStop();
		unregisterReceiver(receiver);
		unregisterReceiver(seekbarReceiver);
		unregisterReceiver(exitReceiver);
	}
	//使用sharedPreferences保存当前position状态
	protected void saveActivityPreferences(){
	// 仅限于本activity使用
	SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
	// 打开sharedperferences编辑
	SharedPreferences.Editor editor = activityPreferences.edit(); 
	//存入position
	editor.putInt("lastPosition",position);
	editor.putInt("seekbarProgress",seekbarProgress);
	editor.putBoolean("isplayinglaststate",isPlaying);
	// Commit changes.
	editor.commit();
	}
	//用于取出sharedperference中的position值
	protected int getActivityPreferences(){
		SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
		int lastPosition = activityPreferences.getInt("lastPosition", -1); 
		return lastPosition;
	}
	protected int getSeekbarProgress(){
		SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
		int seekbarProgress = activityPreferences.getInt("seekbarProgress", -1); 
		return seekbarProgress;
	}
	protected boolean getPlayingLastState(){
		SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
		Boolean isPlaying_lastState = activityPreferences.getBoolean("isplayinglaststate",true);
		return isPlaying_lastState;
	}
	//初始化组件
	private void viewInit(){
		playButton =(Button)findViewById(R.id.play_music);
		nextButton=(Button)findViewById(R.id.next_music);
		preButton=(Button)findViewById(R.id.previous_music);
		menuButton=(Button)findViewById(R.id.play_menu);
		seekbar=(SeekBar)findViewById(R.id.audioTrack);
		lrcTextView=(TextView)findViewById(R.id.lrcTextView);
		musicName=(TextView)findViewById(R.id.musicName_play);
		exit_menu=this.getLayoutInflater().inflate(R.layout.popupwindow_playactivity, null);
		popupMenu=new PopupWindow(exit_menu,150, 150);
		exitButton=(Button)exit_menu.findViewById(R.id.exit_popupwindow_playactivity);
	}
	//绑定相关按钮
	private void listenerSetOn(){
		playButton.setOnClickListener(new PlayButtonListener());
		nextButton.setOnClickListener(new NextButtonListener());
		preButton.setOnClickListener(new PreButtonListener());
		menuButton.setOnClickListener(new MenuOnClickListener());
		exitButton.setOnClickListener(new ExitButtonListener());
		seekbar.setOnSeekBarChangeListener(new  SeekbarChangeListener());
	}
	private void setMusicName(){
		mp3Info=mp3Infos.get(position);
		musicName.setText(mp3Info.getMp3Name());
	}
	private void autoPlay(){
		//如果播放歌曲与退出前不一致，则执行播放。
		if(position!=lastPosition){
				System.out.println("lastPosition----isPlaying--->"+isPlaying);
					mp3Info=mp3Infos.get(position);
					Intent intent=new Intent();
					intent.setClass(PlayerActivity.this,PlayerService.class);
					//传递该MP3信息到服务
					intent.putExtra("mp3Info", mp3Info);
					//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
					intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
					//启动服务
					startService(intent);
					isPlaying=true;
					playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
					//如果退出前与上一次播放一致。则不变
			}else if(position==lastPosition){
				//判断当前是否为播放状态，如果是的话，则按钮为暂停按钮
				System.out.println("不换歌返回时isPlaying---->"+isPlaying);
				if(isPlaying){
					playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
				}else{
					playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_selector));
				}
			}
	}
	private class PlayButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//通过service播放MP3
			if(!isPlaying){
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
	private class NextButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(isPlaying){
				if(position<mp3Infos.size()-1){
					++position;
				}else{
					position=0;
				}
			mp3Info=mp3Infos.get(position);
			Intent intent=new Intent();
			intent.setClass(PlayerActivity.this,PlayerService.class);
			//传递该MP3信息到服务
			intent.putExtra("mp3Info", mp3Info);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
			//启动服务
			startService(intent);
			isPlaying=true;
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			setMusicName();
			}
		}
	}
	private class PreButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(isPlaying){
				if(position>0){
					--position;
				}
				else{
					position=mp3Infos.size()-1;
				}
			mp3Info=mp3Infos.get(position);
			Intent intent=new Intent();
			intent.setClass(PlayerActivity.this,PlayerService.class);
			//传递该MP3信息到服务
			intent.putExtra("mp3Info", mp3Info);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);
			//启动服务
			startService(intent);
			isPlaying=true;
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			setMusicName();
			}
		}
	}
	//进度条监听器
	private class SeekbarChangeListener implements OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			//如果进度被手动改变，把进度值发给服务，服务接收判断后，修改mediaplayer进度
			if(fromUser==true){
				Intent intent=new Intent();
				 	intent.putExtra("progress_change", progress);
					intent.setClass(PlayerActivity.this,PlayerService.class);
					intent.putExtra("MSG", AppConstant.PlayerMsg.SEEK_MSG);
					startService(intent);
					seekbarProgress=progress;
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
	private class MenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("MenuOnClickListener");
			popupMenu.showAsDropDown(v);
			popupMenu.showAtLocation(findViewById(R.id.play_menu), Gravity.LEFT, 30, 0);
			popupMenu.setFocusable(false);
			popupMenu.setOutsideTouchable(true);
		}
	}
	private class ExitButtonListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent stop_intent=new Intent();
			stop_intent.setAction(AppConstant.EXITAPP_ACTION);
			sendBroadcast(stop_intent);
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
	private class SeekbarBroadCastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			//这里写接收到的数据：通过broadcast接收service发送过来的intent的数据。
			seekbarProgress=intent.getIntExtra("seekbarprogress",0);
			seekbar.setProgress(seekbarProgress);
		}
		
	}
	private class ExitAppBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isPlaying=false;
			saveActivityPreferences();
			finish();
		}
	}
	//关闭activity的broadcast过滤器
	private IntentFilter getExitApp_IntentFilter(){
		if(exitApp_IntentFilter==null){
			 exitApp_IntentFilter=new IntentFilter();
			 exitApp_IntentFilter.addAction(AppConstant.EXITAPP_ACTION);
		}
		return exitApp_IntentFilter;
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
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(popupMenu!=null&&popupMenu.isShowing())
		{
			popupMenu.dismiss();
		}
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}


