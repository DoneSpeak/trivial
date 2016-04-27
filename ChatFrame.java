import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	
	private String TouchIp = "localhost";
	private int myPost = 5080;
	private int toPort = 5020;
	
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
		boxH2.add(startBtn);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(sendBtn);
		boxH2.add(Box.createHorizontalStrut(7));
		boxH2.add(clearBtn);
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
			byte msg[] = msgField.getText().trim().getBytes();	
			try {
				InetAddress address = null;
				address = InetAddress.getByName(TouchIp);
				
				DatagramPacket data = new DatagramPacket(msg,msg.length,address,toPort);
				DatagramSocket mail = new DatagramSocket();
				mail.send(data);
				
				FileWriter out = new FileWriter(historyFile,true);
				BufferedWriter writer = new BufferedWriter(out);
				Date nowTime = new Date();
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String info = "<< "+"ME"+": "+matter.format(nowTime);
				chatText.append(info + "\n");
				writer.append(info + "\r\n");
				chatText.append(msgField.getText() + "\n\n");
				writer.append(msgField.getText() + "\r\n\n");
				writer.flush();
				chatText.setCaretPosition(chatText.getText().length());
				msgField.setText("");
				mail.close();
				
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
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			
			historyText.setText("");
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
			if(TouchIp.equals("") || TouchIp == null || TouchIp.length() == 0){
				JOptionPane.showMessageDialog(null, "请输入对方Ip！", "Warning", JOptionPane.WARNING_MESSAGE);
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
						err.printStackTrace();
						JOptionPane.showMessageDialog(null, "出错！将检查端口是否重复使用！", "Warning", JOptionPane.WARNING_MESSAGE);
						startSuccess = false;
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
							int port = pack.getPort();
							System.out.println("收到消息！");
							String message = new String(pack.getData(),0,pack.getLength());
							if(!host.equals(TouchIp)){
								JOptionPane.showMessageDialog(null, "地址："+host+":"+port+"\n"+message);
								continue;
							}
							
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
							chatText.setCaretPosition(chatText.getText().length());
							
						}
						catch(Exception err){
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
			if(startSuccess){
				IpField.setEditable(false);
				postField.setEditable(false);
				myPostField.setEditable(false);
				startBtn.setText("end");
				startOrEnd = START;
				hasStart = true;
			}
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
