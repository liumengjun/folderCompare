package com.lew.scott.compare;

/**
 * 包com.lew.scott.compare的测试类
 * @author Scott Lew
 *
 */
public class Test {
	
	//go test
	public static void main(String[] args) {
		
		try{
			//test，比较“*.fld文件”和“文件夹”
//			String oldTraverFileName = "test_folder.fld";
//			String newFolderName = "test_folder";
//			FolderCompare fc = FolderCompare.compareFolderWithTraverFile(oldTraverFileName, newFolderName);
//			
//			System.out.println("\n----------");
//			System.out.println("总共差异次数: "+fc.getDiffTimes()+" 次。");
			
			test_common_test_proj();
			
//			test_folderCompare_proj();
			
			//FileCopyTool.setOverExistsFile(false);
//			int copiedSize = FileCopyTool.copy("test_folder/and.you", "test_folder_2nd/and.you");
//			System.out.println(copiedSize);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test_common_test_proj() throws Exception{
		System.out.println("\n\n比较我的工程文件夹‘common_test’");
		//比较我的文件夹
		FolderCompare.setIgnoreFileNamePatterns("\\bin\\; \\pics\\; .class; mssqltestdb");
		FolderCompare fc = FolderCompare.compareFolder("F:\\WORKBACKUP\\common_test", "..\\common_test");
		
		System.out.println("\n----------");
		System.out.println(fc.toDescriptionString());
		System.out.println("总共差异次数: "+fc.getDiffTimes()+" 次。");
		
		//复制新增或修改的文件到 F:\\temp
		System.out.println("复制新增或修改的文件到 F:\\temp");
		fc.copyChangedFiles2Folder("F:\\temp");
		fc.copyNewAddedFiles2Folder("F:\\temp");
	}
	
	public static void test_folderCompare_proj() throws Exception{
		System.out.println("\n\n比较我的工程文件夹‘folderCompare’");
		//比较我的文件夹
		FolderCompare.setIgnoreFileNamePatterns("\\bin\\; Test.java; \\doc\\; test_folder; .classpath; .project; \\.settings\\");
		FolderCompare fc = FolderCompare.compareFolder("F:\\WORKBACKUP\\folderCompare", "..\\folderCompare");
		
		System.out.println("\n----------");
		System.out.println(fc.toDescriptionString());
		System.out.println("总共差异次数: "+fc.getDiffTimes()+" 次。");
		
		//复制新增或修改的文件到 F:\\temp
//		System.out.println("复制新增或修改的文件到 F:\\temp");
		fc.copyChangedFiles2Folder("F:\\temp");
		fc.copyNewAddedFiles2Folder("F:\\temp");
	}
}
