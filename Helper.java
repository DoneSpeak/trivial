import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Helper {
	//���û����չ�����ļ���
	public static String getNameWithOutExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return fileName;
		String tfileName = fileName.substring(0,lastIndexOfDot);
		return tfileName;
	}
	
	public static boolean isLegalName(File sourceImg,String newFileName,File targetDir){

		if(newFileName == ""+(char)0 || newFileName.length() == 0){//�����������Ϊ��ʱΪ�ַ���""+(char)0��������Ϊ����ȡ��ʱ������
			JOptionPane.showMessageDialog(null, "�ļ�������Ϊ��",
					"��ʾ",javax.swing.JOptionPane.WARNING_MESSAGE);
			return false;
		}
		String regex = "[\\/:[*][?]\"<>|]";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(newFileName);
		if(matcher.find()){
			JOptionPane.showMessageDialog(null, "�ļ������ܰ��������κη���֮һ��\n"+"\\ / : * ? \" < > |",
					"��ʾ",javax.swing.JOptionPane.WARNING_MESSAGE);
			return false;
		}
		File file = new File(targetDir.getAbsolutePath()+"\\"+newFileName + "."+Helper.getExtension(sourceImg.getName()));
		System.out.println(file.getAbsolutePath());
		if(file.exists()){
			JOptionPane.showMessageDialog(null, "�ļ����Ѵ���","��ʾ",javax.swing.JOptionPane.WARNING_MESSAGE);		
			return false;
		}
		return true;
	}
	
	
	//��ȡ�ļ���չ��
	public static String getExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return "";//û����չ��
		String extension = fileName.substring(lastIndexOfDot+1);
		return extension;
	}
	//��ÿ���socket
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
	//��ÿ���server
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
