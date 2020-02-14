package helper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;

import action.CommandElements;

public class Helper {
	
	public static long getDate (File file,CommandElements cmdE) throws IOException{
		long fileDate;
		BasicFileAttributes dfa = Files.readAttributes(Paths.get(file.getAbsolutePath()), DosFileAttributes.class);
		if (cmdE.modifyElements.contains('c')) {
			fileDate = dfa.creationTime().toMillis();
		} 
		else if (cmdE.modifyElements.contains('a')) {
			fileDate = dfa.lastAccessTime().toMillis();
		}
		else{
			fileDate = dfa.lastModifiedTime().toMillis();
		}
		return fileDate;
	}

	//获取扩展名
	public static String getExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return "";
		String extension = fileName.substring(lastIndexOfDot+1);
		return extension;
	}
	
	//获得文件名
	public static String getNameWithOutExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return fileName;
		String tfileName = fileName.substring(0,lastIndexOfDot);
		return tfileName;
	}
	
	//格式化文件大小格式 
	public static String formatSize(String s,int n) {
		int len = n - s.length();
		StringBuilder sb = new StringBuilder();
		if (len > 0) {
			for (int i = 1; i <= len; i++)
				sb.append(" ");
		}
		sb.append(s);
		return sb.append(" ").toString();
	}
	
	//格式化type
	public static String formatType(String s,int n) {
		int len = n - s.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 4; i++)
			sb.append(" ");
		sb.append(s);		
		if (len > 0) {
			for (int i = 1; i <= len; i++)
				sb.append(" ");
		}
		return sb.toString();
	}
	//格式化Owner ,右增空格
	public static String formatOwner(String s,int n) throws UnsupportedEncodingException {
		int len = n - s.getBytes("GB2312").length;;
		StringBuilder sb = new StringBuilder();
		sb.append(s);	
		if (len > 0) {
			for (int i = 1; i <= len; i++)
				sb.append(" ");
		}
		return sb.toString();
	}
		
	//获取小写名称
	public static String getLowerCaseName(String fName){
		String fileName = fName;
		int lastIndecOfDot = fileName.lastIndexOf(".");
		if(lastIndecOfDot > 0 ){
			String name = fileName.substring(0,lastIndecOfDot);
			String lowerCaseName = stringToLowerCase(name);
			String extension = fileName.substring(lastIndecOfDot+1);
			return lowerCaseName + "." + extension;
		}
		return stringToLowerCase(fileName);
		
	}
	//字符串转小写
	public static String stringToLowerCase(String string){
		String str = string;
		StringBuilder sb = new StringBuilder();
		if(str!=null){
			for(int i=0;i<str.length();i++){
				char c = str.charAt(i);
				if(Character.isUpperCase(c))
					sb.append(Character.toLowerCase(c));
				else
					sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static String getShortNameOfActionReN(String fname) throws UnsupportedEncodingException{
		String fileName = fname;
		
		String extension = getExtension(fileName);	
		String fName = getNameWithOutExtension(fileName);
		int lenOfExt = extension.length();
		int lenOfName = fName.getBytes("GB2312").length;
		int len = 12 - extension.length();
		StringBuilder sb = new StringBuilder();
		int i;
		if(lenOfName <= len){
			sb.append(fName);
			len = len - lenOfName;
			for(int j = 0;j<len; j++)
				sb.append(" ");
		}
		else if(lenOfName > len ){
			for(i= 0;i< len && i<fName.length();i++){
				if(Character.toString(fName.charAt(i)).getBytes("GB2312").length == 2){
					if(i == len-1){
						sb.append(" ");
						break;
					}
					sb.append(fName.charAt(i));
					i++;
					continue;
				}
				sb.append(fName.charAt(i));
			}
		}
		sb.append(extension);
		return sb.toString();
	}
}



