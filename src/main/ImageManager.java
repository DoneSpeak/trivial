package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import frames.ManagerFrame;
import frames.ViewFrame;
import menus.ManagerMenu;
import pane.ImageLabel;
import pane.ManagerPane;
import pane.ShowAllPane;

public class ImageManager {
	
	public static ArrayList<File> imagesList;
	public static final int BIGLABEL_WIDTH = 216,BIGLABEL_HEIGHT = 162,
					  MIDDLELABEL_WIDTH = 120,MIDDLELABEL_HEIGHT = 90,
					  SMALLLABEL_WIDTH = 60,SMALLLABEL_HEIGHT = 45;
	public static int curImgWidth = 120,curImgHeight = 90;
	public static ShowAllPane curShowAllPane = null;
	public static ImageLabel choosedImg = null;
	public static JScrollPane sp;
	public static DefaultMutableTreeNode currentNode = null;
	public static File choosedImgFile = null;
	public static File curNodePath = null;
	public static boolean increase = true;
	public static String curSortType = "name";
	public static int tipInPaint = 1;
	public static JLabel statusbar;
	public static boolean needDeleted = false; //小写 ：c (cut)
	public static HashMap<File,File> renameFile;
	public static String sortCmd = "name";
	public static String showType = "middle";
	public static boolean sortIncrease = true;
	
	public static void main(String[] args) {
		//初始化线程(Initial Thread)，创建一个Runnable()对象
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//这里开始调度 UI事件调度线程(EDT)
				createAndShowGUI();
			}
		});

	}
	
	//这里开始调度 UI事件调度线程(EDT)
	public static void createAndShowGUI() {
		ManagerFrame managerFrame;
		managerFrame = new ManagerFrame("初见");
		managerFrame.setFrame();
		//将此设置放到调用方法中，可以实现关闭一个managerFrame窗口时，另一个不关闭
		//目前的bug是，关闭一个窗口时，另一个窗口将无法操作
		managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		renameFile = new HashMap<File, File>();
	}
	public static void createViewFrame(File imgfile){
		ViewFrame frame = new ViewFrame(imgfile);
		frame.setFrame();
	}
}
