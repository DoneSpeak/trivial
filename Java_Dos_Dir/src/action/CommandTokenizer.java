package action;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import helper.Helper;

public class CommandTokenizer {

	public static boolean commandTokenizer(String[] source, CommandElements cmdE, ArrayList<SearchObject> searchObj)
			throws CommandException {
		// 判断指定路径是否为存在
		// 解析查找文件名

		// 判断操作元素和展示元素是否合法
		// 判断输出文件目的地是否合法

		// 分离文件名和指令
		ArrayList<String> fileStr = new ArrayList<String>();
		ArrayList<String> cmdStr = new ArrayList<String>();
		if(source.length == 0){
			SearchObject sobj = new SearchObject();
			searchObj.add(sobj);
			return true;
		}
		for (int i = 0; i < source.length; i++) {
			StringTokenizer stk = new StringTokenizer(source[i], ";,");

			// 可能 /ah/a:h/o fileName Dir\fileName fileName/a:h
			while (stk.hasMoreTokens()) {
				String temp = stk.nextToken();
				if (temp.startsWith("/")) { // 必定是指令
					cmdStr.add(temp);
				}
				else { // 有可能是 fileName/cmd/cmd2
					int index = temp.indexOf('/');
					if (index < 0) {
						fileStr.add(temp);
					} else {
						fileStr.add(temp.substring(0, index));
						cmdStr.add(temp.substring(index));
					}
				}
			}
		}
		// 获取命令指令
		getCommandElements(cmdE, cmdStr);

		// 解析查找文件名
		if(fileStr.size() == 0){
			SearchObject so = new SearchObject();
			searchObj.add(so);
		}
		Iterator<String> iter = fileStr.iterator();
		while (iter.hasNext()) {
			String f = iter.next();
			searchObj.add(getFileNameInfo(f));
		}

		return true;
	}
	// 异常处理

	// 解析查找文件，判断是否需要用正则表达式代替
	public static SearchObject getFileNameInfo(String fileName) throws CommandException {
		
		SearchObject sf = new SearchObject();
		int indexOfColon = fileName.indexOf(':');	//指定根目录
		if (indexOfColon == 1 ) { // 查找指定目录
			//以 path\fileName path\path
			int indexOfLastSlash = fileName.lastIndexOf('\\');
			if(indexOfLastSlash == fileName.length()-1){	//目录
				if(fileName.contains("*") || fileName.contains("?")){
					throw new CommandException(ExceptionEnum.GrammaticalMistake,null);
				}
				sf.filePath = fileName.replace(fileName.charAt(0), Character.toUpperCase(fileName.charAt(0)));			
				sf.fileName = ".+";	//检索所有
			}
			else if(indexOfLastSlash > 1){
				if(fileName.contains("*") || fileName.contains("?")){
					sf.filePath = fileName.substring(0, indexOfLastSlash);
					if(sf.filePath.contains("*") || sf.filePath.contains("?")){
						throw new CommandException(ExceptionEnum.GrammaticalMistake,null);
					}

					fileName = fileName.substring(indexOfLastSlash + 1);
					sf.isRegex = true;	
					fileName = fileName.replace(".","[.]");
					if (fileName.indexOf("?") != -1) {
						fileName = fileName.replace("?", ".{0,1}");
					}
					if (fileName.indexOf("*") != -1) {
						fileName = fileName.replace("*", ".+");
					}
					sf.fileName = fileName;
				}
				else{
					File tf = new File(fileName);
					if(tf.isDirectory()){
						sf.filePath = fileName;
					}
					else {
						sf.filePath = fileName.substring(0, indexOfLastSlash);
						sf.fileName =  fileName.substring(indexOfLastSlash + 1);
					}
						
				}
				
			}
			else if(indexOfLastSlash < 0 ){
				if(fileName.length()==2){
					sf.filePath = fileName.replace(fileName.charAt(0), Character.toUpperCase(fileName.charAt(0)))+"\\";
					sf.fileName = ".+";
				}
				else{
					throw new CommandException(ExceptionEnum.IsCannotIdentifyDevice,fileName.substring(0,fileName.indexOf(":")+1));
				}
			}
					
		}
		else if(indexOfColon < 0){// 查找默认路径  path\fileName \fileName fileName
			String initPath = Paths.get(System.getProperty("user.home")).toString();
			if(fileName.startsWith("\\")){
				fileName = initPath+fileName;
			}
			else{
				fileName = initPath + "\\"+fileName;
			}
			int indexOfLastSlash = fileName.lastIndexOf('\\');
			if(fileName.contains("*") || fileName.contains("?")){
				sf.filePath = fileName.substring(0, indexOfLastSlash);
				if(sf.filePath.contains("*") || sf.filePath.contains("?")){
					throw new CommandException(ExceptionEnum.GrammaticalMistake,null);
				}

				fileName = fileName.substring(indexOfLastSlash + 1);
				sf.isRegex = true;	
				fileName = fileName.replace(".","[.]");
				if (fileName.indexOf("?") != -1) {
					fileName = fileName.replace("?", ".{0,1}");
				}
				if (fileName.indexOf("*") != -1) {
					fileName = fileName.replace("*", ".+");
				}
				sf.fileName = fileName;
			}
			else{
				File tf = new File(fileName);
				if(tf.isDirectory())
					sf.filePath = fileName;
				else {
					sf.filePath = fileName.substring(0, indexOfLastSlash);

					sf.fileName =  fileName.substring(indexOfLastSlash + 1);
				}
			}			
		}
		else{
			throw new CommandException(ExceptionEnum.IsCannotIdentifyDevice,fileName.substring(0,fileName.indexOf(":")+1));
		}
		return sf;
	}

	public enum CmdEnum { a, b, c, d, e, f, g, h, i, l, n, o, p, q, r, s, t, w, x , A, B, C, D, E, F, G, H, I, L, N, O, P, Q, R, S, T, W, X }

	public static void getCommandElements(CommandElements cmdE, ArrayList<String> cmdStr) throws CommandException {
		String actionStr = "abcdlnopqrstwxABCDLNOPQRSTWX";
		String actionAEStr = "DRHASILdrhasil-"; // 大写长度为7
		String actionOEStr = "NSEDGnsedg-";
		String showEStr = "nwdxb";
		String Wacw = "Wacw";
		String aot = "aotAOT";
		Iterator<String> iter = cmdStr.iterator();
		while (iter.hasNext()) {
			String p = iter.next();
			if (p.contains("//"))
				throw new CommandException(action.ExceptionEnum.WrongParameter, "//"); // 参数格式不正确
																						// -																					// "/"。
			if (p.endsWith("/"))
				throw new CommandException(action.ExceptionEnum.WrongParameter, "//"); // 参数格式不正确
																						// -
																						// ""。
			// p = "/-ahr-iu/-be/p"
			StringTokenizer stk = new StringTokenizer(p, "/");

			while (stk.hasMoreTokens()) { // -ahr-iu -be p
				String tCmd = stk.nextToken();
				tCmd = Helper.stringToLowerCase(tCmd);
				char ch = tCmd.charAt(0);
				int len = tCmd.length();
				
				if(ch == '?')
					throw new CommandException(action.ExceptionEnum.PrintHelpInfo, null); // 输出帮助文件
				
				if (ch == '-') { // -a 和 -o -t 不可用 ，没有效果，-c -n 会有效果，其他的是原效果
					if (len == 1)
						throw new CommandException(action.ExceptionEnum.WrongSwitch, ""); // 无效开关 - ""。
					char ch2 = tCmd.charAt(1);
					if(ch2 == '-')
						throw new CommandException(action.ExceptionEnum.PrintHelpInfo, null); // 输出帮助文件
					if (ch2 == 'a' || ch2 == 'o' || ch2 == 't') {
						if (tCmd.length() == 2)
							continue;
						else { // -ah -os 报错
							String par = tCmd.substring(2);
							throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确 - "ah"。
						}
					} 
					else if (len > 2) { // 仅有a,o,t 有属性
						String par = tCmd.substring(1);
						throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确 - "sl"。
					}
					// 下面的操作已确保tcmd长度为2
					else if (ch2 == 'n' || ch2 == 'N') { // 此时的n为 -n,showEStr = "0nwdxb(6)"
						if (showEStr.indexOf(cmdE.showType) <= showEStr.indexOf('m')) // 没有m，所以m的下标为-1
							cmdE.showType = 'm';
					} else if (ch2 == 'c') {
						if (!cmdE.modifyElements.contains('d'))
							cmdE.modifyElements.add('d');
					}
					continue;
				}

				if (aot.contains(Character.toString(ch))) {
					String chEnumElement = Character.toString(ch);
					CmdEnum ce = CmdEnum.valueOf(chEnumElement);

					switch (ce) {
					case a:
					case A: {
						if (!cmdE.actionA.contains(1))
							cmdE.actionA.add(1); // 未有a
						int k = 1;
						if(len > 1 && tCmd.charAt(1) == ':'){
							if( (tCmd.length() > 2 && tCmd.charAt(2)==':')){
								throw new CommandException(action.ExceptionEnum.WrongSwitch, tCmd.substring(k+1));
							}
							else{
								k++;
							}
						}
						int sign = 1;
						for (; k < len; k++) {
							// actionAEStr = "DRHASILdrhasil-";
							char aAt = tCmd.charAt(k);

							if (actionAEStr.contains(Character.toString(aAt))) {
								if (aAt == '-') {
									if (sign == -1)
										throw new CommandException(action.ExceptionEnum.WrongParameter, tCmd.substring(k+1));
									sign = -1;
								} else { 
									if(!cmdE.actionA.contains(-1*sign*(int)aAt) && !cmdE.actionA.contains(sign * (int) aAt)){  //先进优先
										cmdE.actionA.add(sign * (int) aAt);
									sign = 1;
									}
								}
							} else {
								String par = tCmd.substring(k);
								throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确																		// "u"。
							}

						}
						break;
					}
					case O:
					case o: {
						if (!cmdE.actionO.contains(1))
							cmdE.actionO.add(1); // 未有oint k = 1;
						int k=1;
						if(len > 1 && tCmd.charAt(1) == ':'){
							if( (tCmd.length() > 2 && tCmd.charAt(2)==':')){
								throw new CommandException(action.ExceptionEnum.WrongSwitch, tCmd.substring(k+1));
							}
							else{
								k++;
							}
						}
						int sign = 1;
						for (; k < len; k++) {
							// actionOEStr = "NSEDGnsedg-";
							char oAt = tCmd.charAt(k);

							if (actionOEStr.contains(Character.toString(oAt))) {
								if (oAt == '-') {
									if (sign == -1)
										throw new CommandException(action.ExceptionEnum.WrongParameter, tCmd.substring(k+1));
									
									sign = -1;
								}
								else {
									if(!cmdE.actionO.contains(-1*sign*(int)oAt) && !cmdE.actionO.contains(sign * (int) oAt)){ //先进优先
										cmdE.actionO.add(sign * (int) oAt);
									sign = 1;
									}
								}
							} 
							else{
								String par = tCmd.substring(k);
								throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确
																										// -
																										// "u"。
							}
						}
						break;
					}
					case T:
					case t: {
						// Wacw
						int k = 1;
						if(len > 1 && tCmd.charAt(1) == ':'){
							if( (tCmd.length() > 2 && tCmd.charAt(2)==':')){
								throw new CommandException(action.ExceptionEnum.WrongSwitch, tCmd.substring(k+1));
							}
							else{
								k++;
							}
						}
						for (; k < tCmd.length(); k++) {
							char tType = Character.toLowerCase(tCmd.charAt(k));
							if (Wacw.contains(Character.toString(tType))) {
								if (Wacw.indexOf(tType) > Wacw.indexOf(cmdE.modifyElements.get(0)))
									cmdE.modifyElements.set(0, tType);
							} else {
								String par = tCmd.substring(k);
								throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确
																										// -
																										// "u"。
							}
						}

						break;
					}
					default: {
						throw new CommandException(ExceptionEnum.ProgrammeMistack, null); // 编程出错
					}
					}
				} else {

					if (len > 1) { // 以下指令不带参数
						String par = tCmd.substring(0);
						throw new CommandException(action.ExceptionEnum.WrongParameter, par); // 参数格式不正确
																								// -
																								// "u"。
																								// substring(1)
					}
					// 利用枚举类型
					if(!actionStr.contains(Character.toString(ch))){
						String par = tCmd.substring(0);
						throw new CommandException(action.ExceptionEnum.WrongSwitch, par);
					}
					String chEnumElement = Character.toString(ch);
					CmdEnum ce = CmdEnum.valueOf(chEnumElement);

					switch (ce) {
					// 显示类型
					// showEStr = "bxdwnBXDWN"
					case B:
					case b: {
						// 0nwdxb
						cmdE.showType = 'b';
						break;
					}
					case D:
					case d: {
						if (showEStr.indexOf(cmdE.showType) < showEStr.indexOf('d')) // 0nwdxb
							cmdE.showType = 'd';
						break;
					}
					case W:
					case w: {
						if (showEStr.indexOf(cmdE.showType) < showEStr.indexOf('w')) // 0nwdxb
							cmdE.showType = 'w';
						break;
					}
					case N:
					case n: {
						if (showEStr.indexOf(cmdE.showType) < showEStr.indexOf('n')) // 0nwdxb
							cmdE.showType = 'n';
						break;
					}
					case X:
					case x: {
						if (showEStr.indexOf(cmdE.showType) < showEStr.indexOf('x')) // 0nwdxb
							cmdE.showType = 'x';
						break;
					}
					case C:
					case c: {
						if(cmdE.modifyElements.contains('d')){  //后进优先
							cmdE.actionA.remove((Character)'d');
						}
						break;
					}
					case L:
					case l: {
						if (!cmdE.modifyElements.contains((int) 'l')) {
							cmdE.modifyElements.add('l');
						}
						break;
					}
					case P:
					case p: {
						if (!cmdE.commandPS.contains('p'))
							cmdE.commandPS.add('p');
						break;
					}
					case Q:
					case q: {
						if (!cmdE.modifyElements.contains('q'))
							cmdE.modifyElements.add('q');
						break;
					}
					case S:
					case s: {
						if (!cmdE.commandPS.contains('s'))
							cmdE.commandPS.add('s');
						break;
					}
					default: {
						String par = tCmd.substring(0);
						throw new CommandException(ExceptionEnum.WrongParameter, par); // 参数格式不正确 -"u"。substring(1)
					}
					}
				}
			}
		}
	}
}
