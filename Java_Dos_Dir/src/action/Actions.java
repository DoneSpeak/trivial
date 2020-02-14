package action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import helper.Helper;

public class Actions {
	// 沒有获取当前路径和上一级路径的信息
	public static boolean getInfomations(CommandElements cmdE, SearchObject searchObj, SumOfFilesAndDirs sofd)
			throws IOException {
		// 根据当前路径得到fileList用actionA进行筛选 >> ArrayList<File>
		// 用actionO中对得到的数组进行处理 >> ArrayList<ArrayList<File>> fileListList
		// 得到目的数组 >> ArrayList<File>
		// 输出
		// 如果有s,递归
		if (cmdE.commandPS.contains('s')) {
			actionS(cmdE, searchObj, sofd);
			ShowAction.SumInfoOfFilesAndDirs(cmdE.modifyElements.contains('d'), searchObj.filePath, sofd);
		} else {
			ArrayList<File> fileList = new ArrayList<File>();
			fileList = actionA(cmdE, searchObj,sofd);
			ArrayList<ArrayList<File>> fileListList = new ArrayList<ArrayList<File>>();
			fileListList.add(fileList);
			fileList = actionO(cmdE, fileListList);
			ShowAction.show(cmdE, fileList, searchObj);
			if(sofd.fileNum == 0 && searchObj.fileName == ".+"){}				
			else if (cmdE.showType != 'b')
				ShowAction.SumInfoOfDirs(cmdE.modifyElements.contains('d'), searchObj.filePath, sofd);
		}
		return true;
	}

	// 获取所有文件
	public static void actionDir(String curPath, ArrayList<File> file) throws IOException {

		File curfile = new File(curPath);

		File pDirectory = curfile.getParentFile();

		file.add(curfile);
		file.add(pDirectory);

		File[] fl = curfile.listFiles();

		for (File f : fl) {
			file.add(f);
		}
	}

	public static void actionS(CommandElements cmdE, SearchObject searchObj, SumOfFilesAndDirs sofd)
			throws IOException {
		// 利用a o 处理信息，输出
		ArrayList<File> fileList = new ArrayList<File>();
		fileList = actionA(cmdE, searchObj,sofd);
		ArrayList<ArrayList<File>> fileListList = new ArrayList<ArrayList<File>>();
		fileListList.add(fileList);
		fileList = actionO(cmdE, fileListList);
		ShowAction.show(cmdE, fileList, searchObj );
		System.out.println("");
		File curfile = new File(searchObj.filePath);
		File[] fl = curfile.listFiles();

		for (File f : fl) {
			if (f.isDirectory() && f.getAbsolutePath().length() > 3) {
				searchObj.filePath = f.getAbsolutePath();
				actionS(cmdE, searchObj, sofd);
			}
		}
	}

	public static ArrayList<File> actionA(CommandElements cmdE, SearchObject SearchObj,SumOfFilesAndDirs sofd)
			throws IOException {
		String path = SearchObj.filePath;
		String fileName = SearchObj.fileName;
		boolean isRegex = SearchObj.isRegex;
		ArrayList<File> fileList = new ArrayList<File>();
		File tf = new File(path);
		if(!tf.exists()){
			return fileList;
		}
		// 查找文件目录：fileName = null; 查找指定目录下的文件
		if (path == null) {
			path = System.getProperty("user.home");
		}
		if (fileName == null) {
			fileName = ".+";
			isRegex = true;
		}
		// 没有a或者仅有a
		boolean getHiddenAndSystem;
		if (cmdE.actionA.size() >= 1)
			getHiddenAndSystem = true;
		else
			getHiddenAndSystem = false;
		// 初始的数据为所有的数据

		DosFileAttributes attr = null;
		// 不查找文件，获取目录
		if (fileName == ".+") {

			File curfile = new File(path);

			if (path.length() > 3 ) {
				File pDirectory = curfile.getParentFile();
				fileList.add(curfile);
				fileList.add(pDirectory);
			}
			File[] fl = curfile.listFiles();

			for (File f : fl) {
				attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
				if ((!f.isHidden() && !attr.isSystem()) || getHiddenAndSystem)
					fileList.add(f);
			}
		}
		// 查找指定路径或者当前路径符合条件的文件
		else {
			File curfile = new File(path);

			File[] fl = curfile.listFiles();
			for (File f : fl) {
				attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
				if ((!f.isHidden() && !attr.isSystem()) || getHiddenAndSystem) {
					if (isRegex) {
						if (f.getName().matches(fileName)) {
							fileList.add(f);
						}
					} else if (f.getName().equals(fileName))
						fileList.add(f);
				}
			}
		}
		Iterator<File> fiter = fileList.iterator();
		while(fiter.hasNext()){
			File f = fiter.next();
			if(f.isFile()){
				sofd.fileNum++;
				sofd.sizeOfFiles += f.length();
			}
			else
				sofd.dirNum++;
		}

		if (cmdE.actionA.size() <= 1) {
			return fileList;
		}
		// size>1(含有其他属性) d(目录) r(只读) h（隐藏） a（存档） s（系统） i（索引） l（解析点）取负数表示-
		if (cmdE.actionA.size() > 1) {
			if (cmdE.actionA.contains((int) 'h')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					if (f.isHidden())
						tfile.add(f);
				}
				fileList = tfile;
			}
			
			if (cmdE.actionA.contains(-(int) 'h')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					if (!f.isHidden())
						tfile.add(f);
				}
				fileList = tfile;
			}
			
			if (cmdE.actionA.contains((int) 's')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (attr.isSystem())
						tfile.add(f);
				}
				fileList = tfile;
			}
			if (cmdE.actionA.contains(-(int) 's')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (!attr.isSystem())
						tfile.add(f);
				}
				fileList = tfile;
			}
			if (cmdE.actionA.contains((int) 'd')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					if (f.isDirectory())
						tfile.add(f);
				}
				fileList = tfile;
			}
			if (cmdE.actionA.contains(-(int) 'd')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					if (!f.isDirectory())
						tfile.add(f);
				}
				fileList = tfile;
			}

			if (cmdE.actionA.contains((int) 'r')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (attr.isReadOnly())
						tfile.add(f);
				}
				fileList = tfile;
			}

			if (cmdE.actionA.contains(-(int) 'r')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (!attr.isReadOnly())
						tfile.add(f);
				}
				fileList = tfile;
			}
			if (cmdE.actionA.contains((int) 'a')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (attr.isArchive())
						tfile.add(f);
				}
				fileList = tfile;
			}
			if (cmdE.actionA.contains(-(int) 'a')) {
				ArrayList<File> tfile = new ArrayList<File>();
				Iterator<File> iter = fileList.iterator();
				while (iter.hasNext()) {
					File f = iter.next();
					attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), DosFileAttributes.class);
					if (!attr.isArchive())
						tfile.add(f);
				}
				fileList = tfile;
			}
			
			
			/************ 未实现 *************/
			if (cmdE.actionA.contains((int) 'i')) {

			}
			if (cmdE.actionA.contains(-(int) 'i')) {

			}
			if (cmdE.actionA.contains((int) 'l')) {

			}
			if (cmdE.actionA.contains(-(int) 'l')) {

			}
			/**************************************/
		}
		
		fiter = fileList.iterator();
		while(fiter.hasNext()){
			File f = fiter.next();
			if(f.isFile()){
				sofd.fileNum++;
				sofd.sizeOfFiles += f.length();
			}
			else
				sofd.dirNum++;
		}
		return fileList;
	}

	public static ArrayList<File> actionO(CommandElements cmdE, ArrayList<ArrayList<File>> fileListList)
			throws IOException {
		//// isEmpty(没有) size=1(第一个为1，仅有/a) size>1(含有其他属性) n(名字) s(大小) e(扩展名)
		//// d(时间) g(组目录优先) 负数表示-
		if (cmdE.actionO.isEmpty()) { // 不排序
			return fileListList.get(0);
		}
		if (cmdE.actionO.size() == 1) { // 只排 /0
			ArrayList<File> fileList = fileListList.get(0);
			FileComparator_DirFFileL fc = new FileComparator_DirFFileL();
			fileList.sort(fc);
			return fileList;
		}
		Iterator<Integer> iter = cmdE.actionO.iterator();
		iter.next();// 删除第一个表示只有/o的元素
		while (iter.hasNext()) {
			Comparator<File> fc;
			int action = iter.next();
			switch (action) {
			case (int) 'n': 
			case -(int) 'n':{
				if(action == (int)'n')
					fc = new FileComparator_Name();
				else
					fc = new FileComparator_ReName();
				
				sortWithDifferentFC(fileListList, fc);
				ArrayList<ArrayList<File>> finalfll = new ArrayList<ArrayList<File>>();
				Iterator<ArrayList<File>> aFIter = fileListList.iterator();
				while (aFIter.hasNext()) {
					ArrayList<File> sameFile = new ArrayList<File>();
					ArrayList<File> flist = aFIter.next();
					Iterator<File> afi = flist.iterator();
					File f1 = null;
					if(afi.hasNext()){
						f1 = afi.next();
						sameFile.add(f1);
					}

					while(afi.hasNext()){	//同名称的放到同一个数组中
						ArrayList<File> blank = new ArrayList<File>();
						File f2 = afi.next();
						if(f1.getName().equals(f2.getName())){
							sameFile.add(f2);
							f1 = f2;
						}			
						else{
							f1 = f2;
							finalfll.add(sameFile);
							sameFile=blank;
							sameFile.add(f1);
						}
					}
					finalfll.add(sameFile);
				}
				fileListList = finalfll;
				break;
			}
			case (int) 'e': 
			case -(int) 'e': {
				if(action == (int)'e')
					fc = new FileComparator_Extension();
				else
					fc = new FileComparator_ReName();
				
				sortWithDifferentFC(fileListList, fc );
				ArrayList<ArrayList<File>> finalfll = new ArrayList<ArrayList<File>>();
				Iterator<ArrayList<File>> aFIter = fileListList.iterator();
				while (aFIter.hasNext()) {
					ArrayList<File> sameFile = new ArrayList<File>();
					ArrayList<File> flist = aFIter.next();
					Iterator<File> afi = flist.iterator();
					File f1 = null;
					if(afi.hasNext()){
						f1 = afi.next();
						sameFile.add(f1);
					}

					while(afi.hasNext()){	//同名称的放到同一个数组中
						ArrayList<File> blank = new ArrayList<File>();
						File f2 = afi.next();
						if(Helper.getExtension(f1.getName()).equals(Helper.getExtension(f2.getName()))){
							sameFile.add(f2);
							f1 = f2;
						}			
						else{
							f1 = f2;
							finalfll.add(sameFile);
							sameFile=blank;
							sameFile.add(f1);
						}
					}
					finalfll.add(sameFile);
				}
				fileListList = finalfll;
				break;
			}
			case (int) 'd': 
			case -(int) 'd':{
				if(action == (int)'d')
					fc = new FileComparator_DateFF();
				else
					fc = new FileComparator_DateLF();
				
				sortWithDifferentFC(fileListList, fc );
				ArrayList<ArrayList<File>> finalfll = new ArrayList<ArrayList<File>>();
				Iterator<ArrayList<File>> aFIter = fileListList.iterator();
				while (aFIter.hasNext()) {
					ArrayList<File> sameFile = new ArrayList<File>();
					ArrayList<File> flist = aFIter.next();
					Iterator<File> afi = flist.iterator();
					File f1 = null;
					if(afi.hasNext()){
						f1 = afi.next();
						sameFile.add(f1);
					}
					while(afi.hasNext()){	//同名称的放到同一个数组中
						ArrayList<File> blank = new ArrayList<File>();
						File f2 = afi.next();
						if(Helper.getDate(f1,cmdE) == (Helper.getDate(f2,cmdE))){
							sameFile.add(f2);
							f1 = f2;
						}			
						else{
							f1 = f2;
							finalfll.add(sameFile);
							sameFile=blank;
							sameFile.add(f1);
						}
					}
					finalfll.add(sameFile);
				}
				fileListList = finalfll;
				break;
			}
			case (int) 's': 
			case -(int)'s':{
				if(action == (int)'s')
					fc = new FileComparator_SizeInclease();
				else
					fc = new FileComparator_SizeDeclease();			
				sortWithDifferentFC(fileListList, fc);
				
				ArrayList<ArrayList<File>> finalfll = new ArrayList<ArrayList<File>>();
				Iterator<ArrayList<File>> aFIter = fileListList.iterator();
				while (aFIter.hasNext()) {
					ArrayList<File> sameFile = new ArrayList<File>();
					ArrayList<File> flist = aFIter.next();
					Iterator<File> afi = flist.iterator();
					File f1 = null;
					if(afi.hasNext()){
						f1 = afi.next();
						sameFile.add(f1);
					}

					while(afi.hasNext()){	//同名称的放到同一个数组中
						ArrayList<File> blank = new ArrayList<File>();
						File f2 = afi.next();
						if((f1.isDirectory() || f1.length()==0) && ( f2.isDirectory() || f2.length()==0 )){
							sameFile.add(f2);
							f1 = f2;
						}
						else if(!f1.isDirectory() && !f2.isDirectory() && f1.length() == f2.length()){
							sameFile.add(f2);
							f1 = f2;
						}
						else{
							f1 = f2;
							finalfll.add(sameFile);
							sameFile=blank;
							sameFile.add(f1);
						}
					}
					finalfll.add(sameFile);
				}
				fileListList = finalfll;
				break;
			}
			case (int) 'g': 
			case -(int) 'g': {
				/************未实现*************/
				/*
				if(action == (int)'n')
					fc = new FileComparator_groupFist();
				else
					fc = new FileComparator_groupLast();
				
				sortWithDifferentFC(fileListList, fc);
				ArrayList<ArrayList<File>> finalfll = new ArrayList<ArrayList<File>>();
				Iterator<ArrayList<File>> aFIter = fileListList.iterator();
				while (aFIter.hasNext()) {
					ArrayList<File> sameFile = new ArrayList<File>();
					ArrayList<File> flist = aFIter.next();
					Iterator<File> afi = flist.iterator();
					File f1 = null;
					if(afi.hasNext()){
						f1 = afi.next();
						sameFile.add(f1);
					}

					while(afi.hasNext()){	//同名称的放到同一个数组中
						ArrayList<File> blank = new ArrayList<File>();
						File f2 = afi.next();
						if(f1.getName().equals(f2.getName())){
							sameFile.add(f2);
							f1 = f2;
						}			
						else{
							f1 = f2;
							finalfll.add(sameFile);
							sameFile=blank;
							sameFile.add(f1);
						}
					}
					finalfll.add(sameFile);
				}
				fileListList = finalfll;*/
				/**************************************/
				break;
			}
			default: {
				break;
			}
			} // endOfSwitch
		} // endOfWhile

		ArrayList<File> fileList = new ArrayList<File>();
		Iterator<ArrayList<File>> fAIter = fileListList.iterator();
		while (fAIter.hasNext()) {
			ArrayList<File> fileArray = fAIter.next();
			Iterator<File> fLIter = fileArray.iterator();
			while (fLIter.hasNext()) {
				File f = fLIter.next();
				fileList.add(f);
			}
		}
		return fileList;
	}
	
	public static void sortWithDifferentFC(ArrayList<ArrayList<File>> fileListList, Comparator<File> fc) {
		ArrayList<ArrayList<File>> tfll = new ArrayList<ArrayList<File>>();
		Iterator<ArrayList<File>> aFIter = fileListList.iterator();
		while (aFIter.hasNext()) {
			ArrayList<File> flist = aFIter.next();
			flist.sort(fc);
			tfll.add(flist);
		}
		fileListList = tfll;
	}

	// isEmpty(没有) t(时间w>c>a) q(所有者) -c(大小数值表示方法,用d) l (名称小写)
	// 日期格式
	public static DateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm");

	public static void modifyAction(CommandElements cmdE, File file, FileInfo fInfo) throws IOException {
		// longName
		if (cmdE.modifyElements.contains('l'))
			fInfo.longName = Helper.stringToLowerCase(file.getName());
		else
			fInfo.longName = file.getName();
		// 时间格式 date
		BasicFileAttributes bfa = Files.readAttributes(Paths.get(file.getAbsolutePath()), DosFileAttributes.class);
		if (cmdE.modifyElements.contains('c')) {
			fInfo.date = df.format(new Date(bfa.creationTime().toMillis()));
		} else if (cmdE.modifyElements.contains('a')) {
			fInfo.date = df.format(new Date(bfa.lastAccessTime().toMillis()));
		} else if (cmdE.modifyElements.contains('w') || cmdE.modifyElements.contains('W')) {
			fInfo.date = df.format(new Date(file.lastModified()));
		}

		// owner
		if (cmdE.modifyElements.contains('q')) {
			FileOwnerAttributeView foav = Files.getFileAttributeView(Paths.get(file.getAbsolutePath()),
					FileOwnerAttributeView.class);
			String owner = foav.getOwner().getName();
			fInfo.owner = Helper.formatOwner(owner, 23);
		} else {
			fInfo.owner = "";
		}
		// size格式 &&
		if (!file.isDirectory()) {
			if (cmdE.modifyElements.contains('d'))
				fInfo.size = Helper.formatSize(String.format("%d", file.length()), 18);
			else
				fInfo.size = Helper.formatSize(String.format("%,d", file.length()), 18);
		} else {
			fInfo.size = "";
		}
		/************ 暂时使用 ***************/
		// 找到获取相应信息的方法，直接实现即可，其他的无需修改
		// type
		fInfo.type = "";
		if (file.isDirectory()) {
			fInfo.type = Helper.formatType("<DIR>", 15);
		}
		// shortName
		fInfo.shortName = "";
		/*****************
		 * 未实现****************** /* //解析点，修改longName和type
		 * if(cmdE.actionA.contains((int)'l')){
		 * 
		 * } else{ if(file.isDirectory()) fInfo.type = Helper.formatType("<DIR>"
		 * , 15); } //无内容索引文件，获取fInfo.type if(cmdE.actionA.contains((int)'i')){
		 * 
		 * } else{
		 * 
		 * }
		 * 
		 * //shortName if(cmdE.showType == 'x'){
		 * 
		 * } else{ fInfo.shortName = ""; }
		 ****************************************/
	}
}

class FileComparator_DirFFileL implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.isDirectory() && f2.isDirectory())
			return f1.compareTo(f2);
		else if (f1.isDirectory() && !f2.isDirectory())
			return -1;
		else if (!f1.isDirectory() && f2.isDirectory())
			return 1;
		else
			return f1.compareTo(f2);
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

class FileComparator_DateFF implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.lastModified() - f2.lastModified() >= 0)
			return 1;
		else
			return -1;
	}
}

class FileComparator_DateLF implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if (f1.lastModified() - f2.lastModified() >= 0)
			return -1;
		else
			return 1;
	}
}

class FileComparator_SizeDeclease implements Comparator<File> {
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

class FileComparator_SizeInclease implements Comparator<File> {
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

/******************* 未实现 **********************/

class FileComparator_groupFist implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		return 1;
	}
}

class FileComparator_groupLast implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		return 1;
	}
}

/*******************************************/
