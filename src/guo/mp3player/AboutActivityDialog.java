package guo.mp3player;

import guo.mp3player.R;
import guo.mp3player.R.layout;
import guo.mp3player.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *  关于的弹出对话框
 * @author Administrator
 *
 */
public class AboutActivityDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mp3_about_activity_dialog);
		Button button=(Button)findViewById(R.id.return01);
		button.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
			finish();	
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_activity_dialog, menu);
		return true;
	}

}
