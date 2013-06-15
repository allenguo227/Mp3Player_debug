package guo.mp3player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import guo.model.Mp3Info;
import guo.utils.FileUtils;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 本地显示列表
 */
public class LocalMp3ListActivity extends ListActivity {
	
	private List<Mp3Info> mp3Infos=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//创建文件工具对象
		FileUtils fileUtils=new FileUtils();
		mp3Infos=fileUtils.getMp3Files("mp3/");
		//判断MP3infos是否为空，如果不为空才读取、
		if(mp3Infos!=null){
		if(!mp3Infos.isEmpty()){
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
		//否则告知MP3为空
			else{
				Toast.makeText(LocalMp3ListActivity.this, "MP3目录为空", Toast.LENGTH_LONG).show();
			}
		}
			else{
				Toast.makeText(LocalMp3ListActivity.this, "MP3路径不存在", Toast.LENGTH_LONG).show();
			}
		super.onResume();
	}
	//list中相关歌曲被点击后，发送该歌曲所在position到播放页面的activity
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setClass(this, PlayerActivity.class);
		intent.putExtra("position", position);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
}
