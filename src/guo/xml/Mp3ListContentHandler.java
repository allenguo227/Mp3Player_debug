package guo.xml;

import guo.model.Mp3Info;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 
 * @author Administrator
 *
 */
public class Mp3ListContentHandler extends DefaultHandler {
	private List<Mp3Info>infos=null;
	private Mp3Info mp3Info=null;
	private String tagName=null;
	
	public Mp3ListContentHandler(List<Mp3Info> infos) {
		super();
		this.infos = infos;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		this.tagName=localName;
		if(tagName.equals("resource"))
		{
			mp3Info=new Mp3Info();
		}
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		String temp=new String(ch,start,length);
		if(tagName.equals("id")){
			mp3Info.setId(temp);}
		else if(tagName.equals("mp3.name")){
			mp3Info.setMp3Name(temp);}
		else if(tagName.equals("mp3.cnname")){
			mp3Info.setMp3CnName(temp);}
		else if(tagName.equals("mp3.size")){
			mp3Info.setMp3Size(temp);}
		else if(tagName.equals("lrc.name")){
			mp3Info.setLrcName(temp);}
		else if(tagName.equals("lrc.cnname")){
			mp3Info.setLrcCnName(temp);}
		else if(tagName.equals("lrc.size")){
			mp3Info.setLrcSize(temp);}

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	public Mp3Info getMp3Info() {
		return mp3Info;
	}
	public void setMp3Info(Mp3Info mp3Info) {
		this.mp3Info = mp3Info;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	//������xml�ļ��ڵ����ʱ����
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals("resource"))
		{
//			System.out.println("tagName--->"+localName);
			infos.add(mp3Info);
		}
		tagName="";
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	

}
