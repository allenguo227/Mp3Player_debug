package guo.mp3player;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import guo.fragment.*;
/**
 *欢迎页面 
 */

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		AnimationSet animationset=new AnimationSet(true);
		AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(500);
		animationset.addAnimation(alphaAnimation);
		new Handler().postDelayed(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent =new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				WelcomeActivity.this.startActivity(intent);
				WelcomeActivity.this.finish();
			}
			
		},500);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
