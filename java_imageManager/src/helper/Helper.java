package helper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import pane.ManagerPane;

public class Helper {
	public static boolean isImage(File file) {
		try {

			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return false;
			}
			iis.close();
			return true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}

	// 获取file目录下的所有目录
	public File[] listFiles(File file) {
		if (!file.isDirectory())
			return null;
		try {
			return file.listFiles();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error reading directory" + file.getAbsolutePath(), "Warning",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}

	// 获取file目录下的所有普通目录
	public static File[] getDirs(File file) {
		return file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File f = new File(dir, name);
				if (f.isFile())
					return false;
				DosFileAttributes attr = null;
				try {
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
					return false;
				}

				if (!f.isHidden() && !attr.isSystem() && !attr.isSymbolicLink()
						&& Files.isReadable(Paths.get(f.getAbsolutePath())))
					return true;
				else
					return false;
			}
		});
	}

	// 获取图片文件
	public static File[] getImages(File file) {
		return file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File f = new File(dir, name);
				if (f.isDirectory())
					return false;
				DosFileAttributes attr = null;
				try {
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
					return false;
				}

				if (!attr.isSystem() && isImage(f))
					return true;
				else
					return false;
			}
		});
	}

	//有子目录
	public static boolean hasSubDirs(File file) {
		File[] files = getDirs(file);
		if (files == null)
			return false;
		for (File f : files) {
			if (f.isDirectory())
				return true;
		}
		return false;
	}
	//获取文件拓展名
	public static String getExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return "";//没有拓展名
		String extension = fileName.substring(lastIndexOfDot+1);
		return extension;
	}
	
	//获取文件位置
	public static String getLocation(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf("\\");
		if(lastIndexOfDot < 0)
			return "unknown";
		String Location = fileName.substring(0,lastIndexOfDot);
		return Location;
	}
	
	//图像重命名，用于属性中更改名称
	public static File rename(File oldfile,JTextField nameField){
		File newFile;
		String name = nameField.getText();
		if(name == ""+(char)0 || name.length() == 0){//当输入的内容为空时为字符串""+(char)0，可能是为了与取消时做区分
			JOptionPane.showMessageDialog(null, "文件名不能为空",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		String regex = "[\\/:[*][?]\"<>|]";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(name);
		if(matcher.find()){
			JOptionPane.showMessageDialog(null, "文件名不能包含下列任何符号之一：\n"+"\\ / : * ? \" < > |",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		newFile = new File(Helper.getLocation(oldfile.getAbsolutePath())+"\\"+nameField.getText()+ "."+Helper.getExtension(oldfile.getName()));
		if(!oldfile.exists()){
			JOptionPane.showMessageDialog(null, "重命名文件不存在",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if(oldfile.equals(newFile)){
			return null;
		}
		if(newFile.exists()){
			JOptionPane.showMessageDialog(null, "文件名已存在","提示",javax.swing.JOptionPane.WARNING_MESSAGE);		
			return null;
		}
		oldfile.renameTo(newFile);
		return newFile;
	}
	//图像重命名，用于直接重命名
	/*
	 * 返回null是重命名失败
	 * 成功则返回newFile
	 */
	public static File rename(File oldfile,String newName){
		if(newName == ""+(char)0 || newName.length() == 0){//当输入的内容为空时为字符串""+(char)0，可能是为了与取消时做区分
			JOptionPane.showMessageDialog(null, "文件名不能为空",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		File newFile;
		String regex = "[\\/:[*][?]\"<>|]";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(newName);
		if(matcher.find()){
			JOptionPane.showMessageDialog(null, "文件名不能包含下列任何符号之一：\n"+"\\ / : * ? \"  < > |",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		newFile = new File(Helper.getLocation(oldfile.getAbsolutePath())+"\\"+newName + "."+Helper.getExtension(oldfile.getName()));
		if(!oldfile.exists()){
			JOptionPane.showMessageDialog(null, "重命名文件不存在",
					"提示",javax.swing.JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if(oldfile.equals(newFile)){
			return null;
		}
		if(newFile.exists()){
			JOptionPane.showMessageDialog(null, "文件名已存在","提示",javax.swing.JOptionPane.WARNING_MESSAGE);		
			return null;
		}
		oldfile.renameTo(newFile);
		return newFile;
	}
	

	//判断文件名是否合法
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
	
	//图像粘贴 
	public static String pasteImg(File sourceImg,String newFileName,File targetDir){
		newFileName = targetDir.getAbsolutePath()+"\\"+newFileName + "."+Helper.getExtension(sourceImg.getName());
		try
		{
		if(!sourceImg.exists()){
			JOptionPane.showMessageDialog(null, "粘贴文件"+sourceImg.getName()+"不存在","警告",javax.swing.JOptionPane.WARNING_MESSAGE);	
			return null;
		} 
		 Image image =ImageIO.read(sourceImg); 
		 int width=image.getWidth(null);
		 int height=image.getHeight(null);
		 
		 BufferedImage imageTag=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		 imageTag.getGraphics().drawImage(image,0,0,width,height,null);
		 FileOutputStream out=new FileOutputStream(newFileName);
		 JPEGImageEncoder encoder=JPEGCodec.createJPEGEncoder(out);
		 encoder.encode(imageTag);
		 out.close();
		 return newFileName;
		}catch (IOException e) {
		 JOptionPane.showMessageDialog(null, "图像复制失败","提示",javax.swing.JOptionPane.WARNING_MESSAGE);	
		 e.printStackTrace();
		 return null;
		}
	}
	
	//获得没有拓展名的文件名
	public static String getNameWithOutExtension(String fileName){
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if(lastIndexOfDot < 0)
			return fileName;
		String tfileName = fileName.substring(0,lastIndexOfDot);
		return tfileName;
	}
}
