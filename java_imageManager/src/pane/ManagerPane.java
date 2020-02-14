package pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import frames.ViewFrame;
import helper.Helper;
import main.ImageManager;
import menus.ManagerMenu;

public class ManagerPane extends JPanel {
	
	public ManagerPane() {
		setLayout(new BorderLayout());

		// 中间的拆分窗格
		ImageManager.sp = new JScrollPane();
		ImageManager.sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ImageManager.curShowAllPane = new ShowAllPane();
		new ManagerMenu().PopupMenuForPane(ImageManager.curShowAllPane);
		ImageManager.curShowAllPane.setPreferredSize(new Dimension(480,420));
		ImageManager.sp.getViewport().add(ImageManager.curShowAllPane);
		JSplitPane middle = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new DirectoryTree(), ImageManager.sp);
		middle.setDividerLocation(160);
		middle.setDividerSize(1);
		// 底下的状态栏
		ImageManager.statusbar = new JLabel();
		ImageManager.statusbar.setText("图像路径");

		add(middle, BorderLayout.CENTER);
		add(ImageManager.statusbar, BorderLayout.SOUTH);
		validate();
	}
}														

