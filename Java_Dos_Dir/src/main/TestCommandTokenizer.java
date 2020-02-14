package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import action.CommandElements;
import action.CommandException;
import action.CommandTokenizer;
import action.SearchObject;

public class TestCommandTokenizer {
	public static void main(String args[]){
		@SuppressWarnings("resource")
	    
		Scanner reader = new Scanner(System.in);
		String str[] = new String[2];
		for(int i=0;i<2;i++){
			str[i]=reader.next();
		}
		CommandElements cmdE = new CommandElements();
		ArrayList<SearchObject> searchObj = new ArrayList<SearchObject>();
		try {
			CommandTokenizer.commandTokenizer(str, cmdE, searchObj);
		} catch (CommandException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("searchObject 中的数据");
		Iterator<SearchObject> iter = searchObj.iterator();
		while(iter.hasNext()){
			SearchObject so = iter.next();
			System.out.println(so.fileName+"  "+so.filePath+"  "+so.isRegex);
		}
		System.out.println("action A 中的数据");
		Iterator<Integer> actionA = cmdE.actionA.iterator();
		while(actionA.hasNext()){
			int so = actionA.next();
			if(so < 0){
				so = -so;
				System.out.println("-"+(char)so);
			}
			else{
				System.out.println((char)so);
			}
		}
		System.out.println("action O 中的数据");
		Iterator<Integer> actionO = cmdE.actionO.iterator();
		while(actionO.hasNext()){
			int so = actionO.next();
			if(so < 0){
				so = -so;
				System.out.println("-"+(char)so);
			}
			else{
				System.out.println((char)so);
			}
		}
		System.out.println("commandPS 中的数据");
		Iterator<Character> pqs = cmdE.commandPS.iterator();
		while(pqs.hasNext()){
			char so = pqs.next();			
			System.out.println(so);
		}
		System.out.println("modifyElements 中的数据");
		Iterator<Character> me = cmdE.modifyElements.iterator();
		while(me.hasNext()){
			char so = me.next();			
			System.out.println(so);
		}
		
		System.out.println("showType : "+cmdE.showType);
	}
}
