package action;

public class FileInfo {
	String date;	//长17
	String size;	//长18
	String type;	//长19
	String shortName;	//长13
	String longName;	//很长
	String owner;	//长23
	
	//显示位置 ：date type/size shortName owner longName
	FileInfo(){
		date = null;
		size = null;
		type = null;
		shortName = null;
		longName = null;
		owner = null;
	}

	FileInfo(String date,String size,String type,String shortName,String longName,String owner){
		this.date = date;
		this.size = size;
		this.type = type;
		this.shortName = shortName;
		this.longName = longName;
		this.owner = owner;
	}
}
