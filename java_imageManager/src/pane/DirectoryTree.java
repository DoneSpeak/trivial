package pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import helper.Helper;
import main.ImageManager;
import menus.ManagerMenu;
import pane.DirectoryTree.DirExpansionListener;
import pane.DirectoryTree.DirSelectionListener;
import pane.DirectoryTree.ExpandTask;
import pane.DirectoryTree.SelectedTask;

public class DirectoryTree extends JPanel {
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JTextField showNumberOfPicture;
	ExpandTask expandTask;
	SelectedTask selectedTask; // 选择线程
	ArrayList<SelectedTask> TempSelectedTasks; // 选择线程
	ShowImageTask siTask = null;			//开启新的线程
	ShowImageTask tempSiTask = null;		//用于记录上一次开启的线程，并进行wait和add操作
	ArrayList<ShowImageTask> siTaskList;	//记录所有打开的siTask
	String tempNode = null;	//用于记录上一次访问的结点
	HashMap<String,ShowImageTask> JudgeTaskNode;  //用于判断是否有开启过线程
	
	// private JTextField showPathArea;
	// 设置图标
	public static final ImageIcon ICON_COMPUTER = new ImageIcon("icon\\computer.png");
	public static final ImageIcon ICON_COMPUTERCLOSE = new ImageIcon("icon\\computerClose.png");
	public static final ImageIcon ICON_DRIVE = new ImageIcon("icon\\drive.png");
	public static final ImageIcon ICON_FOLDER = new ImageIcon("icon\\folder.png");
	public static final ImageIcon ICON_EXPANDEDFOLDER = new ImageIcon("icon\\expandedfolder.png");

	public DirectoryTree() {
		super(new BorderLayout());
		// 根目录
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new IconData(ICON_COMPUTERCLOSE, ICON_COMPUTER, "此电脑"));

		DefaultMutableTreeNode node;
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			// 添加磁盘结点，同时设置结点的图片，并设为有子结点
			node = new DefaultMutableTreeNode(new IconData(ICON_DRIVE, ICON_DRIVE, new FileNode(roots[i])));
			top.add(node);
			node.add(new DefaultMutableTreeNode(new Boolean(true)));
		}

		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);

		tree.putClientProperty("JTree.lineStyle", "Angled");
		// 设置结点样式
		TreeCellRenderer renderer = new IconCellRenderer();
		tree.setCellRenderer(renderer);

		tree.addTreeExpansionListener(new DirExpansionListener());
		tree.addTreeSelectionListener(new DirSelectionListener());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // 单一选择
		tree.setShowsRootHandles(true);
		tree.setEditable(false);

		JScrollPane jsp = new JScrollPane();
		jsp.getViewport().add(tree);
		add(jsp, BorderLayout.CENTER);

		// showPathArea = new JTextField();
		// showPathArea.setEditable(false);
		// add(showPathArea,BorderLayout.NORTH);

		showNumberOfPicture = new JTextField("");
		showNumberOfPicture.setEditable(false);
		this.add(showNumberOfPicture, BorderLayout.SOUTH);

		setVisible(true);
	}// DirectoryTree构造

	// 利用文件名创建一个结点，返回
	DefaultMutableTreeNode getTreeNode(TreePath path) {
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}

	FileNode getFileNode(DefaultMutableTreeNode node) {
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof FileNode)
			return (FileNode) obj;
		else
			return null;
	}
	//展开监听器后台线程
	class ExpandTask extends SwingWorker<Void, Void> {

		DefaultMutableTreeNode node;

		public ExpandTask(DefaultMutableTreeNode node) {
			this.node = node;
		}

		public void setTreeNode(DefaultMutableTreeNode node) {
			this.node = node;
		}

		@Override
		public Void doInBackground() {
			final FileNode fnode = getFileNode(node);
			if (fnode != null && fnode.expand(node)) {
				treeModel.reload(node); // 更新该结点作为根的目录树
			}
			return null;
		}

		@Override
		public void done() {
		}
	}

	// 展开监听器，创建展开结点的树
	class DirExpansionListener implements TreeExpansionListener {
		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			expandTask = new ExpandTask(node);
			expandTask.execute();
		}

		public void treeCollapsed(TreeExpansionEvent ignore) {
		}
	}// end of DirExpansionListener
	//选择监听器后台线程
	class SelectedTask extends SwingWorker<Void, Void>{

		DefaultMutableTreeNode node;
		public SelectedTask(DefaultMutableTreeNode node) {
			this.node = node;
		}

		public void setTreeNode(DefaultMutableTreeNode node) {
			this.node = node;
		}
		
		@Override
		public Void doInBackground() {
	
			FileNode fnode = getFileNode(node);
			if (fnode != null) {
				// showPathArea.setText(fnode.getFile().getName());
				// 未完成
				// 获取选择目录下的所有图片到imageList中
				// 并将得到的数据传到showAll区域中

				File file = fnode.getFile();
				ImageManager.curNodePath = file;
				File[] images = Helper.getImages(file);

				for (File i : images) 
					ImageManager.imagesList.add(i);
			} else {
				// showPathArea.setText("");
				// top结点才不会使得fnode == null
				// 此时没有任何图片
			}
			return null;
		}

		@Override
		public void done() {
			//System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
			showNumberOfPicture.setText(ImageManager.imagesList.size() + " 张图片");
			ShowAllPane showAllPane = new ShowAllPane();
			ImageManager.curShowAllPane = showAllPane;
			new ManagerMenu().PopupMenuForPane(showAllPane);
			siTask = new ShowImageTask(showAllPane,node);
			tempSiTask = siTask;
			siTask.execute();
		}
	}

	// 选择监听器，得到该目录下的图片文件信息，在showNumberOfPicture区域展示所选目录中的文件数，以及在showView区域展示图片
	class DirSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent event) {
			//System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
			DefaultMutableTreeNode node = getTreeNode(event.getPath());
			ImageManager.currentNode = node;
			ImageManager.imagesList = new ArrayList<File>();
			new SelectedTask(node).execute();		
		}// end of DirSelectionListener

	}// DirectoryTree
}

// 图标信息类
class IconData {
	private Icon icon;
	private Icon expandedIcon;
	private Object data;

	public IconData(Icon icon, Object data) {
		this.icon = icon;
		expandedIcon = null;
		this.data = data;
	}

	public IconData(Icon icon, Icon expandedIcon, Object data) {
		this.icon = icon;
		this.expandedIcon = expandedIcon;
		this.data = data;
	}

	public Icon getIcon() {
		return icon;
	}

	public Icon getExpandedIcon() {
		return expandedIcon;
	}

	public Object getObject() {
		return data;
	}

	public String toString() {
		return data.toString();
	}

}// IconData

// 文件信息类
class FileNode {
	private File file;

	public FileNode(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String toString() {
		return file.getName().length() > 0 ? file.getName() : file.getPath();
	}

	

	// 展开,获取树的子结点获得
	// 返回true表示为有子结点，false无子结点
	public boolean expand(DefaultMutableTreeNode parent) {
		// 判断是否有子结点
		DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent.getFirstChild();
		if (flag == null) // 表示没有子结点
			return false;
		Object obj = flag.getUserObject();
		if (!(obj instanceof Boolean)) // obj 中没有AllowChild,表示没有子结点，即已全部展开
			return false;
		parent.removeAllChildren();

		//将目录作为树节点
		File[] files = Helper.getDirs(file);
		if (files == null)
			return true;
		
		LinkedList<FileNode> fnl = new LinkedList<FileNode>();
		
		for (File f : files) {	
			FileNode newNode = new FileNode(f);
			fnl.add(newNode);
		}

		fnl.sort(new comp_charIncrease());

		Iterator<FileNode> iter = fnl.iterator();
		while (iter.hasNext()) {
			FileNode fn = (FileNode) iter.next();
			IconData idata = new IconData(DirectoryTree.ICON_FOLDER, DirectoryTree.ICON_EXPANDEDFOLDER, fn);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(idata);
			parent.add(node);
			if (Helper.hasSubDirs(fn.file)) {
				// 设AllowChild为true
				node.add(new DefaultMutableTreeNode(new Boolean(true)));
			}
		}
		return true;
	}// expand()
}// FileNode

// 用于FileNode中的expand函数，对得到的结点进行字母序升序排序
class comp_charIncrease implements Comparator<FileNode> {
	@Override
	public int compare(FileNode f1, FileNode f2) {
		return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
	}
}

// 修改结点的显示样式
class IconCellRenderer extends JLabel implements TreeCellRenderer {
	// 颜色
	private Color textSelectionColor;
	private Color textNonSelectionColor;
	private Color bkSelectionColor;
	private Color bkNonSelectionColor;
	private Color borderSelectionColor;

	private boolean selected;

	public IconCellRenderer() {
		super();
		textSelectionColor = UIManager.getColor("Tree.selectionForeground");
		textNonSelectionColor = UIManager.getColor("Tree.textForeground");
		bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
		bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
		borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
		setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		setText(obj.toString());

		if (obj instanceof Boolean)
			setText("Retrieving data...");

		if (obj instanceof IconData) {
			IconData idata = (IconData) obj;
			if (expanded)
				setIcon(idata.getExpandedIcon());
			else
				setIcon(idata.getIcon());
		} else
			setIcon(null);

		setFont(tree.getFont());
		setForeground(sel ? textSelectionColor : textNonSelectionColor);
		setBackground(sel ? bkSelectionColor : bkNonSelectionColor);
		selected = sel;
		return this;
	}

	@Override
	public void paintComponent(Graphics g) {
		Color bColor = getBackground();
		Icon icon = getIcon();

		g.setColor(bColor);
		int offset = 0;
		if (icon != null & getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());
		g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		if (selected) {
			g.setColor(borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}
		super.paintComponent(g);
	}

}

/**
 *图片展示区 
 */
//这块代码的作用：可以实时在面板上显示图片，就是每找到一个图片就会显示多一个图片，主要是publish和process实现的实时功能
class ShowImageTask extends SwingWorker<Void, ImageLabel> {
	ShowAllPane showAllPane;
	DefaultMutableTreeNode node;
	ImageIcon img;
	ImageLabel imgLabel;
	int count = 0;
	double changeRate;
	int imgWidth;
	int imgHeight;
	int imgChangedWidth;
	int imgChangedHeight;
	
	public ShowImageTask(){
		showAllPane = null;
	}
	public ShowImageTask(ShowAllPane showAllPane,DefaultMutableTreeNode node) {
		this.showAllPane = showAllPane;
		this.showAllPane.setPreferredSize(new Dimension(420,420));
		this.node = node;
	}
	public void setShowAllPane(ShowAllPane showAllPane){
		this.showAllPane = showAllPane;
	}
	@Override
	public Void doInBackground() {
		//在后台中运行，所有的耗时操作都应该在这里运行
		ImageManager.sp.remove(ImageManager.curShowAllPane);		
		ImageManager.sp.getViewport().add(showAllPane);
		ImageManager.sp.revalidate();
		ImageManager.sp.updateUI();
		ImageManager.curShowAllPane = showAllPane;

		int n = ImageManager.imagesList.size();
		int index = 0;
		for(index = 0 ;index < n;index++){
			if(node != ImageManager.currentNode){
				return null;
			}
			//一下代码是将存在imgesList中的图片文件生成一个图片label，并抛出到process中
			File imgfile = ImageManager.imagesList.get(index);
			img=new ImageIcon(imgfile.getAbsolutePath());
			imgWidth = img.getIconWidth();
			imgHeight = img.getIconHeight();
			
			changeRate = 1.0*ImageManager.curImgWidth /imgWidth < 1.0*ImageManager.curImgHeight /imgHeight ? 
					1.0*ImageManager.curImgWidth /imgWidth : 1.0*ImageManager.curImgHeight /imgHeight ;
			imgChangedWidth = (int)(imgWidth * changeRate);
			imgChangedHeight = (int)(1.0*imgChangedWidth / imgWidth * imgHeight);
			img.setImage(img.getImage().getScaledInstance(imgChangedWidth,imgChangedHeight,Image.SCALE_SMOOTH));
			imgLabel = new ImageLabel(img,imgfile);
			//imgLabel.addMouseListener(new MouseEventOnImg(imgfile));
			//添加鼠标事件
			new ManagerMenu(imgLabel).PopupMenuForImg(imgLabel);
			publish(imgLabel); //会将数据抛出到process中
		}
		return null;
	}
	@Override
	protected void process(java.util.List<ImageLabel> chunks) { //注意这里的参数是一个List
		//这里好像也是运行在事件调度线程中
		//这段代码是一个例子，对应的参数类型是SwingWork<Void, ImageLabel>中的第二个参数，publish也是。
		int n = chunks.size();
		for(int i = 0;i<chunks.size();i++){ //这里是一个循环遍历参数的List
			count ++;
			if(count % 5 == 0){
				showAllPane.setPreferredSize(new Dimension(showAllPane.getWidth(),showAllPane.getHeight()+120));
			}
			showAllPane.add(chunks.get(i));
			showAllPane.updateUI();
			//showAllPane.repaint();
			//showAllPane.validate();
			//System.out.println("adding");
		}
	}

	@Override
	public void done() {
		//当后台程序运行结束后就会运行这段代码
		if(node != ImageManager.currentNode){
			//运行在事件调度线程中
			System.out.println("done");
		}
	}
}