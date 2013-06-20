package guo.fragment;

import guo.mp3player.AppConstant;
import guo.mp3player.R;
import guo.mp3player.service.PlayerService;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  程序主页面
 */
public class MainActivity extends FragmentActivity {
	private ViewPager mPager;//页卡内容
	private ArrayList<Fragment> listViews; //Tab页面列表
	private ImageView cursor;//动画图片
	private TextView myMusic, searchMusic, recommendMusic;//页卡头标
	private int offset = 0;//动画图片偏移量
	private int currIndex = 0;//当前页卡编号
	private int bmpW;//动画图片宽度
	private IntentFilter exitApp_IntentFilter;
	private BroadcastReceiver exitReceiver;
	My_Fragment oneFragment=new My_Fragment();
	SearchMusic_Fragment twoFragment=new SearchMusic_Fragment();
	RecommendMusic_Fragment thrFragment=new RecommendMusic_Fragment();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initTextView();
		initViewPager() ;
		initImageView();
		exitReceiver=new ExitAppBroadcastReceiver();
		registerReceiver(exitReceiver,getExitApp_IntentFilter());
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent stop_intent=new Intent();
		stop_intent.setClass(MainActivity.this, PlayerService.class);
		stopService(stop_intent);
		saveActivityPreferences();
		unregisterReceiver(exitReceiver);
		System.exit(0);
	}
	//退出程序时把播放状态改为false
	protected void saveActivityPreferences(){
	// 仅限于本activity使用
	SharedPreferences activityPreferences=getSharedPreferences("SharePreference_playing_state", Context.MODE_WORLD_READABLE); 
	// 打开sharedperferences编辑
	SharedPreferences.Editor editor = activityPreferences.edit(); 
	//存入position
	editor.putBoolean("isplayinglaststate",false);
	// Commit changes.
	editor.commit();
	}
	private void initTextView() {
		myMusic = (TextView) findViewById(R.id.my_text);
		searchMusic= (TextView) findViewById(R.id.search_text);
		recommendMusic = (TextView) findViewById(R.id.recommend_text);
		myMusic.setOnClickListener(new MyOnClickListener(0));
		searchMusic.setOnClickListener(new MyOnClickListener(1));
		recommendMusic.setOnClickListener(new MyOnClickListener(2));
		 }
	/**
	* 头标点击监听
	*/
	public class MyOnClickListener implements View.OnClickListener {
	private int index = 0;
	public MyOnClickListener(int i) {
	index = i;
	}
	@Override
	public void onClick(View v) {
	mPager.setCurrentItem(index);
	}
	};
	/**
	* 初始化ViewPager，并适配fragment
	*/
	private void initViewPager() {
	mPager = (ViewPager) findViewById(R.id.vPager);
	listViews = new ArrayList<Fragment>();
	listViews.add(oneFragment);
	listViews.add(twoFragment);
	listViews.add(thrFragment);
	//用support包，只能用getSupportFragmentManager();
	FragmentManager fragmentManager =this.getSupportFragmentManager();
	//通过fragment适配器把fragment添加入viewpager中
	mPager.setAdapter(new MainFragmentPagerAdapter(fragmentManager,listViews));
	mPager.setCurrentItem(0);
	myMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_on));
	mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	/**
	* 初始化动画
	*/
	private void initImageView() {
	cursor = (ImageView) findViewById(R.id.cursor);
	bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();//获取图片宽度
	DisplayMetrics dm = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(dm);
	int screenW = dm.widthPixels;//获取分辨率宽度
	offset = (screenW/3-bmpW)/2;//计算偏移量
	Matrix matrix = new Matrix();
	matrix.postTranslate(offset, 0);
	cursor.setImageMatrix(matrix);//设置动画初始位置
	}
	/**
	* 页卡切换监听,改变动画位置
	*/
	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one,two;
	@Override
	public void onPageSelected(int arg0) {
		one = offset * 2 + bmpW;//页卡1 -> 页卡2 偏移量
		two = one * 2;//页卡1 -> 页卡3 偏移量
	Animation animation = null;
	switch(arg0) {
	case 0:
		myMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_on));
		searchMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
		recommendMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
	if(currIndex == 1) {
	animation = new TranslateAnimation(one, 0, 0, 0);
	} else if(currIndex == 2) {
	animation = new TranslateAnimation(two, 0, 0, 0);
	}
	break;
	case 1:
		searchMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_on));
		myMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
		recommendMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
	if(currIndex == 0) {
	animation = new TranslateAnimation(offset, one, 0, 0);
	} else if(currIndex == 2) {
	animation = new TranslateAnimation(two, one, 0, 0);
	}
	break;
	case 2:
		recommendMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_on));
		myMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
		searchMusic.setBackgroundDrawable(getResources().getDrawable(R.drawable.mypage_bg_un));
	if(currIndex == 0) {
	animation = new TranslateAnimation(offset, two, 0, 0);
	} else if(currIndex == 1) {
	animation = new TranslateAnimation(one, two, 0, 0);
	}
	break;
	}
	currIndex = arg0;
	animation.setFillAfter(true);//True:图片停在动画结束位置
	animation.setDuration(300);
	cursor.startAnimation(animation);
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	}
	class ExitAppBroadcastReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Intent stop_intent=new Intent();
			stop_intent.setClass(MainActivity.this, PlayerService.class);
			stopService(stop_intent);
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
	
}
