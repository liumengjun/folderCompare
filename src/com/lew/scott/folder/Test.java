package com.lew.scott.folder;

/**
 * 包com.lew.scott.folder的测试类
 * @author Scott Lew
 *
 */
public class Test {

	//go test
	public static void main(String[] args){
		String folder_name = "test_folder";
		if(args.length==1){
			folder_name = args[0];
		}
		
		//遍历文件夹测试
		/*
		try{
			FolderTraversal ft = FolderTraversal.traverse(folder_name);
			ft.printFolderTraversal(null);
			//ft.outputFolderTraversal(folder_name+".fld");
		}catch (Exception e) {
			e.printStackTrace();
		}
		//*/
		
		System.out.println("\n----------\n");
		
		//读取文件夹遍历结果文件测试
		/*
		try{
			FolderTraversal ft = FolderTraversal.initFromFile(folder_name+".fld");
			ft.printFolderTraversal(null);
		}catch (Exception e) {
			e.printStackTrace();
		}
		//*/
		
		//遍历当前工程文件夹
		folder_name = "../folderCompare";
		try{
			FolderTraversal ft = FolderTraversal.traverse(folder_name);
			FolderTraversal.FILE_NAME_PAD_LEN = 80;
			ft.printFolderTraversal(null);
			ft.outputFolderTraversal("folderCompare"+".fld");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
