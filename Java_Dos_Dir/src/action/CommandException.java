package action;

enum ExceptionEnum {
	WrongParameter, WrongSwitch, ProgrammeMistack, PrintHelpInfo , GrammaticalMistake, IsCannotIdentifyDevice, FileCannotFind
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
			message = "显示目录中的文件和子目录列表。\n\nDIR [drive:][path][filename] [/A[[:]attributes]] [/B] [/C] [/D] [/L] [/N]\n[/O[[:]sortorder]] [/P] [/Q] [/R] [/S] [/T[[:]timefield]] [/W] [/X] [/4]\n\n  [drive:][path][filename]\n              指定要列出的驱动器、目录和/或文件。\n\n  /A          显示具有指定属性的文件。\n  属性         D  目录                R  只读文件\n               H  隐藏文件            A  准备存档的文件\n               S  系统文件            I  无内容索引文件\n               L  解析点             -  表示“否”的前缀\n  /B          使用空格式(没有标题信息或摘要)。\n  /C          在文件大小中显示千位数分隔符。这是默认值。用 /-C 来\n              禁用分隔符显示。\n  /D          跟宽式相同，但文件是按栏分类列出的。\n  /L          用小写。\n  /N          新的长列表格式，其中文件名在最右边。\n  /O          用分类顺序列出文件。\n  排列顺序     N  按名称(字母顺序)     S  按大小(从小到大)\n               E  按扩展名(字母顺序)   D  按日期/时间(从先到后)\n               G  组目录优先           -  反转顺序的前缀\n  /P          在每个信息屏幕后暂停。\n  /Q          显示文件所有者。\n  /R          显示文件的备用数据流。\n  /S          显示指定目录和所有子目录中的文件。\n  /T          控制显示或用来分类的时间字符域。\n  时间段      C  创建时间\n              A  上次访问时间\n              W  上次写入的时间\n  /W          用宽列表格式。\n  /X          显示为非 8.3 文件名产生的短名称。格式是 /N 的格式，\n              短名称插在长名称前面。如果没有短名称，在其位置则\n              显示空白。\n  /4          用四位数字显示年\n \n可以在 DIRCMD 环境变量中预先设定开关。通过添加前缀 - (破折号)\n来替代预先设定的开关。例如，/-W。\n";
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
