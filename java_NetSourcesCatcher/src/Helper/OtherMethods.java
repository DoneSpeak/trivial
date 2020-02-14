package Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class OtherMethods {

	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	private static final String IMGURL_REG2 = "<\\s*img\\s+([^>]*)\\s*>";
	private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";
	private static final String SOURCE_REG = "<((img)|(script)|(link))(.+?)[^>]*?>";
	private static final String OTHER_REG = "((src)|(href))\\s*=\\s*[\"'](.*?)[^[\"']]*?[\"']";
	
	
	public static void main(String args[]){
		try {
//			System.out.println("getSrcThroughKit：");
//			getSrcThroughKit();
//			System.out.println("****************************");
//			System.out.println("getSrcWithString：");
//			getSrcWithString();
//			System.out.println("****************************");
			System.out.println("getSrcWithBString：");
			getSrcWithBString();
		} catch (Exception e) {
			System.out.println("有错！");
			e.printStackTrace();
		}
	}

	// 这种检查方式需要取得链接，很慢
	public static boolean isLegal(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			String strMessage = conn.getResponseMessage();
			if (strMessage.compareTo("Not Found") == 0) {
				return false;
			}
			conn.disconnect();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 扣取html文件中的img,js,css链接 三种方法
	 * 
	 * @throws BadLocationException
	 * @throws IOException
	 */
	// [start]通过html自带的包
	public static void getSrcThroughKit() throws IOException, BadLocationException {
		int count = 0;
		long startTime = System.currentTimeMillis();
		File readFile = new File("test.txt");
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		Reader HTMLReader = new InputStreamReader(new FileInputStream(readFile));
		kit.read(HTMLReader, doc, 0);

		// 遍历,读取标签
		ElementIterator it = new ElementIterator(doc);
		Element elem;

		// 当下载下来的网页是被重定向时就会无法下载，如http://bbs.csdn.net/topics/350124241
		while ((elem = it.next()) != null) {
			// System.out.println("test");
			String srclink = null;
			if (elem.getName().equals("img")) {
				srclink = (String) elem.getAttributes().getAttribute(HTML.Attribute.SRC);
			} else if (elem.getName().equals("script")) {
				srclink = (String) elem.getAttributes().getAttribute(HTML.Attribute.SRC);
			} else if (elem.getName().equals("link")) {
				srclink = (String) elem.getAttributes().getAttribute(HTML.Attribute.HREF);
			}
			if (srclink != null) {
				// if (!srclink.startsWith("http:")) {
				// System.out.println(srclink);
				// srclink = "http://" + "*****" +
				// srclink.replaceFirst("[[.]/]*","/");
				// System.out.println(srclink);
				// System.out.println("wuhttp");
				// }
				// source.add(srclink);
				System.out.println(count + " : " + srclink);
				count++;
			}
		}
		HTMLReader.close();
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + (endTime - startTime) + "ms");
	}
	
	//通过String的replace
	public static void getSrcWithString() throws IOException {	
		File readFile = new File("test.txt");
		FileReader in = new FileReader(readFile);
		BufferedReader reader = new BufferedReader(in);
		String s = null;
		StringBuffer sb = new StringBuffer();
		String line;
		String temp;

		Pattern p = Pattern.compile(SOURCE_REG);
		Matcher m;
		int count = 1;
		long startTime = System.currentTimeMillis();
		// [start]通过String的replace
		while ((line = reader.readLine()) != null) {
			temp = line.replaceAll(" ", "");
			// System.out.println(temp);
			m = p.matcher(temp);
			while (m.find()) { // 得到 <img >之类
				String temp2 = m.group();
				// System.out.println(temp2);
				String str = null;
				if (temp2.contains("<img") || temp2.contains("<script")) {
					int start = temp2.indexOf("src=");

					if (start != -1) {
						str = temp2.substring(start + 5, temp2.length());
						start = str.indexOf("\"");
						if (start < 0)
							start = sb.indexOf("'");

						str = str.substring(0, start);
						System.out.println(count + " : " + str);
						count++;
					}
					// 解析链接，获取新链接
					// 得到本地连接
					// line.replace(str, "newLink");

				}
				if (temp2.contains("<link")) {
					int start = temp2.indexOf("href=");

					if (start != -1) {
						str = temp2.substring(start + 6, temp2.length());
						start = str.indexOf("\"");
						if (start < 0)
							start = sb.indexOf("'");

						str = str.substring(0, start);
						System.out.println(count + " : " + str);
						count++;
					}
				}
			}
			in.close();
			reader.close();
			long endTime = System.currentTimeMillis();
			System.out.println("运行时间：" + (endTime - startTime) + "ms");
			// [end]
		}
	}
	
	public static void getSrcWithBString() throws IOException{
		File readFile = new File("test.txt");
		FileReader in = new FileReader(readFile);
		BufferedReader reader = new BufferedReader(in);
		String s = null;
		StringBuffer sb = new StringBuffer();
		String line;
		String temp;

		Pattern p = Pattern.compile(SOURCE_REG);
		Matcher m;
		int count = 1;
		long startTime = System.currentTimeMillis();
		// [start]通过BufferString
		while ((line = reader.readLine()) != null) {
			temp = line.replaceAll(" ", "");
			// System.out.println(temp);
			m = p.matcher(temp);
			while (m.find()) { // 得到 <img >之类
				String temp2 = m.group();
				// System.out.println(temp2);
				// 通过
				if ((temp2.contains("<img") || temp2.contains("<script")) && temp2.contains("src=")) {
					sb = new StringBuffer(temp);
					int start = sb.indexOf("src=");

					if (start != -1) {
						sb.replace(0, start + 5, "");
						start = sb.indexOf("\"");
						if (start < 0)
							start = sb.indexOf("'");
						if (start != -1) {
							sb.replace(start, sb.length(), "");
							System.out.println(count + " : " + sb.toString());
							count++;
						}
					}

				}
				if (temp.contains("<link") && temp.contains("href=")) {
					sb = new StringBuffer(temp);
					int start = sb.indexOf("href=");
					if (start != -1) {
						sb.replace(0, start + 6, "");
						// System.out.println(sb.toString());
						start = sb.indexOf("\"");
						if (start < 0)
							start = sb.indexOf("'");
						if (start != -1) {
							sb.replace(start, sb.length(), "");
							System.out.println(count + " : " + sb.toString());
							count++;
						}
					}
				}
			}

		}
		in.close();
		reader.close();
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + (endTime - startTime) + "ms");
		//[end]	
	}

}
