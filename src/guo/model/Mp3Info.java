package guo.model;

import java.io.Serializable;
//ʵ���࣬���л����㴫�ݡ�
public class Mp3Info implements Serializable {
	
		private static final long serialVersionUID=1L;
		private String id;
		private String mp3Name;
		private String mp3Size;
		private String lrcName;
		private String lrcSize;
		private String mp3CnName;
		private String lrcCnName;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Mp3Info() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Mp3Info(String id, String mp3Name, String mp3Size,
				String lrcName, String lrcSize,String mp3CnName,String lrcCnName) {
			super();
			this.id = id;
			this.mp3Name = mp3Name;
			this.mp3Size = mp3Size;
			this.lrcName = lrcName;
			this.lrcSize = lrcSize;
			this.mp3CnName = mp3CnName;
			this.lrcCnName=lrcCnName;

		}
		public String getMp3Name() {
			return mp3Name;
		}
		@Override
		public String toString() {
			return "Mp3Info [id=" + id + ", mp3Name=" + mp3Name  + ", mp3CnName=" + mp3CnName + ", mp3Size="
					+ mp3Size + ", lrcName=" + lrcName + ", lrcSize=" + lrcSize
					+ "]";
		}
		public void setMp3Name(String mp3Name) {
			this.mp3Name = mp3Name;
		}
		public String getMp3Size() {
			return mp3Size;
		}
		public void setMp3Size(String mp3Size) {
			this.mp3Size = mp3Size;
		}
		public String getLrcName() {
			return lrcName;
		}
		public void setLrcName(String lrcName) {
			this.lrcName = lrcName;
		}
		public String getLrcSize() {
			return lrcSize;
		}
		public void setLrcSize(String lrcSize) {
			this.lrcSize = lrcSize;
		}
		public String getMp3CnName() {
			return mp3CnName;
		}
		public void setMp3CnName(String mp3CnName) {
			this.mp3CnName = mp3CnName;
		}
		public String getLrcCnName() {
			return lrcCnName;
		}
		public void setLrcCnName(String lrcCnName) {
			this.lrcCnName = lrcCnName;
		}
		
}
