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

//����������
class SimpleFrame extends JFrame implements ActionListener {
	JPanel jp = new JPanel();
	JTextField inputUrl = new JTextField(26);
	JButton btn = new JButton("����");
	JTextArea showStatus = new JTextArea();
	JScrollPane jsp;
	Download download;

	public SimpleFrame() {
		super("��ҳץȡ");
		setLayout(new BorderLayout());
		//���ô��������ʾ
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

		if (e.getSource().equals(btn)) { // ������Ҫ�õ�Դ
			if (inputUrl.getText() == null || inputUrl.getText().equals("")
					|| inputUrl.getText().equals(Character.toString((char) '0'))) {
				JOptionPane.showMessageDialog(this, "��������ַ��");
				return;
			}
			String url = inputUrl.getText();
			if (url.indexOf("://") < 0) {
				url = "http://" + url;
			}
			String errMsg = Helper.checkHTTP(url);
			if (errMsg != null) {
				JOptionPane.showMessageDialog(this, errMsg + "\n" + "����������Э��Ϊhttp����ַ");
				// System.out.println("��ҳ���Ӳ��Ϸ���");
				return;
			}
			JFileChooser fChooser = new JFileChooser();
			fChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY); // ����������ѡ����ʽ�����ô򿪶Ի���
			fChooser.showDialog(null, "����");

			File saveDir = fChooser.getSelectedFile();
			if (saveDir == null) {
				return;
			}
			// �����߳��������ļ������߳��д�����ҳ��Ϣ
			download = new Download(url, showStatus, saveDir.getAbsolutePath());
			Thread thread = new Thread(download);
			thread.start();
		}
	}

}

// ������ҳ����Դ
class Download implements Runnable {
	// [start]Download�ı���
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
	String encode = "UTF-8"; // ����Ĭ��UTF-8

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
	//����html�ļ�����ȡhtml�е�img,js,css���ӣ�html�����滻
	public void run() {
		//System.out.println("thread"+javax.swing.SwingUtilities.isEventDispatchThread()); //�жϿ�֪���ں�̨�߳�
		try {
			URL url = new URL(urlString);
			//[start] ���ı�����չʾ������Ϣ
			info.append("����������" + url.getHost() + "\n");
			info.append("���ʶ˿ڣ�" + url.getDefaultPort() + "\n");
			jta.setText(info.toString());
			//[end]
	
			//[start]����socket������ָ��
			encode = Helper.getFileEncoding(url);
			String params = URLEncoder.encode("param1", encode) + "=" + URLEncoder.encode("value1", encode);
			params += "&" + URLEncoder.encode("param2", encode) + "=" + URLEncoder.encode("value2", encode);

			hostname = url.getHost();		 
			int port = 80;
			InetAddress addr = InetAddress.getByName(hostname);
			socket = new Socket(addr, port);
			socket.setSoTimeout(4000); //����4��ȴ�ʱ��
			String path = url.getPath() == null?"/":url.getPath();
			
			wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encode));
			if(path.equals("/"))
				wr.write("GET " + path + " HTTP/1.0\r\n");
			else
				wr.write("POST " + path + " HTTP/1.0\r\n"); //����Ҫ�ύ��Ϣ������ֻҪget����
			wr.write("HOST:" + hostname + "\r\n");
			wr.write("Content-Length: " + params.length() + "\r\n");
			wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
//			wr.write("User-Agent:myself\r\n"+"Accept:www/source;text/html;image/gif;*/*\r\n");
			wr.write("\r\n");
			wr.write(params);
			wr.flush();
			//[end]
			jta.append("��������..." + "\n");
			
			rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), encode));
			String line = null;
			
			//ȥ��ͷ����Ϣ
			clearHead(new DataInputStream(socket.getInputStream()),encode);
			
			//[start]��ȡtitle
			String title = null;
			
			while ((line = rd.readLine()) != null) {
//				System.out.print(line+"\r\n");	
				line = formatLine(line,new File(saveDir)); //���ǵ�����ҳtitle��������Ŀ¼��δ���ɣ��������ﱣ����ѡ��ı���Ŀ¼��
				htmlSb.append(line);
				htmlSb.append("\r\n");
				if(line.contains("</title>"))
				{
					String temp = htmlSb.toString().trim().replaceAll("([(\r)(\n)]*)","").replaceAll(".*<title>\\s*", "");
					title = temp.replaceAll("\\s*</title>.*", "");
					break;
				}
			}


			// ��ȡ������Ϣ������������ʧ��
			if (htmlSb.length() <= 0) {
				JOptionPane.showMessageDialog(null, "�޷����ش���ҳ��");
				jta.append("����ʧ��");
				socket.close();
				return;
			}
			
			//û���ҵ���Ŀ������ҳ�����һ����Ϊ��Ŀ
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
			//�滻�ļ����еķǷ��ַ�
			title = title.replaceAll("[\\/:[*][?]\"<>|]", "_");
			title = title.replaceAll("\\s+", " ");
			//[end]��ȡtitle
			//[start]�����ļ�,�ڱ����ļ����Ѵ����ļ��ǣ����ļ���������չ����ȡ����·����Ϊ�����ĵ�img,js,css·���滻׼��
			savefile = new File(saveDir+"\\"+title+".html");
			int n = 0;
			String expand = "_net";
			while(savefile.exists()){
				savefile  = new File(saveDir+"\\"+title+expand+".html");
				n++;
				expand = expand.substring(0,expand.lastIndexOf("t"))+"t_"+n;	
			}
			//��������img���ļ���Ŀ¼
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
			//��ȡʣ�µ��ַ���
			while ((line = rd.readLine()) != null) {
				line = formatLine(line,saveDirFile); //��ȡ���ӣ�ͬʱ�滻line������Ϊ����
				tohtmlfile.write(line);
				tohtmlfile.newLine();
				tohtmlfile.flush();
			}
			
			wr.close();
			rd.close();
			tohtmlfile.close();
			System.out.println("�ɹ�����html�ļ�");
			OK++; //һ����Դ����
			downloadNum++;
			if(source.size()==0){
				jta.append("�������");
			}

			//����js,img,css
			Iterator <String> iter = source.iterator();
			int portNum = 80;
			while(iter.hasNext()){
				String urlString = iter.next();
				if(Helper.isLegalURL(urlString)){	
					downloadNum++;
//					portNum ++; //�Ե�һ�������ж�˿ڷ������������һ��partNum
					new Thread(new myRunnable(urlString,portNum)).start();		
				}
				
			}
			
//TODO		�Ե�һ���������������д��������Դ�̣߳�δ����
//			new Thread(new GetSourceRunnable(source,saveDirFile.getAbsolutePath())).start();
			
			if(socket.isConnected() && !socket.isClosed()){
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("socket�رճ���");
				e.printStackTrace();
			}
		}
		}catch(NumberFormatException e){
			System.err.println("��Դ��ȡ����\n");
			JOptionPane.showMessageDialog(null, "�޷����ش���ҳ��");
			e.printStackTrace();
		} catch(IOException e){
			JOptionPane.showMessageDialog(null, "�޷����ش���ҳ��");
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public String formatLine(String line,File saveDirFile){
		String temp = line.replaceAll("[=\"'\\s]+", "=");  //src="http://a"; src='http://a'; src=http://a ת��Ϊ src=http://a=
		Pattern p = Pattern.compile(SOURCE_REG);
		Matcher m = p.matcher(temp);
		String srcLink = null;
		String newLink = null;
		StringBuffer sb = new StringBuffer();
		String findLine;
		
		while (m.find()) { // �õ� <img >֮��
			findLine = m.group();
			System.out.println(findLine);
			String lowCaseTemp2 = findLine.toLowerCase();
			if (( lowCaseTemp2.contains("<img") || lowCaseTemp2.contains("<script") ) && lowCaseTemp2.contains("src=")) {
				sb = new StringBuffer(findLine); 
				int start = sb.indexOf("src=");
				if (start != -1) {
					sb.replace(0, start + 4, ""); //ȥ����src=��
//					System.out.println("��Դ��"+sb.toString());
					start = sb.indexOf("=");
					if (start != -1) {
						sb.replace(start, sb.length(), "");
						System.out.println(sb.toString());
//							System.out.println(count + " : " + sb.toString());
//							count++;
						srcLink = sb.toString();
					}
					//ȫ��srcLink���ӱ���Ϊ��ʽ��http://www.fanya.chaoxing.com/passport/allHead.shtml
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
					
					//�滻Ϊ�������� ����ʽ������"./���֮��_files/article.css"
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
//					System.out.println("��Դ��"+sb.toString());
					start = sb.indexOf("=");
					if (start != -1) {
						sb.replace(start, sb.length(), "");
						System.out.println(sb.toString());
//						System.out.println(count + " : " + sb.toString());
//						count++;
						
						//ȫ��srcLink���ӱ���Ϊ��ʽ��http://www.fanya.chaoxing.com/passport/allHead.shtml
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
						
//						//�滻Ϊ�������� //��ʽ������"./���֮��_files/article.css"
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
	
	//�������˿�ͬʱ����Դһ�����ض�д
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
					jta.append("�������");
					JOptionPane.showMessageDialog(null, "������ɣ�");
				}
			}
		}
	}
	// URL���滯
	public String formatURL(String srcLink) {
		String newLink;
		if (srcLink.indexOf("://") < 0) {
			newLink = "http://" + hostname + srcLink.replaceFirst("[[.]/]*", "/");
			return newLink;
		} else {
			return srcLink;
		}
		// ȫ��srcLink���ӱ���Ϊ��ʽ��http://www.fanya.chaoxing.com/passport/allHead.shtml
	}

	public static void clearHead(DataInputStream dis, String tEncode) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("clearhead����");
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
		System.out.println("ͷ�ļ���ȡ���");
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
		FileOutputStream fos = new FileOutputStream(srcfile); // ���
		System.out.println("fileOutput����");
		DataInputStream dis = new DataInputStream(is);
		clearHead(dis, tEncode);
		System.out.println("clearhead����");
		// ���õ����ļ����ݶ��뵽�ļ�������С�
		int data = -1;
		byte b[] = new byte[10024];
		while ((data = dis.read(b, 0, 10000)) != -1) {
			fos.write(b, 0, data);
		}
		fos.flush();

		// �ر����������
		is.close();
		dis.close();
		fos.close();
		if (tSocket.isConnected() && !tSocket.isClosed())
			tSocket.close();
		System.out.println(srcfile.getName() + "�������");
		// TODO
	}

	//ֱ����url.getHost()��url.getPath();�����кܶ��޷���ȷ���·���������Լ�д��һ��
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
