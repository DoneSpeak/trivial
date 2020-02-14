package menus; 

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import helper.Helper;
import pane.ImageLabel;
import main.ImageManager;
import pane.ShowAllPane;

public class RepaintPane extends SwingWorker<Void, ImageLabel> {
	ShowAllPane showAllPane;
	DefaultMutableTreeNode node;
	ImageIcon img;
	ImageLabel imgLabel;
	int imgWidth;
	int imgHeight;
	double changeRate;
	int imgChangedWidth;
	int imgChangedHeight;
	
	
	public RepaintPane(){
		showAllPane = new ShowAllPane();
		new ManagerMenu().PopupMenuForPane(showAllPane);
		showAllPane.setPreferredSize(ImageManager.curShowAllPane.getSize());	
		node = ImageManager.currentNode;
	}
	public RepaintPane(ShowAllPane showAllPane){
		this.showAllPane = showAllPane;
	}
	public RepaintPane(ShowAllPane showAllPane,DefaultMutableTreeNode node) {
		this.showAllPane = showAllPane;
//		this.showAllPane.setPreferredSize(new Dimension(420,420));
		this.node = node;
	}
	public void setShowAllPane(ShowAllPane showAllPane){
		this.showAllPane = showAllPane;
	}
	
	public Void doInBackground() {
		ImageManager.sp.remove(ImageManager.curShowAllPane);
		ImageManager.sp.getViewport().add(showAllPane);
		ImageManager.sp.revalidate();
		ImageManager.sp.updateUI();
		ImageManager.curShowAllPane = showAllPane;
		int n = ImageManager.imagesList.size();
		System.out.println(n);
//		File[] images = Helper.getImages(ImageManager.choosedImgFile);
//		ImageManager.imagesList = new ArrayList<File> 
//		for (File i : images) 
//			ImageManager.imagesList.add(i);
		
		int index = 0;
		for(index = 0 ;index < n;index++){

			if(node != ImageManager.currentNode){
				return null;
			}
			
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
			new ManagerMenu(imgLabel).PopupMenuForImg(imgLabel);
			publish(imgLabel);
		}
		return null;
	}
	@Override
	protected void process(java.util.List<ImageLabel> chunks) {
		int n = chunks.size();
		for(int i = 0;i<chunks.size();i++){
			showAllPane.add(chunks.get(i));
			showAllPane.updateUI();
			System.out.println("adding");
		}
	}

	@Override
	public void done() {
		System.out.println("done");
	}
}