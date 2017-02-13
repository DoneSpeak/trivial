[toc]
# 胡扯
&emsp;&emsp;这是一个很久以前就写了的代码，一直懒于相关的文档说明，所以一直一拖再拖，拖了很久。这个版本的说明还是比较简单的，如果整个张开来说的话，其实可以说上不少内容的。具体的源码可以到我的个人github上下载：https://github.com/DoneSpeak/java_Dos_Dir。

# 功能实现列表
<center>![这里写图片描述](http://img.blog.csdn.net/20170213195658479?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
#需求分析 
## 指令功能了解
&emsp;&emsp;通过help dir指令，我们可以查看dir指令的相关参数很选项以及对应的功能。当然为了更好的理解指令的的使用，我们还需要到网上去搜一些相关的讲解资料以及自己动手实际操作一遍。
<center>![dir基本功能目录](http://img.blog.csdn.net/20170213172409168?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
## 指令分类
&emsp;&emsp;为了更好的吃掉这一只大象，我们需要将其划分成更容易实现和理解的多个小部分。通过实际了解以及实际操作，我们可以将所有的选项划分如下：
|类型|指令|作用|指令间关系|
|---|---|---|---|
|筛选|/A[ D、R、H、A、S、I、L 、-D、-R、-H、-A、-S、-I、-L  ]|对指定目录中的目录及文件进行筛选|并列（可叠加）相反者以出现为准|
|排序|/O[N、S、E、D、G、-N、-S、-E、-D、-G]|对指定目录中的目录及文件进行排序|并列（可叠加）相反者以出现为准|
|修饰|/T[W、C、A] /L /Q /A[I、L]  /C|对显示内容的格式进行修改|大类并列（可叠加）T中属性：互斥（优先级从左到右）|
|显示|/B、/X、 /D、/W、/N、/-N|决定显示的方式|互斥（优先级从左到右，最低一级为默认显示，显示方式与/N相同）|
|其他|/P、/S|显示时的辅助功能|并列|
&emsp;&emsp;此外，以上不同类型选项之间均为并列关系。

## 指令组合功能
&emsp;&emsp;通过个人的实际使用，我将需要实现的功能又总结如下:
<table>
<tr><td>特点</td><td>描述</td><tr>
<tr><td>多指令操作</td><td>D:\TestFolder /a-h-r-s  /s  /d/w/x/n/oe-ns  /l/t:wca/q</td><tr>
<tr>
	<td>多种输入方式，且设置默认目录</td>
	<td>
		<ol>
			<li>支持大小写   Java Dir /A:h /oS</li>
			<li>无限定输入顺序 Java Dir /a /a:-h D:\TestFolder/o /n</li>	 
			<li>多种分隔符 Java Dir /a,/a:-h;D:\TestFolder/o;/n</li>	 
			<li>默认目录为user.home</li>
		</ol>
	</td>
<tr>
<tr><td>多路径操作</td><td>Java Dir D:\ D:\TestFolder D:\TestFolder\tttxt .p2
<tr><td>多重排序</td>
	<td>
		<ol>
			<li>Java Dir D:\TestFolder  /oesn</li>
			<li>Java Dir D:\TestFolder  /osen</li>
			<li>Java Dir D:\TestFolder  /ose-n</li>
		</ol>
	</td>
<tr>
<tr><td>文件检索	</td>
	<td>
		<ol>
			<li>准确检索  Java Dir D:\TestFolder\tdd.txt /s</li>
		    <li>正则表达式检索  Java Dir “D:\TestFolder\*.txt”</li>
		</ol>
	</td>
<tr>
<tr><td>异常处理</td><td>当用户输入一些错误的指令是，会输出异常信息。</td><tr>
<tr><td>显示帮助文件</td><td>当输入的内容中含有 /? 或者 /-- 同时这两个符号之前的命令均没有问题时，会输出帮助信息。<br/>
Java Dir /ahr/--/os D:\TestFolder<br />
Java Dir /?</td><tr>
</table>
#代码结构
## 整体结构
 &emsp;&emsp;指令输入主要包含三种信息：dir指令（包含选项）、检索目录或者文件。由于指令输入可以很长，比如：D:\TestFolder /a-h-r-s  /s  /d/w/x/n/oe-ns  /l/t:wca/q，该指令中包含了很多的组合信息，所以第一步我们需要对指令进行解析，解析出指令及相关选项，目录或者文件，以及判断出目录或者文件的字符串是否为正则表达式。解析完成之后，按照分类以及选项之间的相互关系对指令进行筛选，排序和显示（显示中会包含修饰）。
<center>![整体框架](http://img.blog.csdn.net/20170213175828173?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
## 详细结结构
&emsp;&emsp;代码需要运行在windows下的cmd界面中，所以输入的字符串为参数数组args[]。之后的流程是对以上整体结果进行细化。
<center>![详细结构](http://img.blog.csdn.net/20170213175928236?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
&emsp;&emsp;在处理完成之后，我们在显示之前进行了一次修饰，由于需要考虑显示类型选项之间的互斥关系，以及一些其他的只对部分显示选项才有效，所显示过程还有如下的流程关系。
<center>![comdE.modifyElements对显示进行修饰](http://img.blog.csdn.net/20170213180024125?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
## 几个重要的类
|类名|成员变量|描述|
|----|----------|----|
|CommandElements| ArrayList<Integer>  actionA、ArrayList<Integer> actionO、ArrayList<Character> commandPS、ArrayList<Character> modifyElements、char showType|保存命令及其命令选项|
|SearchObject|String fileName、String filePath、boolean isRegex|保存路径及文件名，并记录是否为正则表达式|
|FileInfo|String date、String size、String type、String longname、String shortname、String owner|	保存需要输出的数据对象，用于显示时的存储格式化后的字符串|
#具体实现 
## 指令解析
```java
CommandTokenizer. commandTokenizer
(String[] source, CommandElements cmdE, ArrayList<SearchObject> searchObj)
```
&emsp;&emsp;指令解析主要由类CommandTokenizer的commandTokenizer方法实现。source为输入的指令字符串，参数cmdE和searchObj为解析之后结果。
<center>![指令解析](http://img.blog.csdn.net/20170213192306838?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
<center>指令解析</center>
## 选项a
**重点：获取文件的属性**
为了获取到文件的属性，我们会使用到`java.io.File`，但是我们知道，该类能够获取到的文件属性非常的少，因而这里我们需要使用到`java.nio.file.*`类。相关部分api 可以看java官网的文档 http://docs.oracle.com/javase/8/docs/api/ 或者自己查找相关资料。

## 选项o
**重点：排序、多重排序**
### 基本代码结构 
如下仅展示部分代码，其他的排序处理方法也如此。
```java
//Actions.actionO
//如下代码中FileComparator_DirFFileL是一个继承了接口Comparator的类，该类规定文件及目录按照目录在文件前面的方式排序
ArrayList<File> fileList = fileListList.get(0);
FileComparator_DirFFileL fc = new FileComparator_DirFFileL();
fileList.sort(fc);

···

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
```
### 多重排序的原理 
&emsp;&emsp;不难注意到，我们这里有两个特别的数据结构`ArrayList<ArrayList<File>>`和`ArrayList<File>`，对应下图就是`ArrayList<ArrayList<File>>`为绿色部分，而`ArrayList<File>`对应蓝色部分。
&emsp;&emsp;我们以指令`Java Dir D:\TestFolder  /oes`为例：开始的时候，所有的文件没有进行任何规则的排序处理，所以我们可以理解为其所有的文件都是一样的，之后进行按照拓展名进行升序排序，这时候中间部分的每个蓝色的矩形，也就是一个的`ArrayList<File>`。每个这样的矩形都是具有相同的拓展名。之后在按照大小进行升序排序，也就得到了更加密的小矩形，这时的每个小矩形都是具有相同的文件拓展名和相同的大小。通过这种巧妙的方式，我们就便可以在保障数据结构（保障相同的数据结构可以保障能够使用相类似的数据处理过程）的前提下实现多重排序。

<center>![多重排序原理](http://img.blog.csdn.net/20170213193413440?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</center>
<center>多重排序原理</center>

排序操作的O选项代码如下，其中省略部分为结果相类似的其他排序操作。
```java
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
		
		···
		
	return fileList;
}
```
排序规则结果继承类。
```java
class FileComparator_ReName implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		if(f1.getName().equals(f2.getName()))
			return 1;
		return f2.compareTo(f1);
	}
}
```
排序函数
```java
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
```
## 修饰类 -/T[W、C、A] /L /Q /A[I、L]  /C
**重点：字符长的格式化**
显示修饰中，部分选项需要获取一些属性或者对字符串进行格式化。 

* 获取文件上次写入时间 /tw 
* 获取文件创建时间 /tc
* 获取文件上次访问时间 /ta
* 获取文件所有者 /q
* 获取所有者
* 判断文件类型
* 数值格式化
* 消除拓展名

![不同显示方式](http://img.blog.csdn.net/20170213200519591?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 异常处理
**重点：重载Exception类**

![异常显示](http://img.blog.csdn.net/20170213200627764?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
```java
//使用enum使得代码更加简洁
enum ExceptionEnum {
	WrongParameter, 
	WrongSwitch, 
	ProgrammeMistack, 
	PrintHelpInfo , 
	GrammaticalMistake, 
	IsCannotIdentifyDevice, 
	FileCannotFind
}

@SuppressWarnings("serial")
public class CommandException extends Exception{
	
	String message;
	
	public CommandException(ExceptionEnum exEnum,String parameter){
		if(exEnum.equals(ExceptionEnum.WrongSwitch)){
			message = "无效开关 - \""+parameter+"\"。";
		}
		else if(exEnum.equals(ExceptionEnum.WrongParameter)){
			message = "参数格式不正确 - \""+parameter+"\"。";
		}
		else if(exEnum.equals(ExceptionEnum.ProgrammeMistack)){
			message = "开发者编程出错！";
		}
		else if(exEnum.equals(ExceptionEnum.PrintHelpInfo)){
			message = "···"; //这里省略了帮助的信息
		}
		else if(exEnum.equals(ExceptionEnum.GrammaticalMistake)){
			message = "文件名、目录名或卷标语法不正确。";
		}
		else if(exEnum.equals(ExceptionEnum.IsCannotIdentifyDevice)){
			message = "\""+parameter + "\" 是无法识别设备。";
		}
		else if(exEnum.equals(ExceptionEnum.FileCannotFind)){
			message = "找不到文件";
		}
	}
	@Override
	public String getMessage(){
		return message;
	}
}

```

#TestCommandTokenizer
&emsp;&emsp;由于我的程序主要依赖于CommandTokenizer处理的来的信息（包括dir指令，检索路径和检索文件名），因此CommandTokenizer的正确运行是我后续运行的保障，所以我专门写了这么一个含有主函数的类来检查我输入的内容经CommandTonizer处理后得到的信息是否正确。
&emsp;&emsp;举例如下：
![TestCommandTokenizer](http://img.blog.csdn.net/20170213201347101?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvRG9uZVNwZWFr/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

searchObjext中的数据是：
1. 检索文件名（是正则表达式的已转为正则表达式），文件路径，文件名是否为正则表达式
2. actionA和actionO中是他们的属性，笑脸（在eclipse中是小方格）表示含有/A或 /O 这个指令。
