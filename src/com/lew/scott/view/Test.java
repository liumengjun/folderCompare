package com.lew.scott.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Test {

	/**
	 * test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		final CompareFolderSelectDialog test = new CompareFolderSelectDialog(null);
//		test.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.out.println(test.getOldFolderPath());
//				System.out.println(test.getNewFolderPath());
//				System.exit(0);
//			}
//		});
//		test.setVisible(true);
		
		
		final SingleFolderSelectDialog test = new SingleFolderSelectDialog(null, "选择目的文件夹", "选择目的文件夹，文件将拷贝到该文件夹下");
		test.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println(test.getFolderPath());
				System.exit(0);
			}
		});
		test.setVisible(true);
	}

}
