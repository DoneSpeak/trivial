package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import Helper.*;

public class NetSourcesCatcher {
	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createGUIAndShow();
			}
		});
	}

	public static void createGUIAndShow() {
		new SimpleFrame();
	}
}

//程序主窗体
class SimpleFrame extends JFrame implements ActionListener {
	JPanel jp = new JPanel();
	JTextField inputUrl = new JTextField(26);
	JButton btn = new JButton("下载");
	JTextArea showStatus = new JTextArea();
	JScrollPane jsp;
	Download download;

	public SimpleFrame() {
		super("网页抓取");
		setLayout(new BorderLayout());
		//设置窗体居中显示
		Toolkit kit=getToolkit();
		Dimension winSize=kit.getScreenSize();
		setBounds((winSize.width-380)/2,(winSize.height-220)/2,380,220);
		setMinimumSize(new Dimension(380,78));
		setVisible(true);

		btn.addActionListener(this);
		btn.setBackground(new Color(30,190,225));
		jp.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp.add(inputUrl);
		jp.add(btn);

		showStatus.setEditable(false);
		jsp = new JScrollPane(showStatus, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(jp, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		validate();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btn)) { // 这里需要得到源
			if (inputUrl.getText() == null || inputUrl.getText().equals("")
					|| inputUrl.getText().equals(Character.toString((char) '0'))) {
				JOptionPane.showMessageDialog(this, "请输入网址！");
				return;
			}
			String url = inputUrl.getText();
			if (url.indexOf("://") < 0) {
				url = "http://" + url;
			}
			String errMsg = Helper.checkHTTP(url);
			if (errMsg != null) {
				JOptionPane.showMessageDialog(this, errMsg + "\n" + "请重新输入协议为http的网址");
				// System.out.println("网页链接不合法！");
				return;
			}
			JFileChooser fChooser = new JFileChooser();
			fChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY); // 必须先设置选择样式再设置打开对话框
			fChooser.showDialog(null, "保存");

			File saveDir = fChooser.getSelectedFile();
			if (saveDir == null) {
				return;
			}
			// 利用线程来下载文件，在线程中处理网页信息
			download = new Download(url, showStatus, saveDir.getAbsolutePath());
			Thread thread = new Thread(download);
			thread.start();
		}
	}

}

// 下载网页中资源
class Download implements Runnable {
	// [start]Download的变量
	private static final String SOURCE_REG = "<((img)|(script)|(link))(.*?)[^>]*?>";
	public static int OK = 0;
	public static int downloadNum = 0;
	ArrayList<String> source;
	String urlString;
	public static JTextArea jta;
	String saveDir;
	String hostname;
	File savefile;
	public File saveDirFile;
	String encode = "UTF-8"; // 编码默认UTF-8

	StringBuffer htmlSb = new StringBuffer();
	StringBuffer info = new StringBuffer();
	BufferedWriter wr = null;
	BufferedReader rd = null;
	Socket socket = null;

	// [end]
	public Download(String urlString, JTextArea jta, String saveDir) {
		this.urlString = urlString;
		this.jta = jta;
		this.saveDir = saveDir;
		source = new ArrayList<String>();
	}

	@Override
	//下载html文件，获取html中的img,js,css链接，html链接替换
	public void run() {
		//System.out.println("thread"+javax.swing.SwingUtilities.isEventDispatchThread()); //判断可知是在后台线程
		try {
			URL url = new URL(urlString);
			//[start] 在文本域中展示链接信息
			info.append("访问主机：" + url.getHost() + "\n");
			info.append("访问端口：" + url.getDefaultPort() + "\n");
			jta.setText(info.toString());
			//[end]
	
			//[start]链接socket及发送指令
			encode = Helper.getFileEncoding(url);
			String params = URLEncoder.encode("param1", encode) + "=" + URLEncoder.encode("value1", encode);
			params += "&" + URLEncoder.encode("param2", encode) + "=" + URLEncoder.encode("value2", encode);

			hostname = url.getHost();		 
			int port = 80;
			InetAddress addr = InetAddress.getByName(hostname);
			socket = new Socket(addr, port);
			socket.setSoTimeout(4000); //设置4秒等待时间
			String path = url.getPath() == null?"/":url.getPath();
			
			wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encode));
			if(path.equals("/"))
				wr.write("GET " + path + " HTTP/1.0\r\n");
			else
				wr.write("POST " + path + " HTTP/1.0\r\n"); //不需要提交信息，所以只要get即可
			wr.write("HOST:" + hostname + "\r\n");
			wr.write("Content-Length: " + params.length() + "\r\n");
			wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
//			wr.write("User-Agent:myself\r\n"+"Accept:www/source;text/html;image/gif;*/*\r\n");
			wr.write("\r\n");
			wr.write(params);
			wr.flush();
			//[end]
			jta.append("正在下载..." + "\n");
			
			rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), encode));
			String line = null;
			
			//去除头部信息
			clearHead(new DataInputStream(socket.getInputStream()),encode);
			
			//[start]获取title
			String title = null;
			
			while ((line = rd.readLine()) != null) {
//				System.out.print(line+"\r\n");	
				line = formatLine(line,new File(saveDir)); //考虑到以网页title名命名的目录尚未生成，所以这里保存在选择的保存目录下
				htmlSb.append(line);
				htmlSb.append("\r\n");
				if(line.contains("</title>"))
				{
					String temp = htmlSb.toString().trim().replaceAll("([(\r)(\n)]*)","").replaceAll(".*<title>\\s*", "");
					title = temp.replaceAll("\\s*</title>.*", "");
					break;
				}
			}


			// 读取不到信息，估计是链接失败
			if (htmlSb.length() <= 0) {
				JOptionPane.showMessageDialog(null, "无法下载此网页！");
				jta.append("下载失败");
				socket.close();
				return;
			}
			
			//没有找到题目，用网页的最后一级作为题目
			if(title == null)
			{
				if(path.equals("/")){
					title = hostname;
				}
				else{
					int lastI = path.lastIndexOf(".");
					if(lastI < 0){
						title = path.substring(path.lastIndexOf("/"));
					}
					else{
						title = path.substring(path.lastIndexOf("/"),lastI);
					}
				}
			}
			//替换文件名中的非法字符
			title = title.replaceAll("[\\/:[*][?]\"<>|]", "_");
			title = title.replaceAll("\\s+", " ");
			//[end]获取title
			//[start]保存文件,在保存文件中已存在文件是，对文件名进行拓展，获取保存路径，为所得文档img,js,css路径替换准备
			savefile = new File(saveDir+"\\"+title+".html");
			int n = 0;
			String expand = "_net";
			while(savefile.exists()){
				savefile  = new File(saveDir+"\\"+title+expand+".html");
				n++;
				expand = expand.substring(0,expand.lastIndexOf("t"))+"t_"+n;	
			}
			//创建保存img等文件的目录
			String savePath = savefile.getAbsolutePath().substring(0,savefile.getAbsolutePath().lastIndexOf("."))+"_files";
			saveDirFile = new File(savePath);
			n = 1;
			while(saveDirFile.exists()){
				saveDirFile = new File(savePath+n);
				n++;
			}
			saveDirFile.mkdir();
			savePath = "./"+saveDirFile.getName();
			//[end]
			BufferedWriter tohtmlfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savefile),encode));
			tohtmlfile.write(htmlSb.toString());
			//读取剩下的字符串
			while ((line = rd.readLine()) != null) {
				line = formatLine(line,saveDirFile); //获取链接，同时替换line中链接为本地
				tohtmlfile.write(line);
				tohtmlfile.newLine();
				tohtmlfile.flush();
			}
			
			wr.close();
			rd.close();
			tohtmlfile.close();
			System.out.println("成功生成html文件");
			OK++; //一个资源下载
			downloadNum++;
			if(source.size()==0){
				jta.append("下载完成");
			}

			//下载js,img,css
			Iterator <String> iter = source.iterator();
			int portNum = 80;
			while(iter.hasNext()){
				String urlString = iter.next();
				if(Helper.isLegalURL(urlString)){	
					downloadNum++;
//					portNum ++; //对单一主机进行多端口访问设想而加上一个partNum
					new Thread(new myRunnable(urlString,portNum)).start();		
				}
				
			}
			
//TODO		对单一主机长连接设想而写的下载资源线程，未完善
//			new Thread(new GetSourceRunnable(source,saveDirFile.getAbsolutePath())).start();
			
			if(socket.isConnected() && !socket.isClosed()){
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("socket关闭出错");
				e.printStackTrace();
			}
		}
		}catch(NumberFormatException e){
			System.err.println("资源获取出错。\n");
			JOptionPane.showMessageDialog(null, "无法下载此网页！");
			e.printStackTrace();
		} catch(IOException e){
			JOptionPane.showMessageDialog(null, "无法下载此网页！");
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public String formatLine(String line,File saveDirFile){
		String temp = line.replaceAll("[=\"'\\s]+", "=");  //src="http://a"; src='http://a'; src=http://a 转化为 src=http://a=
		Pattern p = Pattern.compile(SOURCE_REG);
		Matcher m = p.matcher(temp);
		String srcLink = null;
		String newLink = null;
		StringBuffer sb = new StringBuffer();
		String findLine;
		
		while (m.find()) { // 得到 <img >之类
			findLine = m.group();
			System.out.println(findLine);
			String lowCaseTemp2 = findLine.toLowerCase();
			if (( lowCaseTemp2.contains("<img") || lowCaseTemp2.contains("<script") ) && lowCaseTemp2.contains("src=")) {
				sb = new StringBuffer(findLine); 
				int start = sb.indexOf("src=");
				if (start != -1) {
					sb.replace(0, start + 4, ""); //去除“src=”
//					System.out.println("资源："+sb.toString());
					start = sb.indexOf("=");
					if (start != -1) {
						sb.replace(start, sb.length(), "");
						System.out.println(sb.toString());
//							System.out.println(count + " : " + sb.toString());
//							count++;
						srcLink = sb.toString();
					}
					//全部srcLink链接被给为格式：http://www.fanya.chaoxing.com/passport/allHead.shtml
					if(srcLink.indexOf("://")<0){ 
						newLink = "http://"+hostname+srcLink.replaceFirst("[[.]/]*", "/");
						source.add(newLink);
						/*/
						System.out.println(newLink);
						//*/
					}
					else{
						newLink = srcLink;
						source.add(newLink);
						/*/
						System.out.println(newLink);
						//*/
					}
					
					//替换为本地连接 ，格式样例："./帮客之家_files/article.css"
//					newLink = "./"+saveDirFile.getName()+"/"+newLink.substring(newLink.lastIndexOf('/')+1); 
//					line = line.replace(srcLink, newLink);
					if(newLink!=null){
						newLink = "./"+saveDirFile.getName()+"/"+newLink.substring(newLink.lastIndexOf('/')+1); 
						sb = new StringBuffer(line);
						start = line.indexOf(srcLink);
						line = sb.replace(start, start+srcLink.length(), newLink).toString();
					}
				}
			}

			else if (lowCaseTemp2.contains("<link") && lowCaseTemp2.contains("href=")) {
				sb = new StringBuffer(findLine);
//				System.out.println(findLine);
				int start = sb.indexOf("href=");
				if (start != -1) {
					sb.replace(0, start + 5, "");
//					System.out.println("资源："+sb.toString());
					start = sb.indexOf("=");
					if (start != -1) {
						sb.replace(start, sb.length(), "");
						System.out.println(sb.toString());
//						System.out.println(count + " : " + sb.toString());
//						count++;
						
						//全部srcLink链接被给为格式：http://www.fanya.chaoxing.com/passport/allHead.shtml
						srcLink = sb.toString();
						if(srcLink.indexOf("://")<0){ 
							newLink = "http://"+hostname+srcLink.replaceFirst("[[.]/]*", "/");
							source.add(newLink);
							/*/
							System.out.println(newLink);
							//*/
						}
						else{
							newLink = srcLink;
							source.add(newLink);
							/*/
							System.out.println(newLink);
							//*/
						}
						
//						//替换为本地连接 //格式样例："./帮客之家_files/article.css"
						newLink = "./"+saveDirFile.getName()+"/"+newLink.substring(newLink.lastIndexOf('/')+1); 
						sb = new StringBuffer(line);
						start = line.indexOf(srcLink);
						line = sb.replace(start, start+srcLink.length(), newLink).toString();
					}
				}
			}
			
		}	
		return line;
	}
	
	//设想多个端口同时对资源一起下载而写
	class myRunnable implements Runnable{
		String urlString;
		int portNum;
		public myRunnable(String urlString,int portNum){
			this.urlString = urlString;
			this.portNum = portNum;
		}
		@Override
		public void run(){
			try{	
				System.out.println(urlString);
				download_Img_Js_Css(urlString,portNum);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				OK++;
				if(OK==downloadNum){
					jta.append("下载完成");
					JOptionPane.showMessageDialog(null, "下载完成！");
				}
			}
		}
	}
	// URL正规化
	public String formatURL(String srcLink) {
		String newLink;
		if (srcLink.indexOf("://") < 0) {
			newLink = "http://" + hostname + srcLink.replaceFirst("[[.]/]*", "/");
			return newLink;
		} else {
			return srcLink;
		}
		// 全部srcLink链接被给为格式：http://www.fanya.chaoxing.com/passport/allHead.shtml
	}

	public static void clearHead(DataInputStream dis, String tEncode) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("clearhead里面");
		for (;;) {
			int data = dis.read();
			if (data < 0) {
				return;
			}
			if (data == 10) {
				String str = new String(baos.toByteArray(), tEncode);
//				System.out.println(str);
				baos = new ByteArrayOutputStream();
				if (str.trim().length() <= 0)
					break;
			}
			if (data != 13)
				baos.write(data);
		}
		System.out.println("头文件读取完成");
	}

	public synchronized void download_Img_Js_Css(String urlString, int port) throws IOException {
		String tEncode;
		tEncode = "UTF-8";
		String host = getHost(urlString);
		String path = getPath(urlString);
		System.out.println(host + "  " + path);
		InetAddress addr = InetAddress.getByName(host);

		Socket tSocket = new Socket(addr, port);
		BufferedWriter Bwriter = new BufferedWriter(new OutputStreamWriter(tSocket.getOutputStream(), tEncode));
		Bwriter.write("GET " + path + " HTTP/1.0\r\n");
		Bwriter.write("HOST:" + host + "\r\n");
		Bwriter.write("\r\n");
		Bwriter.flush();

		File srcfile = new File(saveDirFile.getAbsolutePath() + "\\" + Helper.getSrcName(path));

		InputStream is = tSocket.getInputStream();
		FileOutputStream fos = new FileOutputStream(srcfile); // 输出
		System.out.println("fileOutput后面");
		DataInputStream dis = new DataInputStream(is);
		clearHead(dis, tEncode);
		System.out.println("clearhead后面");
		// 将得到的文件内容读入到文件输出流中。
		int data = -1;
		byte b[] = new byte[10024];
		while ((data = dis.read(b, 0, 10000)) != -1) {
			fos.write(b, 0, data);
		}
		fos.flush();

		// 关闭输入输出流
		is.close();
		dis.close();
		fos.close();
		if (tSocket.isConnected() && !tSocket.isClosed())
			tSocket.close();
		System.out.println(srcfile.getName() + "下载完成");
		// TODO
	}

	//直接用url.getHost()和url.getPath();发现有很多无法正确获得路径，所以自己写了一个
	public String getHost(String urlString) {
		urlString = urlString.substring(7);
		int index = urlString.indexOf("/");
		if (index > 0)
			return urlString.substring(0, index);
		return urlString;
	}

	public String getPath(String urlString) {
		urlString = urlString.substring(7);
		int index = urlString.indexOf("/");
		if (index > 0)
			return urlString.substring(index);
		return "/" + urlString;
	}

}
