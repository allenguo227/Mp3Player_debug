package guo.mp3player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import guo.model.Mp3Info;
import guo.mp3player.service.PlayerService;
import guo.utils.FileUtils;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 本地显示列表
 */
@SuppressLint("WorldReadableFiles")
public class LocalMp3ListActivity extends ListActivity implements OnTouchListener {
	
	private Button playButton;
	private Button nextButton;
	private Button preButton;
	private TextView musicName_Playing;
	private Button localList_menu=null;
	private List<Mp3Info> mp3Infos=null;
	private IntentFilter exitApp_IntentFilter;
	private IntentFilter intentFilter_seek;
	private BroadcastReceiver exitReceiver;
	private BroadcastReceiver seekbarReceiver;
	private PopupWindow popupMenu;
	private SeekBar seekbar;
	private RelativeLayout local_mp3_list_RelativeLayout;
	private View root;
	private boolean isPlaying;
	private Mp3Info mp3Info;
	private int position_local=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		viewInit();
		registerReceiver();
		setListener();
	}
	
	
	//初始化组件
	private void viewInit(){
		playButton =(Button)findViewById(R.id.play_music);
		nextButton=(Button)findViewById(R.id.next_music);
		preButton=(Button)findViewById(R.id.previous_music);
		seekbar=(SeekBar)findViewById(R.id.audioTrack_local);
		musicName_Playing=(TextView)findViewById(R.id.musicname_list);
		localList_menu=(Button)findViewById(R.id.locallist_popupwindow_menu);
		root=this.getLayoutInflater().inflate(R.layout.popupwindow_local_mp3_list, null);
		local_mp3_list_RelativeLayout=(RelativeLayout)findViewById(R.id.local_mp3_list_RelativeLayout);
		//使用popupwindow 弹出对话框
		popupMenu=new PopupWindow(root,	250, 150);
	}
	//为广播注册绑定过滤器
	private void registerReceiver(){
		exitReceiver=new ExitAppBroadcastReceiver();
		registerReceiver(exitReceiver,getExitApp_IntentFilter());
		seekbarReceiver=new SeekbarBroadCastReceiver();
		registerReceiver(seekbarReceiver,getSeekBarIntentFileter());
	}
	//为组件绑定监听器
	private void setListener(){
		local_mp3_list_RelativeLayout.setOnTouchListener(this);
		localList_menu.setOnClickListener(new MenuOnClickListener());
		//popupwindow中的两个按钮创建监听
		root.findViewById(R.id.exit_popupwindow).setOnClickListener(new ExitPopupwindowOnClickListener());
		root.findViewById(R.id.reload_popupwindow).setOnClickListener(new  ReloadOnClickListener());
		seekbar.setOnSeekBarChangeListener(new SeekbarChangeListener());
		playButton.setOnClickListener(new PlayButtonListener());
		nextButton.setOnClickListener(new NextButtonListener());
		preButton.setOnClickListener(new PreButtonListener());
	}
	@Override
	protected void onResume() {
		isPlaying=getPlayingLastState();
		if(isPlaying){
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			}else if(!isPlaying){
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_selector));
		}
		System.out.println("LocalMp3ListActivity开始时__isplaying--->"+isPlaying);
		// TODO Auto-generated method stub
		//创建文件工具对象
		FileUtils fileUtils=new FileUtils();
		mp3Infos=fileUtils.getMp3Files("mp3/");
		mp3Info=mp3Infos.get(position_local);
		setPlayingMusicName();
		//判断MP3infos是否为空，如果不为空才读取、
		if(mp3Infos!=null){
			if(!mp3Infos.isEmpty()){
				loadMusiclist();
			}else{//否则告知MP3为空
				loadMusiclist();
				Toast.makeText(LocalMp3ListActivity.this, "MP3目录为空", AppConstant.Time.ONE_SECOND).show();
				
			}
		}else{
				Toast.makeText(LocalMp3ListActivity.this, "MP3路径不存在", AppConstant.Time.ONE_SECOND).show();
			}		
	
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveActivityPreferences();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(exitReceiver);
		unregisterReceiver(seekbarReceiver);
		popupMenu=null; 
	}
	//使用sharedPreferences保存当前播放状态
	protected void saveActivityPreferences(){
	// 仅限于本activity使用
		SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
			//getPreferences(Activity.MODE_PRIVATE);	 
	// 打开sharedperferences编辑
	SharedPreferences.Editor editor = activityPreferences.edit(); 
	//存入position
	editor.putBoolean("isplayinglaststate",isPlaying);
	// Commit changes.
	editor.commit();
	}
	//获取当前播放状态
	protected boolean getPlayingLastState(){
		SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE);  
		Boolean isPlaying_lastState = activityPreferences.getBoolean("isplayinglaststate",false);
		return isPlaying_lastState;
	}
	//设置当前播放歌曲名称
	private void setPlayingMusicName(){
		musicName_Playing.setText(mp3Info.getMp3Name());
	}
	//更新本地歌曲列表
	private void loadMusiclist(){
		//用以显示SD卡中内容的listview
		List<HashMap<String,String>>list=new ArrayList<HashMap<String,String>>();
		int mp3_num=1;
		for (Iterator iterator = mp3Infos.iterator();  iterator.hasNext();) {
			Mp3Info mp3Info=(Mp3Info)iterator.next();
			HashMap<String,String>map=new HashMap<String, String>();
			map.put("mp3_name",mp3_num+++" "+mp3Info.getMp3Name());
			map.put("mp3_size", mp3Info.getMp3Size());
			list.add(map);
		}
		//把sd卡内容写入到listview中
		SimpleAdapter simpleAdapter=new SimpleAdapter(this,list,R.layout.mp3_local_list_item,new String[]{"mp3_name","mp3_size"},new int[]{R.id.mp3_name,R.id.mp3_size});
		setListAdapter(simpleAdapter);	
	}
	//list中相关歌曲被点击后，发送该歌曲所在position到播放页面的activity
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		position_local=position;
		Intent intent=new Intent();
		intent.setClass(this, PlayerActivity.class);
		System.out.println("position---->"+position);
		intent.putExtra("position", position);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	private class PlayButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//通过service播放MP3
			if(!isPlaying){
			//通过该MP3位置获取该MP3信息
			mp3Info=mp3Infos.get(position_local);
			Intent intent=new Intent();
			intent.setClass(LocalMp3ListActivity.this,PlayerService.class);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
			intent.putExtra("mp3Info", mp3Info);
			//启动服务
			startService(intent);
			isPlaying=true;
			//点击播放后，播放器play按钮将改变为pause按钮
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			}else{
				//暂停播放
				Intent intent=new Intent();
				intent.setClass(LocalMp3ListActivity.this,PlayerService.class);
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
				if(position_local<mp3Infos.size()-1){
					++position_local;
				}else{
					position_local=0;
				}
			mp3Info=mp3Infos.get(position_local);
			setPlayingMusicName();
			Intent intent=new Intent();
			intent.setClass(LocalMp3ListActivity.this,PlayerService.class);
			//传递该MP3信息到服务
			intent.putExtra("mp3Info", mp3Info);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
			//启动服务
			startService(intent);
			isPlaying=true;
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			//setMusicName();
			}
		}
	}
	private class PreButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(isPlaying){
				if(position_local>0){
					--position_local;
				}
				else{
					position_local=mp3Infos.size()-1;
				}
			mp3Info=mp3Infos.get(position_local);
			setPlayingMusicName();
			Intent intent=new Intent();
			intent.setClass(LocalMp3ListActivity.this,PlayerService.class);
			//传递该MP3信息到服务
			intent.putExtra("mp3Info", mp3Info);
			//传递该动作的状态到服务，服务接收后，判断状态，做出播放的响应
			intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);
			//启动服务
			startService(intent);
			isPlaying=true;
			playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_selector));
			//setMusicName();
			}
		}
	}
	class MenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			popupMenu.showAsDropDown(v);
			popupMenu.showAtLocation(findViewById(R.id.locallist_popupwindow_menu), Gravity.LEFT, 20, 20);
			popupMenu.setFocusable(false);
			popupMenu.setOutsideTouchable(true);
		}
	}
	class ExitPopupwindowOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent stop_intent=new Intent();
			stop_intent.setAction(AppConstant.EXITAPP_ACTION);
			sendBroadcast(stop_intent);
		}
	}
	class ReloadOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("reload");
			FileUtils fileUtils=new FileUtils();
			mp3Infos=fileUtils.getMp3Files("mp3/");
			 loadMusiclist();
			 popupMenu.dismiss();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getAction()==MotionEvent.ACTION_UP){
			if(popupMenu!=null&&popupMenu.isShowing())
			{
				popupMenu.dismiss();
		System.out.println("dispatchTouchEvent");
		}
		}
		return super.dispatchTouchEvent(ev);
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
					intent.setClass(LocalMp3ListActivity.this,PlayerService.class);
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
	private class ExitAppBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isPlaying=false;
			saveActivityPreferences();
		finish();
		}
		
	}
	private class SeekbarBroadCastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			//这里写接收到的数据：通过broadcast接收service发送过来的intent的数据。
			int seekbarProgress=intent.getIntExtra("seekbarprogress",0);
			seekbar.setProgress(seekbarProgress);
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
	//进度条broadcast过滤器
	private IntentFilter getSeekBarIntentFileter(){
		if(intentFilter_seek==null){
			intentFilter_seek=new IntentFilter();
			intentFilter_seek.addAction(AppConstant.SEEKBAR_PROGRESS_ACTION);
		}
		return intentFilter_seek;
	}
	
	
}
