package guo.fragment;

import guo.mp3player.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * @author Administrator
 *第三个Fragment
 */
public class RecommendMusic_Fragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.mypage_three, container, false);
		return v;
	}
}
