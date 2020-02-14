package action;

public class SearchObject {
		public String filePath = null;
		public String fileName = null;
		public boolean isRegex = false;
		public SearchObject(){
			filePath = System.getProperty("user.home");	//默认路径
			fileName = ".+";								//检索所有
			isRegex = true;								//正则表达式方式
		}
}
