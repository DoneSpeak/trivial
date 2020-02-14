package main;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JOptionPane;

import Helper.Helper;

//利用source中的链接下载img,js,css文件
public class GetSourceRunnable implements Runnable{
	public static ArrayList<ArrayList<String>> pathLList; //用来存放pathList,以便使用同一个主机的socket，减少链接所需要的花销
	public static ArrayList<String> pathList;	//存放链接相对于主机的路径，所有路径都含有相同的主机名
	ArrayList<String> hostList;
	ArrayList<String> source;
	String saveDirFile;
	
	public static final Integer sotimeout = 3000;
	public BufferedWriter Bwriter = null;
	Socket srcDownloadSocket;
	String tEncode;
	boolean isClose;
	
	
	public GetSourceRunnable(ArrayList<String> source, String saveDirFile) {
		this.source = source;
		this.saveDirFile = saveDirFile;
	}

	@Override
	public void run() {
		getHostListAndPathList(source); //得到pathLList
		Iterator<ArrayList<String>> iter = pathLList.iterator();
		ArrayList<String> src;
		while (iter.hasNext()) {
			src = iter.next();
			Iterator<String> linkIter = src.iterator();
			String path = null;
			String host = null;
			if(linkIter.hasNext()){
				host = linkIter.next();
			}
			while(linkIter.hasNext()){ //其他的为链接路径
				insureConn(host);
				path = linkIter.next();
				connectServer(host); //第一条字符串是主机名
				sendGetCmd(host,path);
				download_Img_Js_Css(saveDirFile,path);
				System.out.println("循环");
			}
			closeSocket();
			System.out.println("一个主机上的资源下载完成");
		}
		Download.OK++;
		if(Download.OK==2)
			Download.jta.append("下载完成");
		System.out.println("下载完成");
			
	}

	private void insureConn(String host) {
		isClose = srcDownloadSocket.isClosed();
		while (isClose) {// 已经断开，重新建立连接
			try {
				connectServer(host);
				isClose = false;
			} catch (Exception se) {
				System.out.println("创建连接失败:"+host);
				isClose = true;
			}
		}
		
	}

	// 长连接服务器
	public boolean connectServer(String host) {
		int port = 80;
		try {
			InetAddress addr = InetAddress.getByName(host);
			srcDownloadSocket = new Socket(addr, port);
			srcDownloadSocket.setKeepAlive(true); // 长连接
			srcDownloadSocket.setSoTimeout(sotimeout); // 设置超时
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "无法连接"+host);
			return false;
		}
		isClose = !srcDownloadSocket.isConnected();
		int testTime = 0;
		while (isClose) {
			testTime++;
			if (testTime >= 3) { // 尝试连接3次，都连不上时放弃连接
				JOptionPane.showMessageDialog(null, "无法连接"+host);
				break;
			}
		}
		if (testTime >= 3){
			System.out.println("链接失败");
			return false; // 结束该主机的连接
		}
		System.out.println("链接成功");
		return true;
	}

	// 判断是否连接上
//	public boolean judgeConn(Socket so) {
//		try {
//			PrintWriter out = new PrintWriter(so.getOutputStream(), true);
//			out.println("2"); // 未知这个是否会有什么不良影响，如果报错，表示没有连接上
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}
	
	// 判断socket是否已断开
//	public boolean _SeverIsClose() {
//		try {
//			srcDownloadSocket.sendUrgentData(0);// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
//			return false;
//		} catch (Exception se) {
//			return true;
//		}
//	}

	// 发送get指令
	public boolean sendGetCmd(String host, String path){
		try {
			Bwriter = new BufferedWriter(new OutputStreamWriter(srcDownloadSocket.getOutputStream(), "UTF-8"));
			Bwriter.write("HOST " + host + "\r\n");
			Bwriter.write("GET " + path + " HTTP/1.0\r\n");
			Bwriter.write("\r\n");
			Bwriter.flush();
			System.out.println("发送指令完成");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("指令发送失败");
			return false;
		}

	}

	// 下载文件:host下的path
	public synchronized void download_Img_Js_Css(String saveDir,String path){
		// tEncode = Helper.getFileEncoding(new URL("http://"+host+path));
		try{
			File srcfile = new File(saveDir + "\\" +Helper.getSrcName(path));
			
			InputStream is = srcDownloadSocket.getInputStream();
			
			FileOutputStream fos = new FileOutputStream(srcfile); // 输出
			DataInputStream dis = new DataInputStream(is);
			clearHead(dis, "UTF-8"); // 清除流中的头信息
			
			int data = -1;
			byte b[] = new byte[10024];
			while ((data = dis.read(b, 0, 10000)) != -1) {
				fos.write(b, 0, data);
			}

			fos.flush();
			System.out.println("下载"+path+"完成");
			// 关闭输入输出流
			is.close();
			fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//关闭socket
	public void closeSocket(){
		try{		
			if(srcDownloadSocket.isConnected() && !srcDownloadSocket.isClosed()){
				srcDownloadSocket.close();
				System.out.println("socket关闭成功");
				return;
			}
			System.out.println("socket关闭失败");
		}catch(Exception e){
			System.out.println("socket关闭失败");
			e.printStackTrace();
		}
	}
	// 利用字节读取一行字符串
	public static String readLine(DataInputStream dIS, String tEncode) throws IOException {
		int data = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((data = dIS.read()) != -1) {
			if (data == (int) '\n')
				break;
			baos.write(data);
		}
		return new String(baos.toByteArray(), tEncode);
	}

	// 清除头信息
	public static void clearHead(DataInputStream dis, String tEncode) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("clearhead里面");
		int n = 0;
		for (;;) {
			int data = dis.read();
			if (data < 0) {
				System.out.println(n);
				return;
			}
			if (data == 10) {
				String str = new String(baos.toByteArray(), tEncode);
				System.out.println(str);
				baos = new ByteArrayOutputStream();
				if (str.trim().length() <= 0)
					break;
			}
			if (data != 13)
				baos.write(data);
			n++;
			System.out.println((char)data);
		}
		System.out.println("头文件读取完成");
	}
	//TODO
	// 分离主机名和路径名称
	public void getHostListAndPathList(ArrayList<String> linkSrc) {
		// http://blog.csdn.net/xiaoranchenxi/article/details/4008351
		// 中如果用url获取：
		// host :blog.csdn.net 
		// path:/xiaoranchenxi/article/details/4008351
		
		//排序，使所有的主机名相同的靠在一起
		linkSrc.sort(new Comparator<String>(){
			@Override
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}	
		});
		hostList = new ArrayList<String>();
		pathLList = new ArrayList<ArrayList<String>>();
		// 数据已经排序，所有主机名相同的都靠在一起了
		Iterator<String> iter = linkSrc.iterator(); // linkSrc
													
		while (iter.hasNext()) {
			String src = iter.next();
			if (Helper.isLegalURL(src)) {
				
				String host = getHost(src);
				String path = getPath(src);
				if (hostList.contains(host)) { // 表明当前的ArrayList就是该主机名的List
					pathList.add(path);
				} else {
					pathList = new ArrayList<String>();
					pathList.add(host); // 第一个元素作为主机名，用于作为socket的链接主机
					hostList.add(host);
					pathLList.add(pathList);
				}
			}
			else{
				System.out.println(src+" 不是http链接");
			}
			// 非法的url,比如https://协议的链接，将会被跳过不做处理
		}
		//[DEBUG]：查看资源是否搜集完整和收集正确
		System.out.println("******************");
		//TODO
		Iterator<String> iter2 = linkSrc.iterator();
		while(iter2.hasNext()){
			System.out.println(iter2.next());
		}
		System.out.println("******************");
//		System.out.println("******************");
//		Iterator<ArrayList<String>> itere = pathLList.iterator();
//		ArrayList<String> src;
//		while (itere.hasNext()) {
//			src = itere.next();
//			Iterator<String> linkIter = src.iterator();
//			String path = null;
//			String host = null;
//			int count = 1;
//			if(linkIter.hasNext()){
//				host = linkIter.next();
//				System.out.println("主机为："+host);
//			}
//			while(linkIter.hasNext()){ //其他的为链接路径
//				path = linkIter.next();
//				System.out.println(count + " "+path);
//				count++;
//			}
//		}
//		System.out.println("******************");
	}

	private String getHost(String urlString) {
		urlString = urlString.substring(7);
		int index = urlString.indexOf("/");
		if(index > 0 )
			return urlString.substring(0,index);
		return urlString;
	}
	private String getPath(String urlString) {
		urlString = urlString.substring(7);
		int index = urlString.indexOf("/");
		if(index > 0 )
			return urlString.substring(index);
		return "/"+urlString;
	}
}
