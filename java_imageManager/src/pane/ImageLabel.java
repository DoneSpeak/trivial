package pane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import main.ImageManager;

public class ImageLabel extends JLabel{ //implements MouseListener {
	ImageIcon img;
	File imgFile;
	public ImageLabel(ImageIcon img,File imgFile) {
		super(img,SwingConstants.CENTER);
		this.img = img;
		this.imgFile = imgFile;
		this.setPreferredSize(new Dimension(ImageManager.curImgWidth+30, ImageManager.curImgHeight+10));
		//addMouseListener(this);
	}
	
	public void setImgFile(File newFile){
		imgFile = newFile;
	}
	
	public File getFile(){
		return imgFile;
	}
}