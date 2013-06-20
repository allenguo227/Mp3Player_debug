package guo.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import guo.mp3player.LocalMp3ListActivity;
import guo.mp3player.R;
import guo.mp3player.RemoteMp3ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;


/**
 * @author Administrator
 *第一个Fragment，Activity相关的内容在这里处理。
 */
public class My_Fragment extends Fragment {
	 private GridView gridview; 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.mypage_one, container, false);
		gridview = (GridView) v.findViewById(R.id.gridview);  
		  
        // 生成动态数组，并且转入数据  
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  
      
            HashMap<String, Object> map1 = new HashMap<String, Object>();  
            map1.put("ItemImage", R.drawable.wenjianjia);// 添加图像资源的ID  
            map1.put("ItemText", "下载管理" );
            HashMap<String, Object> map2 = new HashMap<String, Object>();  
            map2.put("ItemImage", R.drawable.localmusic);// 添加图像资源的ID  
            map2.put("ItemText", "本地音乐" );
            lstImageItem.add(map1);  
            lstImageItem.add(map2);  
   
        // 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(), // 没什么解释  
                lstImageItem,// 数据来源  
                R.layout.mypage_item,// night_item的XML实现  
                // 动态数组与ImageItem对应的子项  
                new String[] { "ItemImage", "ItemText" },  
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] { R.id.ItemImage, R.id.ItemText });  
        // 添加并且显示  
        gridview.setAdapter(saImageItems);  
        // 添加消息处理  
        gridview.setOnItemClickListener(new ItemClickListener());  
        return v;
    }  
	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件 
	class ItemClickListener implements OnItemClickListener {  
	    public void onItemClick(AdapterView<?> arg0,// The AdapterView where the click happened  
	            View arg1,// The view within the AdapterView that was clicked  
	            int arg2,// The position of the view in the adapter  
	            long arg3// The row id of the item that was clicked  
	    ) {  
	        if(arg2==0)
	        {
	        	Intent remoteIntent=new Intent();
	    		remoteIntent.setClass(getActivity(), RemoteMp3ListActivity.class);
	    		getActivity().startActivity(remoteIntent);
	        }
	        if(arg2==1)
	        {
	        	Intent localIntent=new Intent();
	        	localIntent.setClass(getActivity(), LocalMp3ListActivity.class);
	        	getActivity().startActivity(localIntent);
	        }
	}
	}
}


