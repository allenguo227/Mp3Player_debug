package guo.mp3player;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import guo.download.HttpDownloader;
import guo.fragment.MainActivity;
import guo.model.Mp3Info;
import guo.mp3player.service.DownloadService;
import guo.mp3player.service.PlayerService;
import guo.utils.NetworkStatusUtils;
import guo.xml.Mp3ListContentHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RemoteMp3ListActivity extends ListActivity {

	private static final int UPDATE=1;
	private static final int ABOUT=2;
	private List<Mp3Info>mp3Infos=null;
	private IntentFilter intentFilter;
	private BroadcastReceiver receiver;
	private IntentFilter exitApp_IntentFilter;
	private BroadcastReceiver exitReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_mp3_list);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//绑定广播与过滤器
		receiver=new DownloadBroadcastReceiver();
		registerReceiver(receiver, getIntentFilter());
		exitReceiver=new ExitAppBroadcastReceiver();
		registerReceiver(exitReceiver,getExitApp_IntentFilter());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(exitReceiver);
		unregisterReceiver(receiver);
	}

	/**
	 * 通过menu键调出菜单栏选项,包括‘更新’和‘关于’两项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, UPDATE  ,1,R.string.mp3list_update);
		menu.add(0, ABOUT  ,2,R.string.mp3list_about);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//		System.out.println("Itemid-------->"+item.getItemId());		
		if(item.getItemId()==UPDATE){
		updateListView();
			}
		else if(item.getItemId()==ABOUT){
			//弹出 关于 的对话框页面
			Intent intent=new Intent();
			intent.setClass(RemoteMp3ListActivity.this, AboutActivityDialog.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 更新下载列表
	 */
	private void updateListView() {
		// TODO Auto-generated method stub	
		//判断当前网络状态
		NetworkStatusUtils netStatus=new NetworkStatusUtils();
		boolean isConnected=netStatus.isNetworkConnected(RemoteMp3ListActivity.this);
		System.out.println("isConnected------->"+isConnected);
		if(isConnected){
		String xml=downLoadXML(AppConstant.URL.BASE_URL+"/resources.xml");
		//解析XML得到MP3info
		mp3Infos=parse(xml);
		//把得到的MP3info信息，通过simpleadapter设置到listview中
		SimpleAdapter simpleAdapter=buildSimpleAdapter(mp3Infos);
		setListAdapter(simpleAdapter);
		}
		else{
			Toast.makeText(RemoteMp3ListActivity.this,"当前无网络连接，请检查网络是否打开", Toast.LENGTH_SHORT).show();
		}
	}
	
	//把从服务器获取到的歌曲列表写入到相关listview中
	private SimpleAdapter buildSimpleAdapter(List<Mp3Info>mp3Infos){
		List<HashMap<String,String>>list=new ArrayList<HashMap<String,String>>();
		int mp3_num=1;
		for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info=(Mp3Info)iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			if(mp3Info.getMp3CnName()!=null){
			map.put("mp3_name",mp3_num+++" "+mp3Info.getMp3CnName());
			}
			else{
			map.put("mp3_name",mp3_num+++" "+mp3Info.getMp3Name());
			}
			map.put("mp3_size",mp3Info.getMp3Size());
			list.add(map);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this,list,R.layout.mp3_remote_list_item
				,new String[]{"mp3_name","mp3_size"},new int[]{R.id.mp3_name,R.id.mp3_size});
		
		return simpleAdapter;
	}
	//通过url下载xml文件
	private String downLoadXML(String url){
		 //添加一个asynTask线程。用以下载xml文件
		DownXmlTask downxmlTask=new DownXmlTask();
		AsyncTask<String, Integer, String> xmlresult=downxmlTask.execute(url);
		String result=null;
		try {
			result = xmlresult.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//如果返回的result为timeout，则表示网络连接超时
		if(result=="timeout"){
			Toast.makeText(RemoteMp3ListActivity.this, "网络连接超时，请检查服务器是否开启", Toast.LENGTH_LONG).show();
		return null;
		}
		else{
		return result;
		}
	}
	//解析xml文件
	private List<Mp3Info> parse (String xmlStr){
		List<Mp3Info> infos=new ArrayList<Mp3Info>();
		SAXParserFactory saxParserFactory=SAXParserFactory.newInstance();
		try{
			XMLReader xmlReader=saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler=new Mp3ListContentHandler(infos);
			xmlReader.setContentHandler(mp3ListContentHandler);
			InputSource inputSource=new InputSource(new StringReader(xmlStr));
			xmlReader.parse(inputSource);
	
				for (Iterator iterator = infos.iterator(); iterator.hasNext();) {
					Mp3Info mp3Info = (Mp3Info) iterator.next();
					System.out.println(mp3Info);
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		return infos;
	}
/**
 * 点击下载列表，通知DownloadService下载相关歌曲
 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Mp3Info mp3Info=mp3Infos.get(position);
		Intent intent=new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.setClass(this, DownloadService.class);
		startService(intent);
		super.onListItemClick(l, v, position, id);
	}

	//用于接收来之下载线程返回的消息
	class DownloadBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String downloadresult=intent.getStringExtra("downloadresult");
			String mp3name_downloading=intent.getStringExtra("mp3name_downloading");
			System.out.println("downloaderresult------->"+downloadresult);
			if(downloadresult.equals("download_fail")){
				Toast.makeText(RemoteMp3ListActivity.this, "文件下载失败！", Toast.LENGTH_SHORT).show();
			}
			else if(downloadresult.equals("download_success")){
				Toast.makeText(RemoteMp3ListActivity.this, "文件下载成功！", Toast.LENGTH_SHORT).show();
			}
			else if(downloadresult.equals("download_isexisted")){
				Toast.makeText(RemoteMp3ListActivity.this, "文件已经存在！", Toast.LENGTH_SHORT).show();
			}
		}
	}
	class DownXmlTask extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpDownloader httpDownloader=new HttpDownloader();
			String result=httpDownloader.download(params[0]);
			return result;
		}
	}
	class ExitAppBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	//广播过滤器
	private IntentFilter getIntentFilter(){
		if(intentFilter==null){
			intentFilter=new IntentFilter();
			intentFilter.addAction(AppConstant.DOWNLOAD_RESULT);
		}
		return intentFilter;
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
