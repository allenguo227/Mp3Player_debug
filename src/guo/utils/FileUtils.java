package guo.utils;

import guo.model.Mp3Info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class FileUtils {
	private String SDCardRoot;
	/**
	 * 构造函数，给SDCardroot赋值，得到当前外部储存卡的目录
	 */
	public FileUtils(){
		SDCardRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	/**
	 * 在SD卡上创建文件
	 */
	public File createFileInSDCard(String fileName,String dir) throws IOException{
		File file =new File(SDCardRoot+File.separator+dir+File.separator+fileName);
		System.out.println("file---->"+file);
		file.createNewFile();
		return file;
	}
	/**
	 * 在SD卡上创建目录
	 */
	public File creatSDDir(String dir){
		File dirFile=new File(SDCardRoot+File.separator+dir+File.separator);
		dirFile.mkdirs();
		return dirFile;
	}
	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName,String path){
		File file=new File(SDCardRoot+File.separator+path+File.separator+fileName);
		return file.exists();
	}
	/**
	 * 将一个Inputstream里面的数据写入到SD卡里
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input){
		File file=null;
		OutputStream output=null;
		try{
			creatSDDir(path);
			file =createFileInSDCard(fileName,path);
			output=new FileOutputStream(file);
			byte buffer[]=new byte[4*1024];
			int temp;
			while((temp=input.read(buffer))!=-1){
				output.write(buffer,0,temp);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
	/**
	 * 读取目录中的Mp3文件的名字和大小(大小转换为MB)
	 * 增加了读取目录中的Lrc文件的名字和大小
	 */
	public List<Mp3Info>getMp3Files(String path)
	{
		
		List<Mp3Info>mp3Infos=new ArrayList<Mp3Info>();
		
		File file=new File(SDCardRoot+File.separator+path);
		File[] files= file.listFiles();
		if(files!=null){
		for(int i = 0; i < files.length; i++){
			if(files[i].getName().endsWith("mp3")){
				Mp3Info mp3Info = new Mp3Info();
				mp3Info.setMp3Name(files[i].getName());
				//转换为MB单位
				mp3Info.setMp3Size(((float)files[i].length() )/1000000+ "MB");
				String lrcname[]=files[i].getName().split("\\.");
				String lrc=lrcname[0]+".lrc";
				if(isFileExist(lrc, "/mp3")){
					mp3Info.setLrcName(lrc);
				}
				mp3Infos.add(mp3Info);			
			}
		}
		return mp3Infos;
	}
		else
		{
			return null;
		}
	}
	
}
