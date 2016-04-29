import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ChatFrame extends JFrame implements ActionListener{
	//[start]全局变量
	public static int CHATHEIGHT = 550;
	public static int CHATWIDTH = 418;
	public static int VIEWHEIGHT = 470;
	public static int HISTWIDTH = 344;
	public static boolean START = true;
	public static boolean END = false;
	
	boolean showHistory = false;
	boolean isfirstSend = true;
	boolean startOrEnd = END;
	boolean startSuccess = true;
	boolean hasStart = false;
	boolean sending = false;
	private String TouchIp = "localhost";
	private int myPost = 5080;
	private int toPort = 5020;
	private int receivePort = 5080+1;
	private File choosefile = null;
	
	JSplitPane mainPane;
	JPanel chatPane,inputPane,histPane,histOper;
	JTextArea chatText,historyText;
	JTextField IpField,msgField,postField,myPostField;
	MyButton sendBtn,histBtn,clearBtn,reflushBtn,clearfileBtn,startBtn;
	JScrollPane histJs,chatJs;
	
	File historyFile;
	//[end]
	
	public ChatFrame(){
		
		//[start]设置窗体
		super("SimpleChat");
		Toolkit kit=getToolkit();
		Dimension winSize=kit.getScreenSize();
		setBounds((winSize.width-CHATWIDTH)/2,(winSize.height-CHATHEIGHT)/2,CHATWIDTH,CHATHEIGHT);
		setMinimumSize(new Dimension(CHATWIDTH,CHATHEIGHT));
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//[end]
		//[start]聊天区域
		chatPane = new JPanel();
		chatPane.setBounds(0,0,418,536);
		chatPane.setLayout(new BorderLayout());
		chatPane.setVisible(true);
		//[end]
		//[start]显示消息
		chatText = new JTextArea();
		//chatText.setPreferredSize(new Dimension(CHATWIDTH, VIEWHEIGHT));
		chatText.setEditable(false);
		chatText.setLineWrap(true);         //自动换行
		chatText.setWrapStyleWord(true);    //不断字
		chatJs = new JScrollPane(chatText);
		chatPane.add(chatJs,BorderLayout.CENTER);
		//[end]
		//[start]输入区域及按钮
		inputPane = new JPanel();
		inputPane.setPreferredSize(new Dimension(CHATWIDTH,CHATHEIGHT - VIEWHEIGHT));
		
		IpField = new JTextField(9);
		IpField.setText("TouchIp");
		msgField = new JTextField(25);
		Box baseBox = Box.createVerticalBox();
		Box boxH1 = Box.createHorizontalBox();
		boxH1.add(IpField);
		boxH1.add(Box.createHorizontalStrut(7));
		boxH1.add(msgField);
		baseBox.add(Box.createVerticalStrut(4));
		baseBox.add(boxH1);
		
		postField = new JTextField(4);
		postField.setText("ToPost");
		myPostField = new JTextField(4);
		myPostField.setText("MyPost");
		startBtn = new MyButton("start");
		startBtn.addActionListener(this);	
		sendBtn = new MyButton("send"); 
		sendBtn.addActionListener(this);		
		histBtn = new MyButton("history");
		histBtn.addActionListener(this);
		clearBtn = new MyButton("clear");
		clearBtn.addActionListener(this);
		Box boxH2 = Box.createHorizontalBox();
		
		boxH2.add(postField);
		boxH2.add(Box.createHorizontalStrut(4));
		boxH2.add(myPostField);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(sendBtn);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(clearBtn);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(startBtn);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(histBtn);
		baseBox.add(Box.createVerticalStrut(8));
		baseBox.add(boxH2);
		
		inputPane.add(baseBox);
		chatPane.add(inputPane,BorderLayout.SOUTH);
		//[end]
		
		
		//[start]显示历史消息区域	
		histPane = new JPanel(new BorderLayout());
		histPane.setVisible(false);
		
		historyText = new JTextArea();
		historyText.setLineWrap(true);         //自动换行
		historyText.setWrapStyleWord(true);    //不断字
		historyText.setEditable(false); 
		histJs = new JScrollPane(historyText);
		histPane.add(BorderLayout.CENTER,histJs);
		//[end]
		//[start]历史消息操作
		histOper = new JPanel();
		histOper.setPreferredSize(new Dimension(CHATWIDTH,(CHATHEIGHT - VIEWHEIGHT)/2));
		reflushBtn = new MyButton("reflush");
		reflushBtn.addActionListener(this);
		clearfileBtn = new MyButton("clear");
		clearfileBtn.addActionListener(this);
		boxH1 = Box.createHorizontalBox();
		boxH1.add(reflushBtn);
	
		boxH1.add(clearfileBtn);
		boxH1.add(Box.createHorizontalStrut(40));
		boxH1.add(clearfileBtn);
		histOper.add(boxH1);
		histPane.add(BorderLayout.SOUTH,histOper);
		
		//jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//[end]
		//[start]添加主体
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,chatPane,histPane);
		mainPane.setDividerLocation(418);
		mainPane.setDividerSize(1);
		this.getContentPane().add(mainPane);
		validate();
		//[end]
	}
	
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == sendBtn){ //发送消息
			if(!hasStart){
				return;
			}
			String text = msgField.getText();
			if(text.equals("[fasongwenjian]") && sending == true){
				//暂时限制一次仅可以发送一个文件
				JOptionPane.showMessageDialog(null, "有文件正在发送，请稍后！", "提醒", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			try {
				
				//判断是否是发送文件的指令
				
				FileWriter out = new FileWriter(historyFile,true);
				BufferedWriter writer = new BufferedWriter(out);
				Date nowTime = new Date();
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String info = "<< "+"ME"+": "+matter.format(nowTime);
				chatText.append(info + "\n");
				writer.append(info + "\r\n");
				chatText.append(text + "\n\n");
				writer.append(text + "\r\n\n");
				
				chatText.setCaretPosition(chatText.getText().length());
				msgField.setText("");
				writer.flush();	
				
				if(text.equalsIgnoreCase("[fasongwenjian]")){
					
						JFileChooser fc = new JFileChooser();
						int returnVal = fc.showOpenDialog(this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {			
							//得到选择的文件名
							choosefile = fc.getSelectedFile();
							//TODO 限制发送文件大小,暂时不做限制处理
							/*
							while(choosefile.length() < ){
								
							}
							*/
							System.out.println("发送的文件大小为：" + choosefile.length());
							sending = true;
							//writer.append(choosefile.getAbsolutePath() + "\r\n");
						}
						else{
							//取消发送,对方无需知道我方曾经打算发送文件
							return;
						}
					
				}
				//需要确认发送再发送
				InetAddress address = null;
				address = InetAddress.getByName(TouchIp);
				//将post放到消息中，以便区分第三者介入
				String postStr = myPost + "#";				
				text = postStr + text;
				byte msg[] = text.trim().getBytes();
				
				DatagramPacket data = new DatagramPacket(msg,msg.length,address,toPort);
				DatagramSocket mail = new DatagramSocket();
				mail.send(data);
				mail.close();
				out.close();
				writer.close();
			} catch (Exception err) {
				JOptionPane.showMessageDialog(null, "消息发送失败！", "Warning", JOptionPane.WARNING_MESSAGE);
				err.printStackTrace();
			}
			
		}
		else if(e.getSource() == histBtn){
			if(!hasStart){
				return;
			}
			if(showHistory == true){
				System.out.println(mainPane.getMinimumDividerLocation());
				showHistory = false;
				histPane.setVisible(false);
				 
				this.setPreferredSize(new Dimension(CHATWIDTH,CHATHEIGHT));
				this.pack();
			}
			else{
				System.out.println(mainPane.getMinimumDividerLocation());
				showHistory = true;
				histPane.setVisible(true);
				this.setPreferredSize(new Dimension(CHATWIDTH+HISTWIDTH,CHATHEIGHT));
				this.pack();
			}
			//读取记录
			historyText.setText("");
			if(historyFile.length() == 0){
				historyText.setText("历史记录为空"+"\n");
				return;
			}
			new Thread(new Runnable(){
				@Override
				public void run(){
					try {
						FileReader in = new FileReader(historyFile);
						BufferedReader reader = new BufferedReader(in);
						String str = null;
						while((str = reader.readLine()) != null){
							historyText.append(str+"\n");
							historyText.setCaretPosition(historyText.getText().length());
						}
						reader.close();
						in.close();
					} catch (Exception err) {
						JOptionPane.showMessageDialog(null, "读取消息记录出错！", "Warning", JOptionPane.WARNING_MESSAGE);
						err.printStackTrace();
					}
					
				}
			}).start();
		}
		else if(e.getSource() == clearBtn){
			if(!hasStart){
				return;
			}
			chatText.setText("");
		}
		else if(e.getSource() == reflushBtn){
			if(!hasStart){ 
				return ;
			}
			historyText.setText("");
			if(historyFile.length() == 0){
				historyText.setText("历史记录为空"+"\n");
				return;
			}
			new Thread(new Runnable(){
				@Override
				public void run(){
					try {
						FileReader in = new FileReader(historyFile);
						BufferedReader reader = new BufferedReader(in);
						String str = null;
						while((str = reader.readLine()) != null){
							historyText.append(str+"\n");
							historyText.setCaretPosition(historyText.getText().length());
						}
						reader.close();
						in.close();
					} catch (Exception err) {
						JOptionPane.showMessageDialog(null, "读取消息记录出错！", "Warning", JOptionPane.WARNING_MESSAGE);
						err.printStackTrace();
					}
					
				}
			}).start();
		}
		else if(e.getSource() == clearfileBtn){
			if(!hasStart){
				return;
			}
			
			
			try {
				FileWriter out = new FileWriter(historyFile,false);
				BufferedWriter writer = new BufferedWriter(out);
				writer.write("");
				writer.flush();
				out.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			
			historyText.setText("历史记录为空"+"\n");
			historyFile.delete();
			historyFile = new File(IpField.getText() + "-" + toPort + ".txt");
		}
		else if(e.getSource() == startBtn){
			if(startOrEnd == START){
				IpField.setEditable(true);
				postField.setEditable(true);
				myPostField.setEditable(true);
				startBtn.setText("start");
				startOrEnd = END;
				return;
			}
			
			TouchIp = IpField.getText();
			if(!Helper.judgeIp(TouchIp)){
				JOptionPane.showMessageDialog(null, "请确保对方Ip输入正确！", "Warning", JOptionPane.WARNING_MESSAGE);
				return ;
			}
			try
			{
				toPort = Integer.parseInt(postField.getText()); //获得发送目的端口
			}catch(Exception err){
				JOptionPane.showMessageDialog(null, "ToPost出错，请确认输入正确！", "Warning", JOptionPane.WARNING_MESSAGE);
				return ;
			}
			try
			{
				myPost = Integer.parseInt(myPostField.getText()); //获得发送目的端口
			}catch(Exception err){
				JOptionPane.showMessageDialog(null, "MyPost出错，请确认输入正确！", "Warning", JOptionPane.WARNING_MESSAGE);
				return ;
			}
			
			historyFile = new File(IpField.getText() + "-" + toPort + ".txt");
			
			chatText.setText("聊天开始"+"\n");
			chatText.append("------------------"+"\n");
			chatText.setCaretPosition(chatText.getText().length());
			if(startSuccess){
				IpField.setEditable(false);
				postField.setEditable(false);
				myPostField.setEditable(false);
				startBtn.setText("end");
				startOrEnd = START;
				hasStart = true;
			}
			
			new Thread(new Runnable(){
				@Override
				public void run(){
					//接受消息
					
					DatagramPacket pack = null;
					DatagramSocket mail = null;
					byte[] msg = new byte[8192];
					try{
						pack = new DatagramPacket(msg,msg.length);
						mail = new DatagramSocket(myPost);  //监督端口，即本机端口
					
					}catch(Exception err){
						startSuccess = false;
						chatText.setText("");
						chatText.setCaretPosition(0);
						
						IpField.setEditable(true);
						postField.setEditable(true);
						myPostField.setEditable(true);
						startBtn.setText("start");
						startOrEnd = END;
						startSuccess = true;
						
						err.printStackTrace();
						JOptionPane.showMessageDialog(null, "出错！将检查端口是否重复使用！", "Warning", JOptionPane.WARNING_MESSAGE);
						
						return;
					}
					FileWriter out = null;
					BufferedWriter writer = null;
					while(true){					
						try{
							if(startOrEnd == END){
								break;
							}
							System.out.println("等待消息！");
							mail.receive(pack);
							if(startOrEnd == END){
								break;
							}
							String host = pack.getAddress().getHostAddress();
							
							System.out.println("收到消息！");
							String message = new String(pack.getData(),0,pack.getLength());
							String portStr = message.substring(0,message.indexOf("#"));
							message = message.substring(message.indexOf("#")+1);
							int port = Integer.parseInt(portStr);
							
							if(!host.equals(TouchIp) || port != toPort){
								String infoStr = "IP地址："+host+"\n"+"端口号：" + port+"\n"+message + "\n" + "是否回复？";
								int n = JOptionPane.showConfirmDialog(null, infoStr,"收到其他消息",JOptionPane.YES_NO_OPTION);
								//JOptionPane.showMessageDialog(null, infoStr);
								if(n == JOptionPane.YES_OPTION){
									String str = JOptionPane.showInputDialog(null, "请输入回复信息","回复"+host+":"+port,JOptionPane.INFORMATION_MESSAGE);
									if(str != null || str.length() != 0){
										InetAddress address = null;
										address = InetAddress.getByName(host);
										//将post放到消息中，以便区分第三者介入
										String postStr = myPost + "#";				
										str = postStr + str;
										byte _msg[] = str.trim().getBytes();
										
										DatagramPacket data = new DatagramPacket(_msg,_msg.length,address,port);
										DatagramSocket _mail = new DatagramSocket();
										_mail.send(data);
										_mail.close();
									}
								}
								continue;
							}
							//判断是否为接收指令
							if(message.equals("#!!!Y***&")){
								//对方返回接收指令，利用socket发送文件
								Socket socket = null;
								System.out.println("收到确认");
								socket = new Socket(TouchIp,toPort);
								
								System.out.println("与服务器连接成功");
								DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
								DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(choosefile.getAbsolutePath())));
								
								int size = 8192;
								byte[] b = new byte[size];
								//发送路径测试使用
								dos.writeUTF(choosefile.getAbsolutePath());
								dos.flush();
								System.out.println("文件发送完成\n");
								//发送文件
								int read = 0;
								while(true){
									
									if(dis != null){
										read = dis.read(b);
									}else{
										break;
									}
									
									if(read == -1){
										break;
									}
									
									dos.write(b,0,read);
								}
								dos.flush();
								
								dos.close();
								dis.close();
								socket.close();
								sending = false; //发送状态取消、
								JOptionPane.showMessageDialog(null, "文件发送完成！", "提示", JOptionPane.WARNING_MESSAGE);
							}else if(message.equals("#!!!N***&")){
								//拒绝接收指令
								JOptionPane.showMessageDialog(null, "对方拒绝接收！", "Warning", JOptionPane.WARNING_MESSAGE);
								sending = false;
							}else if(message.equals("[fasongwenjian]")){
								//显示发送消息
								out = new FileWriter(historyFile,true);
								writer = new BufferedWriter(out);
								Date nowTime = new Date();
								SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String info = ">> "+InetAddress.getByName(IpField.getText()).getHostName()+": "+matter.format(nowTime);
								chatText.append(info + "\n");
								writer.write(info + "\r\n");
								chatText.append(message + "\n\n");
								writer.write(message + "\r\n\n");
								writer.flush();
								out.close();
								chatText.setCaretPosition(chatText.getText().length());
								
								//接受到对方发送文件的指令
								int n = JOptionPane.showConfirmDialog(null, "对方发来文件，接收吗?", "收到文件", JOptionPane.YES_NO_OPTION); 
						        if(n == JOptionPane.YES_OPTION) { 
						        	//选择保存路径
						        	JFileChooser fChooser = new JFileChooser();
									fChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY); // 必须先设置选择样式再设置打开对话框
									fChooser.showDialog(null, "选择保存路径");

									File saveDir = fChooser.getSelectedFile();
									if (saveDir == null) {
										//返回拒绝接受消息
										//将post放到消息中，以便区分第三者介入
										String msgStr = myPost + "#";				
										msgStr = msgStr + "#!!!N***&";
										byte refuseMsg[] = msgStr.getBytes();	
										
										InetAddress address = null;
										address = InetAddress.getByName(TouchIp);
											
										DatagramPacket data = new DatagramPacket(refuseMsg,refuseMsg.length,address,toPort);
										DatagramSocket refuseMail = new DatagramSocket();
										refuseMail.send(data);
										refuseMail.close();
										continue;
									}
									//TODO 
									
									
									receivePort = myPost+1; //监督的端口
									//开启socketServer接收
									System.out.println("服务器接收到大接收文件请求，服务器准备开启！");
									//ServerSocket server = Helper.getUsefulServer(receivePort);
									ServerSocket server = new ServerSocket(myPost);
									System.out.println("接收到服务器开启！");
									//返回接收消息
									String msgStr = myPost + "#";				
									msgStr = msgStr + "#!!!Y***&";
									byte refuseMsg[] = msgStr.getBytes();
									
									InetAddress address = null;
									address = InetAddress.getByName(TouchIp);
										
									DatagramPacket data = new DatagramPacket(refuseMsg,refuseMsg.length,address,toPort);
									DatagramSocket refuseMail = new DatagramSocket();
									refuseMail.send(data);
									refuseMail.close();
									
									Socket s = server.accept();
									
									DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
									int size = 8192;
									byte[] b = new byte[size];
									
									String fileName = dis.readUTF();
									System.out.println("接收文件：" + fileName);
									//[start] 自定义文件名称
									String fileN = null;
									fileN = JOptionPane.showInputDialog(null, "原文件名：" + Helper.getNameWithOutExtension(new File(fileName).getName()) + "\n请输入新文件名！","重命名",JOptionPane.INFORMATION_MESSAGE);
									if(fileN == null){
										String _msgStr = myPost + "#";				
										_msgStr = _msgStr + "#!!!N***&";
										byte _refuseMsg[] = msgStr.getBytes();	
										
										InetAddress _address = null;
										_address = InetAddress.getByName(TouchIp);
											
										DatagramPacket _data = new DatagramPacket(_refuseMsg,_refuseMsg.length,_address,toPort);
										DatagramSocket _refuseMail = new DatagramSocket();
										_refuseMail.send(_data);
										_refuseMail.close();
										continue;
									}
									while(!Helper.isLegalName(new File(fileName),fileN,saveDir)){
										fileN = JOptionPane.showInputDialog(null, "原文件名：" + Helper.getNameWithOutExtension(new File(fileName).getName()) + "\n请输入新文件名！","重命名",JOptionPane.INFORMATION_MESSAGE);
									}
									String savePath = saveDir.getAbsolutePath() + "\\" + fileN + "." + Helper.getExtension(fileName);
									File saveFile = new File(savePath);
									
									System.out.println("自定义文件名：" + savePath);
									//[end]
									/*这个是系统自动生成新名字
									fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
									
									
									String savePath = saveDir.getAbsolutePath() + "\\" + fileN;
									//如果已存在其后添加 "_new"
									File saveFile = new File(savePath);
									while(saveFile.exists()){
										savePath = savePath.substring(0,savePath.lastIndexOf(".")) + 
												"_new" + savePath.substring(savePath.lastIndexOf("."));
										System.out.println("新路径：" + savePath);
										saveFile = new File(savePath);
									}
									*/
									System.out.println("开始接收文件\n");
									DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)));
									while(true){
										int read = 0;
										if(dis != null){
											read = dis.read(b);
										}else{
											break;
										}
										if(read == -1){
											break;
										}
										fileOut.write(b,0,read);
										fileOut.flush();
									}
									fileOut.close();
									s.close();
									server.close();
									System.out.println("接收文件完成\n");
									//接受完成
									JOptionPane.showMessageDialog(null, "文件接收完成！", "提示", JOptionPane.WARNING_MESSAGE);
						        }else{ 
						        	//返回拒绝接受
						        	String msgStr = myPost + "#";				
									msgStr = msgStr + "#!!!N***&";
									byte refuseMsg[] = msgStr.getBytes();
									
									InetAddress address = null;
									address = InetAddress.getByName(TouchIp);
										
									DatagramPacket data = new DatagramPacket(refuseMsg,refuseMsg.length,address,toPort);
									DatagramSocket refuseMail = new DatagramSocket();
									refuseMail.send(data);
									refuseMail.close();
									continue;
						        }
							}
							else{
								//接受普通消息
								out = new FileWriter(historyFile,true);
								writer = new BufferedWriter(out);
								Date nowTime = new Date();
								SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String info = ">> "+InetAddress.getByName(IpField.getText()).getHostName()+": "+matter.format(nowTime);
								chatText.append(info + "\n");
								writer.write(info + "\r\n");
								chatText.append(message + "\n\n");
								writer.write(message + "\r\n\n");
								writer.flush();
								out.close();
								chatText.setCaretPosition(chatText.getText().length());
							}
						}
						catch(Exception err){
							err.printStackTrace();
							JOptionPane.showMessageDialog(null, "消息接收出错！", "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
					if(writer != null){
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(mail != null){
						mail.close();
					}
				}
			}).start();
			
		}
	}
	
	
}
class MyButton extends JButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2069255685242251854L;

	public MyButton(String value){
		super(value);
		setBackground(new Color(162,205,90));  //TODO 改颜色
		//setBounds(0,0,150,20);
	 // setPreferredSize(new Dimension(100,20));
	   // this.setSize(20, 20);
	}
}
