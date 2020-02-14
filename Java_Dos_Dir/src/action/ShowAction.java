package action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import helper.Helper;

public class ShowAction {
	//日期格式
	public static DateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
	
	public static void DiskInfo(String path){
		//磁盘信息
		Process p;
		try {
			p = Runtime.getRuntime().exec("cmd.exe /c dir " + path + " /tc");
		
		BufferedReader breader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		for(int i=0;i<3;i++){	
			System.out.println(breader.readLine());
		}
		breader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return ;
		}
	}
	
	public static void SumInfoOfFilesAndDirs(boolean modifyElementsHasD,String path,SumOfFilesAndDirs sofd){		
		File root = new File(path);
		long freeSpace=root.getFreeSpace();
		System.out.printf("%5s所列文件总数：\n","");
		if(modifyElementsHasD){
			System.out.printf(Helper.formatSize(String.format("%d",sofd.fileNum),16)+" 个文件"
					+Helper.formatSize(String.format("%d",sofd.sizeOfFiles),15)+" 字节\n");
			System.out.printf(Helper.formatSize(String.format("%d",sofd.dirNum),16)+" 个目录"
					+Helper.formatSize(String.format("%d",freeSpace),15)+" 可用字节\n");
		}
		else{
			System.out.printf(Helper.formatSize(String.format("%,d",sofd.fileNum),16)+" 个文件"
					+Helper.formatSize(String.format("%,d",sofd.sizeOfFiles),15)+" 字节\n");
			System.out.printf(Helper.formatSize(String.format("%,d",sofd.dirNum),16)+" 个目录"
					+Helper.formatSize(String.format("%,d",freeSpace),15)+" 可用字节\n");	
		}
	}
	
	public static void SumInfoOfDirs(boolean modifyElementsHasD,String path,SumOfFilesAndDirs sofd){
		File root = new File(path);
		long freeSpace=root.getFreeSpace();
		if(modifyElementsHasD)
			System.out.printf(Helper.formatSize(String.format("%d",sofd.dirNum),16)+" 个目录"+Helper.formatSize(String.format("%d",freeSpace),16)+" 可用字节\n");
		else
			System.out.printf(Helper.formatSize(String.format("%,d",sofd.dirNum),16)+" 个目录"+Helper.formatSize(String.format("%,d",freeSpace),16)+" 可用字节\n");	
	}
	
	public static void show(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj) throws IOException{
		//  b > x > d > w > n > -n (用m表示) >showType：0(直接输出目录)  用6~0标号等级
		if (fileList.isEmpty()) {
			System.out.printf(" "+searchObj.filePath+" 的目录\n\n");
			System.out.println("找不到文件");
			System.out.println("");
			return;
		}
		switch(cmdE.showType){
		case 'b':{
			showBType(cmdE,fileList,searchObj);
			break;
		}
		case 'd':{
			showDType(cmdE,fileList,searchObj);
			break;
		}
		case 'w':{
			showWType(cmdE,fileList,searchObj);
			break;
		}
		case 'x':
		case 'n':
		case '0':{
			withoutShowTypeORWhitN(cmdE, fileList, searchObj);
			break;
		}
		case 'm':{
			showReNType(cmdE,fileList,searchObj );
			break;
		}
		default:{
			break;
		}
		}
	}
	public static void showReNType(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj ) throws IOException{
		System.out.printf(" "+searchObj.filePath+" 的目录\n\n");
		long fileNum=0;
		long fileSize=0;
		File curF = new File(searchObj.filePath);
		File f;
		FileInfo fInfo = new FileInfo(); 
		String fileName;
		Iterator<File> iter = fileList.iterator();
		while (iter.hasNext()) {
			f = iter.next();
			fileName = f.getName();
			if(f.length() > 3){	//确保不是根目录
				if(f.equals(curF))
					fileName = ".";
				else if(f.equals(curF.getParentFile()))
					fileName = "..";				
			}
			fileName = Helper.getShortNameOfActionReN(fileName);
			if(cmdE.modifyElements.contains('l')){
				fileName = Helper.stringToLowerCase(fileName);
			}
			if(f.isFile()){
				fileNum++;
				fileSize += f.length();
			}
			Actions.modifyAction(cmdE, f, fInfo);
			System.out.println(fileName + fInfo.type+fInfo.shortName+fInfo.owner+fInfo.size+fInfo.date);
			}
		if(cmdE.modifyElements.contains('d'))
			System.out.printf(Helper.formatSize(String.format("%d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%d",fileSize),16)+" 字节\n");
		else
			System.out.printf(Helper.formatSize(String.format("%,d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%,d",fileSize),16)+" 字节\n");	
	
	}
	
	// showType:  b > x > d > w > n > -n > 0(直接输出目录) (用m表示) 用6~0标号等级  modifyElements：p,l,t,q,c
	//showType = '0' 或者 'n'
	public static void withoutShowTypeORWhitN(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj ) throws IOException{
		System.out.printf(" "+searchObj.filePath+" 的目录\n\n");
		long fileNum=0;
		long fileSize=0;
		File curF = new File(searchObj.filePath);
		File f;
		FileInfo fInfo = new FileInfo(); 
		String fileName;
		Iterator<File> iter = fileList.iterator();
		while (iter.hasNext()) {
			f = iter.next();

			fileName = f.getName();
			if(f.length() > 3){
				if(f.equals(curF))
					fileName = ".";
				else if(f.equals(curF.getParentFile()))
					fileName = "..";				
			}
			if(f.isFile()){
				fileNum++;
				fileSize += f.length();
			}
			if(cmdE.modifyElements.contains('l')){
				fileName = Helper.stringToLowerCase(fileName);
			}
			Actions.modifyAction(cmdE, f, fInfo);
			System.out.println(fInfo.date+fInfo.type+fInfo.size+fInfo.shortName+fInfo.owner+fileName);
			}
		if(cmdE.modifyElements.contains('d'))
			System.out.printf(Helper.formatSize(String.format("%d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%d",fileSize),15)+" 字节\n");
		else
			System.out.printf(Helper.formatSize(String.format("%,d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%,d",fileSize),15)+" 字节\n");	
	}
	
	//直接输出名称 /b
	public static void showBType(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj ){
		String path = searchObj.filePath;
		File curF = new File(path);
		String fileName;
		Iterator<File> iter = fileList.iterator();
		File f;
		while (iter.hasNext()) {
			f = iter.next();
			
			fileName = f.getName();
			if(f.length() > 3){
				if(f.equals(curF))
					fileName = ".";
				else if(f.equals(curF.getParentFile()))
					fileName = "..";				
			}
			if(cmdE.modifyElements.contains('l')){
				fileName = Helper.stringToLowerCase(fileName);
			}
			System.out.println(fileName);
		}
	}
	
	public static void showDType(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj ) throws IOException{
		Iterator<File> iter = fileList.iterator();
		File f;
		File curF = new File(searchObj.filePath);
		long fileNum = 0;
		long fileSize = 0;
		int maxLen = 1;
		while(iter.hasNext()){
			f = iter.next();
			int tempLen = f.getName().getBytes("GB2312").length;
			if( maxLen < tempLen ){
				maxLen = tempLen;
			}
		}
		int columns;
		int row;
		int formatLen;
		if(maxLen + 2+1 >= 80){
			columns = 1;
			row = fileList.size();
			formatLen = 80;
		}
		else{
			columns = 80 / (maxLen + 2 +1);	//得到列数  总宽  / 文件名长度
			row = (fileList.size()+columns - 1)/(columns == 0 ? 1 : columns);	//得到行数
			formatLen = maxLen + 3;
		}
		int count = 0;
		iter = fileList.iterator();
		while (iter.hasNext()) {
			f = iter.next();

			String fileName;
			fileName = f.getName();
			if(f.length() > 3){
				if(f.equals(curF))
					fileName = ".";
				else if(f.equals(curF.getParentFile()))
					fileName = "..";				
			}				
			if(cmdE.modifyElements.contains('l')){
				fileName = Helper.stringToLowerCase(fileName);
			}
			if(!f.isDirectory()){
				fileNum++;
				fileSize+=f.length();
				System.out.printf(Helper.formatOwner(fileName,formatLen));
			}
			else{
				System.out.printf(Helper.formatOwner("["+fileName+"]",formatLen));
			}
			if((++count)%columns == 0 )
				System.out.println("");
			/*
			if(cmdE.commandPS.contains('p')){
				if(((sofd.fileNum+sofd.dirNum)/columns)% 30 == 0){
					System.out.println("请按ENTER键继续 . . .");
					try{
				          System.in.read();
				    }catch(Exception e){}
				}
			}
			*/
		}
		if(count%row != 0 )
			System.out.println("");
		if(cmdE.modifyElements.contains('d'))
			System.out.printf(Helper.formatSize(String.format("%d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%d",fileSize),15)+" 字节\n");
		else
			System.out.printf(Helper.formatSize(String.format("%,d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%,d",fileSize),15)+" 字节\n");	
	}
	
	public static void showWType(CommandElements cmdE,ArrayList<File> fileList,SearchObject searchObj ) throws UnsupportedEncodingException{
		Iterator<File> iter = fileList.iterator();
		File f;
		File curF = new File(searchObj.filePath);
		long fileNum=0;
		long fileSize=0;
		
		int maxLen = 1;
		while(iter.hasNext()){
			f = iter.next();
			
			int tempLen = f.getName().getBytes("GB2312").length;
			if( maxLen < tempLen ){
				maxLen = tempLen;
			}
		}
		int columns = 80 / (maxLen + 2 +1);
		columns = columns == 0? 1 : columns;
		int count = 0;
		iter = fileList.iterator();
		while (iter.hasNext()) {
			f = iter.next();
			String fileName;
			fileName = f.getName();
			if(f.length() > 3){
				if(f.equals(curF))
					fileName = ".";
				else if(f.equals(curF.getParentFile()))
					fileName = "..";				
			}
			if(cmdE.modifyElements.contains('l')){
				fileName = Helper.stringToLowerCase(fileName);
			}			
			if(!f.isDirectory()){
				fileNum++;
				fileSize+=f.length();
				System.out.printf(Helper.formatOwner(fileName,maxLen+2));
			}
			else{
				System.out.printf(Helper.formatOwner("["+fileName+"]",maxLen+2));
			}
			if((++count)%columns == 0 )
				System.out.println("");			
		}
		if(count%columns != 0 )
			System.out.println("");
		System.out.printf(Helper.formatSize(String.format("%,d",fileNum),16)+" 个文件"+Helper.formatSize(String.format("%,d",fileSize),15)+" 字节\n");	
	}

	
}