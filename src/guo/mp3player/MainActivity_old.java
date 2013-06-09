package guo.mp3player;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity_old extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//创建tabHost对象
		TabHost tabHost=getTabHost();
		// 创建第一个tab页面
		Intent remoteIntent=new Intent();
		remoteIntent.setClass(this, RemoteMp3ListActivity.class);
		//
		TabHost.TabSpec remoteSpec=tabHost.newTabSpec("Remote");
		Resources res=getResources();
		//
		remoteSpec.setIndicator("Remote",res.getDrawable(android.R.drawable.stat_sys_download));
		//
		remoteSpec.setContent(remoteIntent);
		//
		tabHost.addTab(remoteSpec);
	
		//创建第二个tab页面
		Intent localIntent=new Intent();
		localIntent.setClass(this, LocalMp3ListActivity.class);
		TabHost.TabSpec localSpec=tabHost.newTabSpec("Local");
		localSpec.setIndicator("local",res.getDrawable(android.R.drawable.stat_sys_upload));
		localSpec.setContent(localIntent);
		tabHost.addTab(localSpec);
	}
					
}
