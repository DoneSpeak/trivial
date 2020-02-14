package Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import cpdetector.io.*;

public class Helper {
	
	//仅判断ulrString是否合法
	public static boolean isLegalURL(String urlString){
		try{
			URL target =  new URL(urlString);
			if(target == null || !target.getProtocol().toUpperCase().equals("HTTP")){
				return false;
			}
			return true;
		}catch(MalformedURLException m){
			return false;
		}
	}
	//检查urlString是否合法，同时返回不合法信息
	public static String checkHTTP(String urlString){
		try{
			URL target =  new URL(urlString);
			if(target == null || !target.getProtocol().toUpperCase().equals("HTTP")){
				System.out.println(urlString + "不是http协议");
				return "这不是http协议网址";
			}
			return null;
		}catch(MalformedURLException m){
			System.out.println("协议格式错误");
			return "协议格式错误";
		}
	}
	//通过url获取网页编码,利用第三方包cpdetector,仅可以用于http协议的网页
	public static String getFileEncoding(URL url){
		try {
		    CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();
		    
		    codepageDetectorProxy.add(JChardetFacade.getInstance());
		    codepageDetectorProxy.add(ASCIIDetector.getInstance());
		    codepageDetectorProxy.add(UnicodeDetector.getInstance());
		    codepageDetectorProxy.add(new ParsingDetector(false));
		    codepageDetectorProxy.add(new ByteOrderMarkDetector());	
		    Charset charset=null;
			charset = codepageDetectorProxy.detectCodepage(url);
			if(charset.equals(null)){
				return "UTF-8";
			}
			return charset.name();
		} catch (IOException e) {
			System.out.println("利用cpdetector获取网页编码失败！\n"+"使用默认编码UTF-8");
//			e.printStackTrace();
			return "UTF-8";
		}
	}
	
	//TODO 做笔记时移走
	//利用html包获取html文件中的标签内容
	public static ArrayList<String> getSrc(InputStream inS) throws IOException{
		ArrayList<String> source = new ArrayList<String>();
		HTMLEditorKit kit = new HTMLEditorKit(); 
	    HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
	    doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
	    Reader HTMLReader = new InputStreamReader(inS); 
	    try {
			kit.read(HTMLReader, doc, 0);
		} catch (IOException | BadLocationException e) {JOptionPane.showMessageDialog(null, "无法下载此网页！");
			System.out.println("HTMLEditorKit"+"获取网页失败");
			JOptionPane.showMessageDialog(null, "获取网页资源失败");
			e.printStackTrace();
		} 

	    //  Get an iterator for all HTML tags.
	    ElementIterator it = new ElementIterator(doc); 
	    Element elem; 
	    
	    while( (elem = it.next() )!= null  )
	    { 
	    	String srclink = null;
	    	if(elem.getName().equals("img")){
	    		srclink = (String)elem.getAttributes().getAttribute(HTML.Attribute.SRC);
	    	}
	    	else if(elem.getName().equals("script")){
	    		srclink = (String)elem.getAttributes().getAttribute(HTML.Attribute.SRC);
	    	}
	    	else if(elem.getName().equals("link")){
	    		srclink = (String)elem.getAttributes().getAttribute(HTML.Attribute.HREF);
	    	}
	    	if(srclink != null)
	    	{
	    		source.add(srclink);
				System.out.println(srclink);
			}
			
	    }
	    return source;
    }
	
	//获取标签中的名称，有些链接的图片名称不在链接末尾，在内部，所有就要从中截取
	public static String getSrcName(String path) {
		 String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
		if(path.endsWith(".css") || path.endsWith(".js") || path.contains(".jpg") || path.endsWith(".gif") || path.endsWith(".png") || path.endsWith(".con")){ //最后的字符串就是名字
			int start = path.lastIndexOf("/");
			return path.substring(start+1);
		}
		String[] str = path.split(regEx);
		for(int i = 0;i<str.length;i++){
			if(str[i].contains(".js") || str[i].contains(".css") || str[i].contains(".jpg") || str[i].contains(".gif") || str[i].contains(".png") || str[i].contains(".con")){
				return str[i];
			}
		}
		for(int i = 0;i<str.length;i++){
			if(str[i].indexOf('.')+4 >= str[i].length()){
				return str[i];
			}
		}
		String temp = "temp";
		File file = new File(temp);
		int n = 1;
		while(file.exists()){
			file = new File("temp"+n);
			n++;
		}
		return file.getName();
	}
}
	
