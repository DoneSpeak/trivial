import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Helper {
	//获得没有拓展名的文件名
	public static String getNameWithOutExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return fileName;
		String tfileName = fileName.substring(0,lastIndexOfDot);
		return tfileName;
	}
	
	public static boolean isLegalName(File sourceImg,String newFileName,File targetDir){

		if(newFileName == ""+(char)0 || newFileName.length() == 0){//当输入的内容为空时为字符串""+(char)0，可能是为了与取消时做区分
			JOptionPane.showMessageDialog(null, "文件名不能为空",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return false;
		}
		String regex = "[\\/:[*][?]\"<>|]";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(newFileName);
		if(matcher.find()){
			JOptionPane.showMessageDialog(null, "文件名不能包含下列任何符号之一：\n"+"\\ / : * ? \" < > |",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return false;
		}
		File file = new File(targetDir.getAbsolutePath()+"\\"+newFileName + "."+Helper.getExtension(sourceImg.getName()));
		System.out.println(file.getAbsolutePath());
		if(file.exists()){
			JOptionPane.showMessageDialog(null, "文件名已存在","提示",javax.swing.JOptionPane.WARNING_MESSAGE);		
			return false;
		}
		return true;
	}
	
	
	//获取文件拓展名
	public static String getExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return "";//没有拓展名
		String extension = fileName.substring(lastIndexOfDot+1);
		return extension;
	}
	//获得可用socket
	public static Socket getUsefulSocket(String touchIp,int port){
		
		System.out.println(touchIp);
		System.out.println(port);
		Socket socket = null;
		try {
			socket = new Socket(touchIp,port);
		} catch (Exception e) {
			System.err.println(e);
			port++; 
			socket = getUsefulSocket(touchIp,port);
			return socket;
		}
		return socket;
	}
	/*
	//获得可用server
	public static Socket getUsefulServer(int port){
		
		System.out.println(touchIp);
		System.out.println(port);
		Socket socket = null;
		try {
			socket = new Socket(touchIp,port);
		} catch (Exception e) {
			System.err.println(e);
			port++; 
			socket = getUsefulSocket(touchIp,port);
			return socket;
		}
		return socket;
	}
	*/
}
