//package menus;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.io.File;
//
//import javax.swing.BorderFactory;
//import javax.swing.ButtonGroup;
//import javax.swing.JMenu;
//import javax.swing.JMenuItem;
//import javax.swing.JPopupMenu;
//import javax.swing.JRadioButtonMenuItem;
//
//import com.sun.glass.events.KeyEvent;
//
//import pane.ImageLabel;
//import pane.ManagerPane;
//import pane.ShowAllPane;
//
////enum ActionEnum {
////	openI, openW, close, attribute, print , 
////	cut, copy, paste, delete,rename,paint,
////	big,middle,small,
////	name,date,type,size,
////	increase,decrease,
////	blogs,update,about,
////	peoper,anime,view,other,treasure,
////	view,path,
////}
//
//public class ViewMenu  implements ActionListener {
//	ImageLabel imgLabel;
//	ShowAllPane showAllPane;
//	File imgFile;
//
//	//用于添加图片上得右键事件
//	public ViewMenu(ImageLabel imgLabel,ShowAllPane showAllPane,File imgFile){
//		this.imgLabel = imgLabel;
//		this.showAllPane = showAllPane;
//		this.imgFile = imgFile;
//	}
//	
//	//面板上的右键事件
//	public ViewMenu(){}
//	//[start]图片弹出菜单
//	public void PopupMenuForImg(Component comp){
//		JPopupMenu popup = new JPopupMenu();
//		JMenuItem menuItem;
//		JMenu menu;
//		
//		menuItem = new JMenuItem("查看(V)");
//		menuItem.setActionCommand("view");
//		menuItem.setMnemonic(KeyEvent.VK_V);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		//[start]分组
//		menu = new JMenu("分组(G)");
//		menu.setMnemonic(KeyEvent.VK_G);
//		popup.add(menu);
//		
//		menuItem = new JMenuItem("人物(P)");
//		menuItem.setActionCommand("peoper");
//		menuItem.setMnemonic(KeyEvent.VK_P);
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
//		
//		menuItem = new JMenuItem("景色(V)");
//		menuItem.setActionCommand("view");
//		menuItem.setMnemonic(KeyEvent.VK_V);
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
//
//		menuItem = new JMenuItem("动漫(A)");
//		menuItem.setActionCommand("anime");
//		menuItem.setMnemonic(KeyEvent.VK_A);
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
//		
//		menuItem = new JMenuItem("其他(O)");
//		menuItem.setActionCommand("other");
//		menuItem.setMnemonic(KeyEvent.VK_O);
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
//		
//
//		menu.addSeparator();
//		
//		menuItem = new JMenuItem("珍藏(T)");
//		menuItem.setActionCommand("treasure");
//		menuItem.setMnemonic(KeyEvent.VK_T);
//		menuItem.addActionListener(this);
//		menu.add(menuItem);		
//		
//		//[end]分组
//		
//		menuItem = new JMenuItem("打印(P)");
//		menuItem.setActionCommand("print");
//		menuItem.setMnemonic(KeyEvent.VK_P);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		popup.addSeparator();
//		
//		menuItem = new JMenuItem("剪切(T)");
//		menuItem.setActionCommand("cut");
//		menuItem.setMnemonic(KeyEvent.VK_T);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		menuItem = new JMenuItem("复制(C)");
//		menuItem.setActionCommand("copy");
//		menuItem.setMnemonic(KeyEvent.VK_C);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		popup.addSeparator();
//		
//		menuItem = new JMenuItem("删除(D)");
//		menuItem.setActionCommand("delete");
//		menuItem.setMnemonic(KeyEvent.VK_D);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		menuItem = new JMenuItem("重命名(M)");
//		menuItem.setActionCommand("rename");
//		menuItem.setMnemonic(KeyEvent.VK_M);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		popup.addSeparator();
//		
//		menuItem = new JMenuItem("打开文件路径(S)");
//		menuItem.setActionCommand("path");
//		menuItem.setMnemonic(KeyEvent.VK_S);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		menuItem = new JMenuItem("属性(R)");
//		menuItem.setActionCommand("attribute");
//		menuItem.setMnemonic(KeyEvent.VK_R);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		//创建监听器并为组件添加监听器
//		MouseListener popupListener = new PopupListener(popup);
//		comp.addMouseListener(popupListener);
//		
//	}
//	//[end]弹出菜单
//	
//	//[start]面板弹出菜单
//	public void PopupMenuForPane(Component comp){
//		JPopupMenu popup = new JPopupMenu();
//		JMenuItem menuItem;
//		JMenu submenu;
//		JRadioButtonMenuItem rbMenuItem;
//		ButtonGroup group;
//		
//		//[start]子菜单项：显示方式
//		submenu = new JMenu("显示方式(S)");
//		submenu.setActionCommand("showType");
//		popup.add(submenu);
//		
//		group = new ButtonGroup();
//		
//		rbMenuItem = new JRadioButtonMenuItem("大图标(B)");
//		rbMenuItem.setActionCommand("big");
//		rbMenuItem.setMnemonic(KeyEvent.VK_B);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		rbMenuItem = new JRadioButtonMenuItem("中图标(M)");
//		rbMenuItem.setActionCommand("middle");
//		rbMenuItem.setMnemonic(KeyEvent.VK_M);
//		rbMenuItem.addActionListener(this);
//		rbMenuItem.setSelected(true);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		rbMenuItem = new JRadioButtonMenuItem("小图标(N)");
//		rbMenuItem.setActionCommand("small");
//		rbMenuItem.setMnemonic(KeyEvent.VK_N);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);	
//		
//		rbMenuItem = new JRadioButtonMenuItem("列表(L)");
//		rbMenuItem.setActionCommand("list");
//		rbMenuItem.setMnemonic(KeyEvent.VK_L);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		//[end]显示方式
//		
//		//[start]子菜单：排列依据
//		submenu = new JMenu("排列依据(O)");
//		submenu.setActionCommand("sort");
//		popup.add(submenu);
//		
//		group = new ButtonGroup();
//		
//		rbMenuItem = new JRadioButtonMenuItem("名称(N)");
//		rbMenuItem.setActionCommand("name");
//		rbMenuItem.setMnemonic(KeyEvent.VK_N);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		rbMenuItem = new JRadioButtonMenuItem("修改日期(D)");
//		rbMenuItem.setActionCommand("date");
//		rbMenuItem.setMnemonic(KeyEvent.VK_D);
//		rbMenuItem.addActionListener(this);
//		rbMenuItem.setSelected(true);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		rbMenuItem = new JRadioButtonMenuItem("类型(T)");
//		rbMenuItem.setActionCommand("type");
//		rbMenuItem.setMnemonic(KeyEvent.VK_T);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		submenu.addSeparator();
//		
//		rbMenuItem = new JRadioButtonMenuItem("递增(I)");
//		rbMenuItem.setActionCommand("increase");
//		rbMenuItem.setMnemonic(KeyEvent.VK_I);
//		rbMenuItem.addActionListener(this);
//		rbMenuItem.setSelected(true);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		
//		rbMenuItem = new JRadioButtonMenuItem("递减(L)");
//		rbMenuItem.setActionCommand("decrease");
//		rbMenuItem.setMnemonic(KeyEvent.VK_L);
//		rbMenuItem.addActionListener(this);
//		group.add(rbMenuItem);
//		submenu.add(rbMenuItem);
//		//[end]排列依据
//
//		popup.addSeparator();
//		
//		menuItem = new JMenuItem("粘贴(P)");
//		menuItem.setActionCommand("paste");
//		menuItem.setMnemonic(KeyEvent.VK_P);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		menuItem = new JMenuItem("刷新(E)");
//		menuItem.setActionCommand("repain");
//		menuItem.setMnemonic(KeyEvent.VK_E);
//		menuItem.addActionListener(this);
//		popup.add(menuItem);
//		
//		//创建监听器并为组件添加监听器
//		MouseListener popupListener = new PopupListenerOfPane(popup);
//		comp.addMouseListener(popupListener);
//		
//	}
//	//[end]面板弹出菜单
//	
//	//图片右键监听器
//	class PopupListener extends MouseAdapter{
//		JPopupMenu popup;
//		
//		public PopupListener(JPopupMenu popupMenu){
//			popup = popupMenu;
//			
//		}
//		
//		 public void mousePress(MouseEvent e){
//			 maybeShowPopup(e);
//		 }
//		
//		public void mouseReleased(MouseEvent e){
//			maybeShowPopup(e);
//		}
//		
//		private void maybeShowPopup(MouseEvent e){
//			if(e.isPopupTrigger()){ //右键
//				if(imgLabel != null){
//					imgLabel.setBackground(new Color(193,230,249));
//					imgLabel.setBorder(BorderFactory.createLineBorder(new Color(163,230,249), 1));
//					imgLabel.setOpaque(true); // 这里是必须的
//					ManagerPane.choosedImgFile = imgFile;
//					if(ManagerPane.choosedImg != null && ManagerPane.choosedImg != imgLabel){
//						ManagerPane.choosedImg.setOpaque(false); // 这里是必须的
//						ManagerPane.choosedImg.setBorder(null);
//						//ManagerPane.choosedImg.repaint();
//					}
//				}
//				ManagerPane.choosedImg = imgLabel;
//				popup.show(e.getComponent(), e.getX(), e.getY());				
//			}
//		}
//	}
//
//	//面板右键监听器
//	class PopupListenerOfPane extends MouseAdapter{
//		JPopupMenu popup;
//		
//		public PopupListenerOfPane(JPopupMenu popupMenu){
//			popup = popupMenu;
//			
//		}
//		
//		 public void mousePress(MouseEvent e){
//			 if(ManagerPane.choosedImg != null){
//				 ManagerPane.choosedImg.setOpaque(false);
//				 ManagerPane.choosedImg.repaint();
//			 }
//			 maybeShowPopup(e);
//		 }
//		
//		public void mouseReleased(MouseEvent e){
//			if(ManagerPane.choosedImg != null){
//				 ManagerPane.choosedImg.setOpaque(false);
//				 ManagerPane.choosedImg.repaint();
//			 }
//			System.out.println("设置");
//			maybeShowPopup(e);
//		}
//		
//		private void maybeShowPopup(MouseEvent e){
//			if(e.isPopupTrigger()){ //右键
//				popup.show(e.getComponent(), e.getX(), e.getY());				
//			}
//		}
//	}
//	 
//	
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//}
