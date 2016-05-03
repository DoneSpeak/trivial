package main;

import java.io.*;
import java.util.*;

import action.Actions;
import action.CommandElements;
import action.CommandException;
import action.CommandTokenizer;
import action.SearchObject;
import action.ShowAction;
import action.SumOfFilesAndDirs;

public class Dir {

	public static void main(String args[]) {
		// 指令处理
		CommandElements cmdE = new CommandElements();
		ArrayList<SearchObject> searchObj = new ArrayList<SearchObject>();
/*
		try {
			CommandTokenizer.commandTokenizer(args, cmdE, searchObj);
		} catch (CommandException e) {
			System.out.println(e.getMessage());
			return;
		}
*/

		Scanner reader = new Scanner(System.in);
		String str[] = new String[2];
		for (int i = 0; i < 2; i++) {
			str[i] = reader.next();
		}
		try {
			CommandTokenizer.commandTokenizer(str, cmdE, searchObj);
		} catch (CommandException e) {
			System.out.printf(e.getMessage() + "\n");
			return;
		}
		
		// 对每个路径进行处理
		Iterator<SearchObject> iter = searchObj.iterator();
		while (iter.hasNext()) {
			SumOfFilesAndDirs sofd = new SumOfFilesAndDirs();
			SearchObject so = iter.next();
			ShowAction.DiskInfo(so.filePath);
			try {
				Actions.getInfomations(cmdE, so, sofd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
