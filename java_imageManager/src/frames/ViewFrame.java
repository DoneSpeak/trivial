package frames;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ViewFrame extends JFrame{
	File imgfile;
	ImageIcon img;
	Image image;
	JPanel pane;
	JPanel tempPane;
	//修改
	int imgWidth = 480;
	int imgHeight = 360;
	int frameWidth = 480 + 60;
	int frameHeight = 360;
	int imgChangedWidth;
	int imgChangedHeight;
	double changeRate;
	JLabel imgLabel;
	public ViewFrame(File imgfile){
		this.imgfile = imgfile;
		img=new ImageIcon(imgfile.getAbsolutePath());
		imgWidth = img.getIconWidth();
		imgHeight = img.getIconHeight();
		image = img.getImage();
	}

	public void setFrame(){
		setTitle(imgfile.getName());
		//窗体位置及大小
		Toolkit kit=getToolkit();
		Dimension winSize=kit.getScreenSize();
		setBounds((winSize.width-frameWidth)/2,(winSize.height-frameHeight)/2,frameWidth,frameHeight);
		//setBounds(500,500,frameWidth,frameHeight);
		//this.pack();
		this.setVisible(true);
		this.setMinimumSize(new Dimension(frameWidth,frameHeight));
		
		changeRate = 1.0*frameWidth /imgWidth < 1.0*frameHeight /imgHeight ? 1.0*frameWidth /imgWidth : 1.0*frameHeight /imgHeight ;
		imgChangedWidth = (int)(imgWidth * changeRate);
		imgChangedHeight = (int)(1.0*imgChangedWidth / imgWidth * imgHeight);
		//.getScaledInstance(imgWidth,imgHeight,Image.SCALE_SMOOTH);
        pane = new BackgroundPanel(image.getScaledInstance(imgChangedWidth,imgChangedHeight,Image.SCALE_SMOOTH));
        tempPane = pane;
		this.add(pane);
		this.addComponentListener(new changeImgSizeWithFrame(this));
		this.validate();
	}

	//修改：还有一种在JLabel中实现的方法，实验判断哪个显示效果更好
	class BackgroundPanel extends JPanel{
	    private Image image;
	    public BackgroundPanel(Image image){ 
	        this.image=image;
	        Dimension size = new Dimension(frameWidth,frameHeight);
	        setSize(size);
	    }
	    @Override
	    public void paintComponent(Graphics g) 
	    {
//	         super.paintComponent(g);
//		     Dimension size=this.getParent().getSize();
	    	//居中设置
	    	int a = (int)(1.0*(frameWidth - imgChangedWidth)/2);
	    	int b = (int)(1.0*(frameHeight - imgChangedHeight)/2);
		     g.drawImage(image,a,b,imgChangedWidth,imgChangedHeight,this);
	    }
	}

	class changeImgSizeWithFrame extends ComponentAdapter{
		ViewFrame adapter;
		
		public changeImgSizeWithFrame(ViewFrame adapter){
			this.adapter = adapter;
		}
		@Override
		public void componentResized(ComponentEvent e){
			adapter.changeImgSizeAutoUsePane(adapter.pane);
		}
	}
	public void changeImgSizeAutoUsePane(JPanel pane){
		//if(frameWidth != this.getWidth() && frameHeight != this.getHeight()){
			frameWidth = this.getWidth();
			frameHeight = this.getHeight();	
			//System.out.println(frameWidth+" "+frameHeight);
			changeRate = 1.0*frameWidth /imgWidth < 1.0*frameHeight /imgHeight ? 1.0*frameWidth /imgWidth : 1.0*frameHeight /imgHeight ;
			//由于imgWidth 和 imgHeight最后会变成int，所以存在一个误差还需继续细化缩放比
			//优化
			//最后的效果中显示Frame中右一块空白的区域
//			System.out.println(changeRate);
//			System.out.println(imgWidth+" "+imgHeight);
			imgChangedWidth = (int)(imgWidth * changeRate);
			imgChangedHeight = (int)(1.0*imgChangedWidth / imgWidth * imgHeight);
			//System.out.println(imgWidth+" "+imgHeight);
		//}
		this.remove(tempPane);
		this.repaint();
		pane = new BackgroundPanel(image);
		pane.setSize(frameWidth ,frameHeight);
		this.add(pane);
		this.repaint();
		this.validate();
	}
	


}

/*
 * 3.3.4 读缩略图 
有的图片格式允许一个（或多个）小的预览图，与主图片一起存储在文件中。这些
“缩略图”对于快速识别图片很有用，不用解码整个图片。

程序可以调用如下代码，探测一张图片有多少张缩略图：
reader.getNumThumbnails(imageIndex);

如果存在缩略图，可以调用如下代码获取：
int thumbailIndex = 0;
BufferedImage bi;
bi = reader.readThumbnail(imageIndex, thumbnailIndex);
 */


//public class BackgroundPanel extends JPanel {
//
//// 固定背景图片，允许这个JPanel可以在图片上添加其他组件
//protected void paintComponent(Graphics g) {
//	//g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);			
//	img=new ImageIcon(imgfile.getAbsolutePath());
//	//img.setImage(img.getImage().getScaledInstance(imgWidth,imgHeight,Image.SCALE_SMOOTH));
//	image = img.getImage();
//	g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
//	
//}
//}
//

//public void changeImgSizeAuto(JLabel imgLabel){
//double changeRate;
//changeRate = 1.0*imgWidth /frameWidth< 1.0*imgHeight /frameHeight ?  1.0*imgWidth /frameWidth : 1.0*imgHeight /frameHeight;
//System.out.println(changeRate);
//this.remove(tempPane);
//this.repaint();
//this.validate();
//imgHeight = (int)(frameHeight * changeRate);
//imgWidth = (int)(frameWidth * changeRate);
//img=new ImageIcon(imgfile.getAbsolutePath());
//////img.setImage(img.getImage().getScaledInstance(imgWidth,imgHeight,Image.SCALE_SMOOTH));
//image = img.getImage();
//pane = new BackgroundPanel(image);
//tempPane = pane;
//this.add(pane);
//this.repaint();
//this.validate();
//}













