package action;

import java.util.*;

public class CommandElements {				// /o-s 与  /os 同时出现，去先出现的
	public ArrayList<Integer> actionA;		//isEmpty(没有) size=1(第一个为1，仅有/a) size>1(含有其他属性) d(目录) r(只读) h（隐藏） a（存档） s（系统） i（索引） l（解析点）取负数表示-
	public ArrayList<Integer> actionO;		//isEmpty(没有) size=1(第一个为1，仅有/a) size>1(含有其他属性) n(名字) s(大小) e(扩展名) d(时间) g(组目录优先) 负数表示-
	public ArrayList<Character> modifyElements;	//isEmpty(没有) t(时间w>c>a) q(所有者) -c(大小数值表示方法,用d) l (名称小写)
	public ArrayList<Character> commandPS;		//pqs
	public char showType = '0';	//  b > x > d > w > n > -n >showType：0(直接输出目录) (用m表示) 用6~0标号等级
	public CommandElements(){
		actionA = new ArrayList<Integer>();
		actionO = new ArrayList<Integer>();
		modifyElements = new ArrayList<Character>();
		modifyElements.add((Character)'w');	//设定第0位置为/t属性 ,表示默认输出
		commandPS = new ArrayList<Character>();
		showType = '0';
	}
}
