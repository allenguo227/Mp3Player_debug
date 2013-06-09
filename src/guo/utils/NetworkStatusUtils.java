package guo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *判断网络是否存在的工具类
 */
public class NetworkStatusUtils {
public boolean isNetworkConnected(Context context) { 
	if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
					.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null) { 
				return mNetworkInfo.isAvailable(); 
			} 
	} 
	return false; 
	} 
}
