package menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.glass.events.KeyEvent;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import frames.ManagerFrame;
import frames.ViewFrame;
import helper.Helper;
import main.ImageManager;
import pane.ImageLabel;
import pane.ShowAllPane;


enum ActionEnum {
	openI, openW, close, path,attribute, print , 
	cut, copy, paste, delete,rename,paint,
	big,middle,small,
	name,date,type,size,
	increase,decrease,
	blogs,update,about,
	peoper,anime,view,other,treasure,
	addToPeoper,addToAnime,addToView,addToOther,addToTreasure,
	look,repaint
}

public class ManagerMenu implements ActionListener {
	public static ActionEnum aEnum;
	JFrame frame;
	File editFile = null;
	ImageLabel editImgLabel = null;
	ShowAllPane editPane = null;
	Comparator<File> compType;
	
	public ManagerMenu(JFrame frame){
		this.frame = frame;
	}

	//用于添加图片上得右键事件
	public ManagerMenu(ImageLabel imgLabel){
		this.editImgLabel = imgLabel;
		this.editFile = editImgLabel.getFile();
	}
	
	//面板上的右键事件
	public ManagerMenu(){}
	
	// 创建菜单栏
	public JMenuBar createMenu() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		ButtonGroup group;
		JRadioButtonMenuItem rbMenuItem;
		menuBar = new JMenuBar();

		//[start]文件菜单
		menu = new JMenu("文件(F)");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		//[start]子菜单项：打开
		submenu = new JMenu("打开(N)");
		submenu.setMnemonic(KeyEvent.VK_N);
		menu.add(submenu);
		
		menuItem = new JMenuItem("新窗口(W)");
		menu.setMnemonic(KeyEvent.VK_W);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("openW");
		menuItem.addActionListener(this);
		submenu.add(menuItem);
		
		menuItem = new JMenuItem("新图像(I)");
		menu.setMnemonic(KeyEvent.VK_I);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("openI");
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		// 菜单项：关闭
		menuItem = new JMenuItem("关闭(C)");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setActionCommand("close");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// 菜单项：属性
		menuItem = new JMenuItem("属性(R)");
		menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("attribute");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		//菜单项：打开文件路径
		menuItem = new JMenuItem("打开文件路径(S)");
		menuItem.setActionCommand("path");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		// 菜单项：打印
		menuItem = new JMenuItem("打印(P)");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("print");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		//[end]
		//[end]
		
		//[start]编辑菜单
		menu = new JMenu("编辑(E)");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);
		
		//[start]编辑菜单项
		menuItem = new JMenuItem("剪切(T)");
		menuItem.setActionCommand("cut");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("复制(C)");
		menuItem.setActionCommand("copy");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("粘贴(P)");
		menuItem.setActionCommand("paste");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("删除(D)");
		menuItem.setActionCommand("delete");
		menuItem.setMnemonic(KeyEvent.VK_D);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);	
		
		menuItem = new JMenuItem("重命名(M)");
		menuItem.setActionCommand("rename");
		menuItem.setMnemonic(KeyEvent.VK_M);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("涂改(R)");
		menuItem.setActionCommand("paint");
		menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		//[end]编辑菜单项		
		//[end]编辑菜单

		//[start]查看菜单
		menu = new JMenu("查看(V)");
		menu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(menu);
		
		//查看菜单项
		//[start]子菜单项：显示方式
		submenu = new JMenu("显示方式(S)");
		submenu.setActionCommand("showType");
		menu.add(submenu);
		
		group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("大图标(B)");
		rbMenuItem.setActionCommand("big");
		rbMenuItem.setMnemonic(KeyEvent.VK_B);
		rbMenuItem.addActionListener(this);
		if(ImageManager.showType.equals("big")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("中图标(M)");
		rbMenuItem.setActionCommand("middle");
		rbMenuItem.setMnemonic(KeyEvent.VK_M);
		rbMenuItem.addActionListener(this);
		if(ImageManager.showType.equals("middle")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("小图标(N)");
		rbMenuItem.setActionCommand("small");
		rbMenuItem.setMnemonic(KeyEvent.VK_N);
		rbMenuItem.addActionListener(this);
		if(ImageManager.showType.equals("small")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);	
		
//		rbMenuItem = new JRadioButtonMenuItem("列表(L)");
//		rbMenuItem.setActionCommand("list");
//		rbMenuItem.setMnemonic(KeyEvent.VK_L);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
		//[end]显示方式
		
		//[start]子菜单：排列依据
		submenu = new JMenu("排列依据(O)");
		submenu.setActionCommand("sort");
		menu.add(submenu);
		
		group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("名称(N)");
		rbMenuItem.setActionCommand("name");
		rbMenuItem.setMnemonic(KeyEvent.VK_N);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("name")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("修改日期(D)");
		rbMenuItem.setActionCommand("date");
		rbMenuItem.setMnemonic(KeyEvent.VK_D);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("date")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("类型(T)");
		rbMenuItem.setActionCommand("type");
		rbMenuItem.setMnemonic(KeyEvent.VK_T);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("type")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("大小(S)");
		rbMenuItem.setActionCommand("size");
		rbMenuItem.setMnemonic(KeyEvent.VK_S);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("size")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		submenu.addSeparator();
		
		group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("递增(I)");
		rbMenuItem.setActionCommand("increase");
		rbMenuItem.setMnemonic(KeyEvent.VK_I);
		rbMenuItem.addActionListener(this);
		if(ImageManager.increase){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("递减(L)");
		rbMenuItem.setActionCommand("decrease");
		rbMenuItem.setMnemonic(KeyEvent.VK_L);
		rbMenuItem.addActionListener(this);
		if(!ImageManager.increase){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		//[end]排列依据
		//[end]查看
		
		//[start]菜单：分组
		menu = new JMenu("分组(G)");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("人物(P)");
		menuItem.setActionCommand("peoper");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("景色(V)");
		menuItem.setActionCommand("view");
		menuItem.setMnemonic(KeyEvent.VK_V);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("动漫(A)");
		menuItem.setActionCommand("anime");
		menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("其他(O)");
		menuItem.setActionCommand("other");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();
		
		menuItem = new JMenuItem("珍藏(T)");
		menuItem.setActionCommand("treasure");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);		
		//[end]分组
		
		//[start] 菜单：帮助
		menu = new JMenu("帮助(H)");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("作者博客");
		menuItem.setActionCommand("blogs");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("版本更新");
		menuItem.setActionCommand("update");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("关于");
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		//[end]帮助
		
		return menuBar;
	}
	

	public void PopupMenuForImg(Component comp){
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
		JMenu menu;
		
		menuItem = new JMenuItem("查看(V)");
		menuItem.setActionCommand("look");
		menuItem.setMnemonic(KeyEvent.VK_V);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		//[start]分组
		menu = new JMenu("加到分组(G)");
		menu.setMnemonic(KeyEvent.VK_G);
		popup.add(menu);
		
		menuItem = new JMenuItem("人物(P)");
		menuItem.setActionCommand("peoper");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("景色(V)");
		menuItem.setActionCommand("view");
		menuItem.setMnemonic(KeyEvent.VK_V);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("动漫(A)");
		menuItem.setActionCommand("anime");
		menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("其他(O)");
		menuItem.setActionCommand("other");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		

		menu.addSeparator();
		
		menuItem = new JMenuItem("珍藏(T)");
		menuItem.setActionCommand("treasure");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);		
		
		//[end]分组
		
		menuItem = new JMenuItem("打印(P)");
		menuItem.setActionCommand("print");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		popup.addSeparator();
		
		menuItem = new JMenuItem("剪切(X)");
		menuItem.setActionCommand("cut");
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("复制(C)");
		menuItem.setActionCommand("copy");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		popup.addSeparator();
		
		menuItem = new JMenuItem("删除(D)");
		menuItem.setActionCommand("delete");
		menuItem.setMnemonic(KeyEvent.VK_D);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("重命名(M)");
		menuItem.setActionCommand("rename");
		menuItem.setMnemonic(KeyEvent.VK_M);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		popup.addSeparator();
		
		menuItem = new JMenuItem("打开文件路径(S)");
		menuItem.setActionCommand("path");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("属性(R)");
		menuItem.setActionCommand("attribute");
		menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		//创建监听器并为组件添加监听器
		MouseListener popupListener = new PopupListener(popup);
		comp.addMouseListener(popupListener);
		
	}
	//[end]弹出菜单
	
	//[start]面板弹出菜单
	public void PopupMenuForPane(Component comp){
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
		JMenu submenu;
		JRadioButtonMenuItem rbMenuItem;
		ButtonGroup group;
		
		//[start]子菜单项：显示方式
		submenu = new JMenu("显示方式(S)");
		submenu.setActionCommand("showType");
		popup.add(submenu);
		
		group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("大图标(B)");
		rbMenuItem.setActionCommand("big");
		rbMenuItem.setMnemonic(KeyEvent.VK_B);
		rbMenuItem.addActionListener(this);
		if(ImageManager.showType.equals("big")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("中图标(M)");
		rbMenuItem.setActionCommand("middle");
		rbMenuItem.setMnemonic(KeyEvent.VK_M);
		rbMenuItem.addActionListener(this);
		if(ImageManager.showType.equals("middle")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("小图标(N)");
		rbMenuItem.setActionCommand("small");
		rbMenuItem.setMnemonic(KeyEvent.VK_N);
		if(ImageManager.showType.equals("small")){
			rbMenuItem.setSelected(true);
		}
		rbMenuItem.addActionListener(this);
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);	
	
		//[end]显示方式
		
		//[start]子菜单：排列依据
		submenu = new JMenu("排列依据(O)");
		submenu.setActionCommand("sort");
		popup.add(submenu);
		
		group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("名称(N)");
		rbMenuItem.setActionCommand("name");
		rbMenuItem.setMnemonic(KeyEvent.VK_N);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("name")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("修改日期(D)");
		rbMenuItem.setActionCommand("date");
		rbMenuItem.setMnemonic(KeyEvent.VK_D);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("date")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("类型(T)");
		rbMenuItem.setActionCommand("type");
		rbMenuItem.setMnemonic(KeyEvent.VK_T);
		rbMenuItem.addActionListener(this);
		if(ImageManager.sortCmd.equals("type")){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		submenu.addSeparator();
		
		rbMenuItem = new JRadioButtonMenuItem("递增(I)");
		rbMenuItem.setActionCommand("increase");
		rbMenuItem.setMnemonic(KeyEvent.VK_I);
		rbMenuItem.addActionListener(this);
		if(ImageManager.increase){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("递减(L)");
		rbMenuItem.setActionCommand("decrease");
		rbMenuItem.setMnemonic(KeyEvent.VK_L);
		rbMenuItem.addActionListener(this);
		if(!ImageManager.increase){
			rbMenuItem.setSelected(true);
		}
		group.add(rbMenuItem);
		submenu.add(rbMenuItem);
		//[end]排列依据

		popup.addSeparator();
		
		menuItem = new JMenuItem("粘贴(P)");
		menuItem.setActionCommand("paste");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("刷新(E)");
		menuItem.setActionCommand("repaint");
		menuItem.setMnemonic(KeyEvent.VK_E);
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		//创建监听器并为组件添加监听器
		MouseListener popupListener = new PopupListenerOfPane(popup);
		comp.addMouseListener(popupListener);
		
	}
	//[end]面板弹出菜单
	
	//图片右键监听器
	class PopupListener extends MouseAdapter{
		JPopupMenu popup;
		
		public PopupListener(JPopupMenu popupMenu){
			popup = popupMenu;
			
		}
		
		private void maybeShowPopup(MouseEvent e){
			if(e.isPopupTrigger()){ //右键
				System.out.println("右键");
				if(editImgLabel != null){
					editImgLabel.setBackground(new Color(193,230,249));
					editImgLabel.setBorder(BorderFactory.createLineBorder(new Color(163,230,249), 1));
					editImgLabel.setOpaque(true); // 这里是必须的
					
					if(!ImageManager.renameFile.isEmpty() && ImageManager.renameFile.containsKey(editFile)) //已被重命名，换为姓名字
						editFile = ImageManager.renameFile.get(editFile);
					
					ImageManager.choosedImgFile = editFile;
					ImageManager.statusbar.setText("  "+ImageManager.choosedImgFile.getAbsolutePath());
					if(ImageManager.choosedImg != null && ImageManager.choosedImg != editImgLabel){
						ImageManager.choosedImg.setOpaque(false); // 这里是必须的
						ImageManager.choosedImg.setBorder(null);
					}
					ImageManager.choosedImg = editImgLabel;					
				}
				popup.show(e.getComponent(), e.getX(), e.getY());				
			}
		}
		

		@Override
		public void mouseClicked(MouseEvent e) {
//			System.out.println("click");
			editImgLabel.setBackground(new Color(193,230,249));
			editImgLabel.setBorder(BorderFactory.createLineBorder(new Color(163,230,249), 1));
			editImgLabel.setOpaque(true); // 这里是必须的
			if(!ImageManager.renameFile.isEmpty() && ImageManager.renameFile.containsKey(editFile)) //已被重命名，换为姓名字
				editFile = ImageManager.renameFile.get(editFile);
			
			ImageManager.choosedImgFile = editFile;
			ImageManager.statusbar.setText("  "+ImageManager.choosedImgFile.getAbsolutePath());
			if(ImageManager.choosedImg != null && ImageManager.choosedImg != editImgLabel){
				ImageManager.choosedImg.setOpaque(false); // 这里是必须的
				ImageManager.choosedImg.setBorder(null);
			}
			ImageManager.choosedImg = editImgLabel;
			if(e.getClickCount() >= 2){
				javax.swing.SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run(){
						ImageManager.createViewFrame(editFile);
					}
				});
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(ImageManager.choosedImg == editImgLabel)
				return;
			editImgLabel.setBackground(new Color(215,230,249));
			editImgLabel.setOpaque(true); 
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(ImageManager.choosedImg == editImgLabel)
				return;
			editImgLabel.setBackground(null);
			editImgLabel.setOpaque(false); 
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}

	//面板右键监听器
	class PopupListenerOfPane extends MouseAdapter{
		JPopupMenu popup;
		
		public PopupListenerOfPane(JPopupMenu popupMenu){
			popup = popupMenu;
			
		}
		
		 public void mousePress(MouseEvent e){
			 if(ImageManager.choosedImg != null){
				 ImageManager.choosedImg.setOpaque(false);
				 ImageManager.choosedImg.repaint();
			 }
			 maybeShowPopup(e);
		 }
		
		public void mouseReleased(MouseEvent e){
			if(ImageManager.choosedImg != null){
				 ImageManager.choosedImg.setOpaque(false);
				 ImageManager.choosedImg.repaint();
			 }
			maybeShowPopup(e);
		}
		
		private void maybeShowPopup(MouseEvent e){
			//editFile = ImageManager.choosedImgFile;
			
			if(e.isPopupTrigger()){ //右键
				popup.show(e.getComponent(), e.getX(), e.getY());				
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File chooseFile = ImageManager.choosedImgFile;
		ImageLabel choosedImg = ImageManager.choosedImg;
		String cmd = e.getActionCommand();
		editPane = ImageManager.curShowAllPane;
		System.out.println(ImageManager.choosedImgFile);
		ActionEnum ce = ActionEnum.valueOf(cmd);
		
		switch(ce){
		case openI:{
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter;
			filter = new FileNameExtensionFilter("JPEG(*.jpg;*.jpeg;*.jfif)","jpg","jpeg","jfif");
			fc.addChoosableFileFilter(filter);
			//filter = new FileNameExtensionFilter("GIF(*.gif)","gif");	//暂时不支持
			//fc.addChoosableFileFilter(filter);
			filter = new FileNameExtensionFilter("TIFF(*.tif;*.tif)","tif","tif");
			fc.addChoosableFileFilter(filter);
			filter = new FileNameExtensionFilter("PNG(*.png)","png");
			fc.addChoosableFileFilter(filter);
			filter = new FileNameExtensionFilter("OTHERS(*.bmp;*.dib)","bmp","dib");
			fc.addChoosableFileFilter(filter);
			
			
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {			
				javax.swing.SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run(){
						//优化为独立的Thread线程
						ImageManager.createViewFrame(fc.getSelectedFile());
					}
				});
			}
			break;
		}
		case openW:{
//			javax.swing.SwingUtilities.invokeLater(new Runnable(){
//				@Override
//				public void run(){
//					//优化为独立的Thread线程
//					ImageManager.createAndShowGUI();
//				}
//			});
			Thread newFrame = new Thread(new Runnable(){
				@Override
				public void run() {
					ImageManager.createAndShowGUI();
				}	
			});
			newFrame.start();
			break;
		}
		case close:{
			frame.dispose();
			break;
		}
		case attribute:{	
			if(ImageManager.choosedImgFile == null)
				return;
			if(!ImageManager.curNodePath.equals(new File(Helper.getLocation(chooseFile.getAbsolutePath()))))
				return;
			new ImgAttributesFrame().createAttFrame();
			break;
		}
		
		case path:{
			File curFile = ImageManager.choosedImgFile;
			if(curFile == null)
				return;
			String path = Helper.getLocation(curFile.getAbsolutePath());
			Desktop desktop = Desktop.getDesktop();
			if(!desktop.isSupported(Desktop.Action.OPEN))
				JOptionPane.showMessageDialog(frame, "抱歉！文件路径打开出错");
			try {
				desktop.open(new File(path));
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "抱歉！文件路径打开出错");
				e1.printStackTrace();
			}
			break;
		}
		case print:{
			Desktop desktop = Desktop.getDesktop();
			if(!desktop.isSupported(Desktop.Action.PRINT)){
				JOptionPane.showMessageDialog(frame, "请检查本机是否已连接打印机");
			}
			//未完成
			try {
				desktop.print(ImageManager.choosedImgFile);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "请检查本机是否已连接打印机");
			}
			//System.out.println(desktop.isSupported(Desktop.Action.PRINT));
			break;
		}
		case cut:{
			editFile = chooseFile;
			editImgLabel = choosedImg;
			ImageManager.needDeleted = true;
			editPane = ImageManager.curShowAllPane;
			break;
		}
		case copy:{
			editFile = chooseFile;
			editImgLabel = choosedImg;
			ImageManager.needDeleted = false;
			break;
		}
		case paste:{
			File targetDir = ImageManager.curNodePath;
			if(editFile == null || targetDir == null)
				return;
			if(!editFile.exists()){
				JOptionPane.showMessageDialog(null, "粘贴文件："+editFile.getName()+" 已不存在","警告",javax.swing.JOptionPane.WARNING_MESSAGE);	
				return;
			}			
			if(editFile == null || targetDir == null )
				break;
			String newFileName;
			if(ImageManager.needDeleted){
//				System.out.println((targetDir.getAbsoluteFile()).equals(Helper.getLocation(editFile.getAbsolutePath())));
				if( targetDir.equals(new File(Helper.getLocation(editFile.getAbsolutePath()))) ){			
					JOptionPane.showMessageDialog(null, "目标目录与文件所在目录相同","跳过",javax.swing.JOptionPane.WARNING_MESSAGE);	
					return;
				}		
				newFileName = Helper.getNameWithOutExtension(editFile.getName());	
				newFileName = Helper.pasteImg(editFile,newFileName,targetDir);
				if(newFileName == null )
					return;
				ImageIcon img=new ImageIcon(newFileName);
				int imgWidth = img.getIconWidth();
				int imgHeight = img.getIconHeight();
				
				double changeRate = 1.0*ImageManager.curImgWidth /imgWidth < 1.0*ImageManager.curImgHeight /imgHeight ? 
						1.0*ImageManager.curImgWidth /imgWidth : 1.0*ImageManager.curImgHeight /imgHeight ;
				int imgChangedWidth = (int)(imgWidth * changeRate);
				int imgChangedHeight = (int)(1.0*imgChangedWidth / imgWidth * imgHeight);
				img.setImage(img.getImage().getScaledInstance(imgChangedWidth,imgChangedHeight,Image.SCALE_SMOOTH));
				File file = new File(newFileName);
				ImageLabel imgLabel = new ImageLabel(img,file);
				ManagerMenu mmenu = new ManagerMenu(imgLabel);
				mmenu.PopupMenuForImg(imgLabel);
//				mmenu.PopupMenuForImg(imgLabel);
				imgLabel.repaint();
				imgLabel.validate();
				ImageManager.curShowAllPane.add(imgLabel);
				ImageManager.curShowAllPane.repaint();			
				ImageManager.curShowAllPane.validate();
				
				editFile.delete();
				ImageManager.imagesList.remove(editFile);
//				editPane.remove(editImgLabel);
//				editPane.repaint();
//				editPane.validate();				
			}
			//获取新图像名字		
			else{
				newFileName = JOptionPane.showInputDialog(frame,"请输入新文件名称：",Helper.getNameWithOutExtension(editFile.getName()));		
				if(newFileName == null)
					return;
				while(!Helper.isLegalName(editFile,newFileName,targetDir)){
					if(newFileName == null)
						return;
					newFileName = JOptionPane.showInputDialog(frame,"请输入新文件名称：",Helper.getNameWithOutExtension(editFile.getName()));
				}
				newFileName = Helper.pasteImg(editFile,newFileName,targetDir);
				if(newFileName == null)
					return;
				ImageIcon img=new ImageIcon(newFileName);
				int imgWidth = img.getIconWidth();
				int imgHeight = img.getIconHeight();
				
				double changeRate = 1.0*ImageManager.curImgWidth /imgWidth < 1.0*ImageManager.curImgHeight /imgHeight ? 
						1.0*ImageManager.curImgWidth /imgWidth : 1.0*ImageManager.curImgHeight /imgHeight ;
				int imgChangedWidth = (int)(imgWidth * changeRate);
				int imgChangedHeight = (int)(1.0*imgChangedWidth / imgWidth * imgHeight);
				img.setImage(img.getImage().getScaledInstance(imgChangedWidth,imgChangedHeight,Image.SCALE_SMOOTH));
				File file = new File(newFileName);
				ImageLabel imgLabel = new ImageLabel(img,file);
				ManagerMenu mmenu = new ManagerMenu(imgLabel);
				mmenu.PopupMenuForImg(imgLabel);
				imgLabel.repaint();
				imgLabel.validate();
				ImageManager.curShowAllPane.add(imgLabel);
				ImageManager.curShowAllPane.repaint();			
				ImageManager.curShowAllPane.validate();
				ImageManager.imagesList.add(file);
			}			
			break;
		}
		case delete:{
			if(chooseFile == null)
				return;
			chooseFile.delete();
		
			ImageManager.curShowAllPane.remove(choosedImg);
			ImageManager.curShowAllPane.repaint();
			ImageManager.curShowAllPane.validate();
			
			ImageManager.imagesList.remove(chooseFile);
			ImageManager.choosedImgFile = null;
			ImageManager.choosedImg = null;
			break;
		}
		case rename:{
			editFile = ImageManager.choosedImgFile ;
			
			if(!ImageManager.renameFile.isEmpty() && ImageManager.renameFile.containsKey(editFile)) //已被重命名，换为姓名字
				editFile = ImageManager.renameFile.get(editFile);
			if(editFile == null)
				return;
			String newFileName;
			
			newFileName = JOptionPane.showInputDialog(frame,"请输入新文件名称：",Helper.getNameWithOutExtension(editFile.getName()));		
			if(newFileName == null || newFileName.equals(Helper.getNameWithOutExtension(editFile.getName())))
				return;
			while(!Helper.isLegalName(editFile,newFileName,ImageManager.curNodePath)){
				newFileName = JOptionPane.showInputDialog(frame,"请输入新文件名称：",Helper.getNameWithOutExtension(editFile.getName()));
				if(newFileName == null || newFileName.equals(Helper.getNameWithOutExtension(editFile.getName())))
					return;
			}
			ImageManager.imagesList.remove(chooseFile);
			
			File newFile = Helper.rename(chooseFile,newFileName);
			
			ImageManager.renameFile.put(editFile, newFile);
			editFile = newFile;
			
			ImageManager.choosedImgFile = editFile;
			ImageManager.imagesList.add(ImageManager.choosedImgFile);
			
			ImageManager.choosedImg.setImgFile(ImageManager.choosedImgFile);
			break;
		}
		case paint:{
			if(chooseFile == null)
				return;
			if(ImageManager.tipInPaint != 0) //0表示YES，不等于0,，则提示（YES=0,NO=1,关闭 = -1）
				ImageManager.tipInPaint = JOptionPane.showConfirmDialog(frame, "涂改结果需刷新后才能查看！\n"+"我知道了，不再提示。","提示",JOptionPane.YES_NO_OPTION);
			System.out.println(ImageManager.tipInPaint);
			Runtime runtime=Runtime.getRuntime();  
			String[] commandArgs={"mspaint.exe",chooseFile.getAbsolutePath()};  
			try {
				runtime.exec(commandArgs);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "无法正常打开绘图应用");
				e1.printStackTrace();
			}
			break;
		}
		case big:{
			if(ImageManager.curImgWidth == ImageManager.BIGLABEL_WIDTH)
				return;
			ImageManager.curImgWidth = ImageManager.BIGLABEL_WIDTH;
			ImageManager.curImgHeight = ImageManager.BIGLABEL_HEIGHT;
			new RepaintPane().execute();
			break;
		}
		case middle:{
			if(ImageManager.curImgWidth == ImageManager.MIDDLELABEL_WIDTH)
				return;
			ImageManager.curImgWidth = ImageManager.MIDDLELABEL_WIDTH;
			ImageManager.curImgHeight = ImageManager.MIDDLELABEL_HEIGHT;
			new RepaintPane().execute();
			break;
		}
		case small:{
			if(ImageManager.curImgWidth == ImageManager.SMALLLABEL_WIDTH)
				return;
			ImageManager.curImgWidth = ImageManager.SMALLLABEL_WIDTH;
			ImageManager.curImgHeight = ImageManager.SMALLLABEL_HEIGHT;
			new RepaintPane().execute();
			break;
		}
		case name:{
			if(ImageManager.curSortType.equals("name"))
				return;
			ImageManager.curSortType = "name";
			if(ImageManager.increase)
				ImageManager.imagesList.sort(new FileComparator_Name());
			else
				ImageManager.imagesList.sort(new FileComparator_ReName());
			
			new RepaintPane().execute();
			
			break;
		}
		case date:{
			if(ImageManager.curSortType.equals("date"))
				return;
			ImageManager.curSortType = "date";
			if(ImageManager.increase)
				ImageManager.imagesList.sort(new FileComparator_Date());
			else
				ImageManager.imagesList.sort(new FileComparator_ReDate());
			
			new RepaintPane().execute();
			break;
		}
		case type:{
			if(ImageManager.curSortType.equals("type"))
				return;
			ImageManager.curSortType = "type";
			if(ImageManager.increase)
				ImageManager.imagesList.sort(new FileComparator_Extension());
			else
				ImageManager.imagesList.sort(new FileComparator_ReExtension());
			
			new RepaintPane().execute();
			break;
		}		
		case size:{
			if(ImageManager.curSortType.equals("size"))
				return;
			ImageManager.curSortType = "size";
			if(ImageManager.increase)
				ImageManager.imagesList.sort(new FileComparator_Size());
			else
				ImageManager.imagesList.sort(new FileComparator_ReSize());
			
			new RepaintPane().execute();
		}
		case increase:{
			if(ImageManager.increase)
				return;
			ImageManager.increase = true;
			switch(ImageManager.curSortType){
			case "name":{
				ImageManager.imagesList.sort(new FileComparator_Name());
				break;
			}
			case "date":{
				ImageManager.imagesList.sort(new FileComparator_Date());
				break;
			}
			case "type":{
				ImageManager.imagesList.sort(new FileComparator_Size());
				break;
			}
			default:{
				JOptionPane.showMessageDialog(frame, "排序出错","错误",JOptionPane.WARNING_MESSAGE);
				break;
			}
			}
			new RepaintPane().execute();
			break;
		}
		case decrease:{
			if(!ImageManager.increase)
				return;
			ImageManager.increase = false;
			switch(ImageManager.curSortType){
			case "name":{
				ImageManager.imagesList.sort(new FileComparator_ReName());
				break;
			}
			case "date":{
				ImageManager.imagesList.sort(new FileComparator_ReDate());
				break;
			}
			case "type":{
				ImageManager.imagesList.sort(new FileComparator_ReSize());
				break;
			}
			default:{
				JOptionPane.showMessageDialog(frame, "排序出错","错误",JOptionPane.WARNING_MESSAGE);
				break;
			}
			}
			new RepaintPane().execute();
			break;
		}
		case blogs:{
			Desktop desktop = Desktop.getDesktop();
			if(!desktop.isSupported(Desktop.Action.BROWSE)){
				JOptionPane.showMessageDialog(frame, "无法正常打开浏览器\n"+"作者博客："+"http://blog.csdn.net/donespeak");
			}
			try {
				desktop.browse(new URI("http://blog.csdn.net/donespeak"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1){
				e1.printStackTrace();
			} 
			break;
		}
		case update:{
			JOptionPane.showMessageDialog(frame, "本版本就是最牛逼的最新版本1.0\n"+"版本更新请留意作者博客","版本更新",
					javax.swing.JOptionPane.PLAIN_MESSAGE,new javax.swing.ImageIcon("image\\无脸男_updateOption.png"));
			break;
		}
		case about:{
			JOptionPane.showMessageDialog(frame, "人生若只如初见，\n"+"何事秋风悲画扇。\n\n"
		+"名称：  初见\n"
		+"版本 :    1.0\n"
		+"作者：  DoneSpeak\n"
		+"开始时间：2015-11-20\n"
		+"结束时间：2015-11-29\n\n","关于",
		javax.swing.JOptionPane.PLAIN_MESSAGE,new javax.swing.ImageIcon("image\\aboutOption.png"));
			break;
		}
		case look:{
			javax.swing.SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run(){
					ImageManager.createViewFrame(ImageManager.choosedImgFile);
				}
			});
			break;
		}
		case repaint:{
			new RepaintPane().execute();
		}
		default:{
			break;
		}
		}//switch
	}

//图像信息窗体
class ImgAttributesFrame extends JFrame{
	Box baseBox,boxAttribute,boxInfo;
	JPanel buttonPane;
	JPanel infoPane;
	JButton sureBt,cancelBt;
	JTextField info;
	JTextField nameField;
	ImageLabel imgLabel;
	File curFile;
	JFrame frame;
	ImageIcon img;
	ImageLabel curImgLabel;
	int infoWidth  = 18;
	int Strut = 10;
	public DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	ImgAttributesFrame(){
		setTitle("图像信息");
		curFile = ImageManager.choosedImgFile;
		frame = this;
		img=new ImageIcon(curFile.getAbsolutePath());
		imgLabel = ImageManager.choosedImg;
		curImgLabel = imgLabel;
	}
	
	public void createAttFrame(){	
		
		//图像属性
		boxAttribute = Box.createVerticalBox();
		boxAttribute.add(new JLabel("名称："));
		boxAttribute.add(Box.createVerticalStrut(Strut));
		boxAttribute.add(new JLabel("格式："));
		boxAttribute.add(Box.createVerticalStrut(Strut));
		boxAttribute.add(new JLabel("图片尺寸："));
		boxAttribute.add(Box.createVerticalStrut(Strut));
		boxAttribute.add(new JLabel("位置："));
		boxAttribute.add(Box.createVerticalStrut(Strut));
		boxAttribute.add(new JLabel("文件大小："));
		boxAttribute.add(Box.createVerticalStrut(Strut));
		boxAttribute.add(new JLabel("创建时间："));
		
		//图像信息
		boxInfo = Box.createVerticalBox();
		
		//名称
		nameField = new JTextField(infoWidth);
		nameField.setText(Helper.getNameWithOutExtension( curFile.getName()));
		nameField.addKeyListener(new EnterKeyListener(curFile,nameField,imgLabel));
		boxInfo.add(nameField);
		boxInfo.add(Box.createVerticalStrut(Strut));
		//格式
		info = new JTextField(infoWidth);
		info.setText(Helper.getExtension(curFile.getName().toUpperCase()));
		info.setEditable(false);
		info.setBorder(null);
		boxInfo.add(info);
		boxInfo.add(Box.createVerticalStrut(Strut));
		//尺寸
		info = new JTextField(infoWidth);
		info.setText(img.getIconWidth()+"x"+img.getIconHeight()+"像素(宽x高)");
		info.setEditable(false);
		info.setBorder(null);
		boxInfo.add(info);
		boxInfo.add(Box.createVerticalStrut(Strut));
		//位置
		info = new JTextField(infoWidth);
		info.setText(Helper.getLocation(curFile.getAbsolutePath()));
		boxInfo.add(info);
		boxInfo.add(Box.createVerticalStrut(Strut));
		//大小
		info = new JTextField(infoWidth);
		info.setText(""+curFile.length());
		info.setEditable(false);
		info.setBorder(null);
		boxInfo.add(info);
		boxInfo.add(Box.createVerticalStrut(Strut));
		//时间
		info = new JTextField(infoWidth);
		BasicFileAttributes bfa = null;
		try {
			bfa = Files.readAttributes(Paths.get(curFile.getAbsolutePath()), DosFileAttributes.class);		
			info.setText(""+df.format(new Date(bfa.creationTime().toMillis())));
		} catch (IOException e) {
			System.out.println("获取图像创建时间出错");
			info.setText("unknown");
		}		
		info.setEditable(false);
		info.setBorder(null);
		boxInfo.add(info);
		//创建baseBox
		baseBox = Box.createHorizontalBox();
		
		baseBox.add(boxAttribute);
		baseBox.add(Box.createHorizontalStrut(10));
		baseBox.add(boxInfo);
		
		infoPane = new JPanel();
		//infoPane.setLayout(new FlowLayout());
		infoPane.add(baseBox);
		
		setLayout(new BorderLayout());
		add(new JPanel(),BorderLayout.NORTH);
		add(infoPane,BorderLayout.CENTER);
		//按键
		sureBt = new JButton("确定");
		sureBt.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				File temp = Helper.rename(curFile,nameField);
				//重命名成功
				if(temp != null){
					if(ImageManager.choosedImgFile == curFile){
						ImageManager.choosedImgFile = temp;
						ImageManager.choosedImg.setImgFile(ImageManager.choosedImgFile);
					}
					ImageManager.renameFile.put(editFile, temp);
					ImageManager.imagesList.remove(editFile);
					ImageManager.imagesList.add(temp);
					editFile = temp;
					
					curImgLabel.setImgFile(temp);
					frame.dispose();
				}
			}
		});
		cancelBt = new JButton("取消");
		cancelBt.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		buttonPane = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		buttonPane.setLayout(flow);
		buttonPane.add(sureBt);
		buttonPane.add(cancelBt);
		buttonPane.setBorder(new MatteBorder(1, 0, 0, 0, new Color(202,202,202)));
		add(buttonPane,BorderLayout.SOUTH);
		
		validate();
		setBounds(500,500,353,462);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		System.out.println("end");
	}	
	
	class EnterKeyListener implements KeyListener{
		File oldfile;
		JTextField nameField;
		public EnterKeyListener(File oldfile,JTextField nameField,ImageLabel imgLabel){
			this.oldfile = oldfile;
			this.nameField = nameField;
		}
		@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				File temp = Helper.rename(curFile,nameField);
				//重命名成功
				if(temp != null){
					if(ImageManager.choosedImgFile == curFile){
						
						ImageManager.choosedImgFile = temp;
						ImageManager.choosedImg.setImgFile(ImageManager.choosedImgFile);						
					}
					ImageManager.renameFile.put(editFile, temp);
					ImageManager.imagesList.remove(editFile);
					ImageManager.imagesList.add(temp);
					
					editFile = temp;
					curImgLabel.setImgFile(temp);
					frame.dispose();
				}
				//失败不做任何操作
			}
		}
		@Override
		public void keyReleased(java.awt.event.KeyEvent e) {}
		@Override
		public void keyTyped(java.awt.event.KeyEvent e) {}		
	}
}
}

class FileComparator_Name implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if(f1.getName().equals(f2.getName()))
			return 1;
		return f1.compareTo(f2);
	}
}

class FileComparator_ReName implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if(f1.getName().equals(f2.getName()))
			return 1;
		return f2.compareTo(f1);
	}
}

class FileComparator_Extension implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		String extensionOff1 = Helper.getExtension(f1.getName());
		String extensionOff2 = Helper.getExtension(f2.getName());
		return extensionOff1.compareTo(extensionOff2);
	}
}

class FileComparator_ReExtension implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		String extensionOff1 = Helper.getExtension(f1.getName());
		String extensionOff2 = Helper.getExtension(f2.getName());
		return extensionOff2.compareTo(extensionOff1);
	}
}

class FileComparator_Date implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.lastModified() - f2.lastModified() >= 0)
			return 1;
		else
			return -1;
	}
}

class FileComparator_ReDate implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.lastModified() - f2.lastModified() >= 0)
			return -1;
		else
			return 1;
	}
}

class FileComparator_Size implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.isDirectory() && f2.isFile()) {
			if (f2.length() > 0)
				return 1;
			else
				return -1;
		} else if (f1.isDirectory() && f2.isDirectory())
			return -1;
		else if (f1.length() - f2.length() > 0)
			return -1;
		else if (f1.length() - f2.length() < 0)
			return 1;
		return 1;
	}
}

class FileComparator_ReSize implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.isDirectory() && f2.isFile()) {
			if (f2.length() > 0)
				return -1;
			else
				return 1;
		} else if (f1.isDirectory() && f2.isDirectory())
			return 1;
		else if (f1.length() - f2.length() > 0)
			return 1;
		else if (f1.length() - f2.length() < 0)
			return -1;
		return 1;
	}
}






















