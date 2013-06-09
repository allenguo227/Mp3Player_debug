package guo.mp3player;

public interface AppConstant {

	public class PlayerMsg
	{
		public static final int PLAY_MSG=1;
		public static final int PAUSE_MSG=2;
		public static final int STOP_MSG=3;
	}
	public class URL
	{
		//修改下载路径
		public static final String BASE_URL="http://192.168.1.102:8098/mp3";	
	}
	//broadcast的action
	public static final String LRC_MESSAGE_ACTION="guo.mp3player.lrcmessage.action";
	public static final String SEEKBAR_PROGRESS_ACTION="guo.mp3player.seekbarprogress.action";
	public static final String DOWNLOAD_RESULT="guo.mp3player.downloadresult.action";
	public static final String DOWNLOAD_FORRESULT="guo.mp3player.downloadforresult.action";
}
