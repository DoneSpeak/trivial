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

//����source�е���������img,js,css�ļ�
public class GetSourceRunnable implements Runnable{
	public static ArrayList<ArrayList<String>> pathLList; //�������pathList,�Ա�ʹ��ͬһ��������socket��������������Ҫ�Ļ���
	public static ArrayList<String> pathList;	//������������������·��������·����������ͬ��������
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
		getHostListAndPathList(source); //�õ�pathLList
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
			while(linkIter.hasNext()){ //������Ϊ����·��
				insureConn(host);
				path = linkIter.next();
				connectServer(host); //��һ���ַ�����������
				sendGetCmd(host,path);
				download_Img_Js_Css(saveDirFile,path);
				System.out.println("ѭ��");
			}
			closeSocket();
			System.out.println("һ�������ϵ���Դ�������");
		}
		Download.OK++;
		if(Download.OK==2)
			Download.jta.append("�������");
		System.out.println("�������");
			
	}

	private void insureConn(String host) {
		isClose = srcDownloadSocket.isClosed();
		while (isClose) {// �Ѿ��Ͽ������½�������
			try {
				connectServer(host);
				isClose = false;
			} catch (Exception se) {
				System.out.println("��������ʧ��:"+host);
				isClose = true;
			}
		}
		
	}

	// �����ӷ�����
	public boolean connectServer(String host) {
		int port = 80;
		try {
			InetAddress addr = InetAddress.getByName(host);
			srcDownloadSocket = new Socket(addr, port);
			srcDownloadSocket.setKeepAlive(true); // ������
			srcDownloadSocket.setSoTimeout(sotimeout); // ���ó�ʱ
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "�޷�����"+host);
			return false;
		}
		isClose = !srcDownloadSocket.isConnected();
		int testTime = 0;
		while (isClose) {
			testTime++;
			if (testTime >= 3) { // ��������3�Σ���������ʱ��������
				JOptionPane.showMessageDialog(null, "�޷�����"+host);
				break;
			}
		}
		if (testTime >= 3){
			System.out.println("����ʧ��");
			return false; // ����������������
		}
		System.out.println("���ӳɹ�");
		return true;
	}

	// �ж��Ƿ�������
//	public boolean judgeConn(Socket so) {
//		try {
//			PrintWriter out = new PrintWriter(so.getOutputStream(), true);
//			out.println("2"); // δ֪����Ƿ����ʲô����Ӱ�죬���������ʾû��������
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}
	
	// �ж�socket�Ƿ��ѶϿ�
//	public boolean _SeverIsClose() {
//		try {
//			srcDownloadSocket.sendUrgentData(0);// ����1���ֽڵĽ������ݣ�Ĭ������£���������û�п����������ݴ�����Ӱ������ͨ��
//			return false;
//		} catch (Exception se) {
//			return true;
//		}
//	}

	// ����getָ��
	public boolean sendGetCmd(String host, String path){
		try {
			Bwriter = new BufferedWriter(new OutputStreamWriter(srcDownloadSocket.getOutputStream(), "UTF-8"));
			Bwriter.write("HOST " + host + "\r\n");
			Bwriter.write("GET " + path + " HTTP/1.0\r\n");
			Bwriter.write("\r\n");
			Bwriter.flush();
			System.out.println("����ָ�����");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ָ���ʧ��");
			return false;
		}

	}

	// �����ļ�:host�µ�path
	public synchronized void download_Img_Js_Css(String saveDir,String path){
		// tEncode = Helper.getFileEncoding(new URL("http://"+host+path));
		try{
			File srcfile = new File(saveDir + "\\" +Helper.getSrcName(path));
			
			InputStream is = srcDownloadSocket.getInputStream();
			
			FileOutputStream fos = new FileOutputStream(srcfile); // ���
			DataInputStream dis = new DataInputStream(is);
			clearHead(dis, "UTF-8"); // ������е�ͷ��Ϣ
			
			int data = -1;
			byte b[] = new byte[10024];
			while ((data = dis.read(b, 0, 10000)) != -1) {
				fos.write(b, 0, data);
			}

			fos.flush();
			System.out.println("����"+path+"���");
			// �ر����������
			is.close();
			fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//�ر�socket
	public void closeSocket(){
		try{		
			if(srcDownloadSocket.isConnected() && !srcDownloadSocket.isClosed()){
				srcDownloadSocket.close();
				System.out.println("socket�رճɹ�");
				return;
			}
			System.out.println("socket�ر�ʧ��");
		}catch(Exception e){
			System.out.println("socket�ر�ʧ��");
			e.printStackTrace();
		}
	}
	// �����ֽڶ�ȡһ���ַ���
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

	// ���ͷ��Ϣ
	public static void clearHead(DataInputStream dis, String tEncode) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("clearhead����");
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
		System.out.println("ͷ�ļ���ȡ���");
	}
	//TODO
	// ������������·������
	public void getHostListAndPathList(ArrayList<String> linkSrc) {
		// http://blog.csdn.net/xiaoranchenxi/article/details/4008351
		// �������url��ȡ��
		// host :blog.csdn.net 
		// path:/xiaoranchenxi/article/details/4008351
		
		//����ʹ���е���������ͬ�Ŀ���һ��
		linkSrc.sort(new Comparator<String>(){
			@Override
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}	
		});
		hostList = new ArrayList<String>();
		pathLList = new ArrayList<ArrayList<String>>();
		// �����Ѿ�����������������ͬ�Ķ�����һ����
		Iterator<String> iter = linkSrc.iterator(); // linkSrc
													
		while (iter.hasNext()) {
			String src = iter.next();
			if (Helper.isLegalURL(src)) {
				
				String host = getHost(src);
				String path = getPath(src);
				if (hostList.contains(host)) { // ������ǰ��ArrayList���Ǹ���������List
					pathList.add(path);
				} else {
					pathList = new ArrayList<String>();
					pathList.add(host); // ��һ��Ԫ����Ϊ��������������Ϊsocket����������
					hostList.add(host);
					pathLList.add(pathList);
				}
			}
			else{
				System.out.println(src+" ����http����");
			}
			// �Ƿ���url,����https://Э������ӣ����ᱻ������������
		}
		//[DEBUG]���鿴��Դ�Ƿ��Ѽ��������ռ���ȷ
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
//				System.out.println("����Ϊ��"+host);
//			}
//			while(linkIter.hasNext()){ //������Ϊ����·��
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
