package com.lew.scott.folder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 文件夹遍历类 指定一个文件夹，递归遍历所有文件，保存到一个treemap列表，参考{@link FilePropertyMini}
 * 
 * @author Scott Lew
 * 
 */
public class FolderTraversal {

	public static int FILE_NAME_PAD_LEN = 80;
	public static int FILE_SIZE_PAD_LEN = 10;
	public static final boolean ALSO_LIST_DIR = true;// 遍历时，也列出文件夹，暂时只是默认为true
	// public static boolean CONTAIN_FOLDER_NAME = true;//文件列表名字包含文件夹短名

	/**
	 * a sorted map for hold the file name as the key ,and the referred file
	 * object sorted by the file name
	 */
	private TreeMap<String, FilePropertyMini> folderFilesMap;// 按文件名排序的文件属性记录map
	private String folderName;// 文件夹短名
	private String parentPath;// 文件夹所在目录的绝对路径

	/**
	 * 不可以创建该类的对象
	 */
	private FolderTraversal() {
	}

	/**
	 * 静态调用，开始遍历一个目录，并返回一个FolderTraversal实例对象
	 * 
	 * @param folderName
	 *            需要遍历的文件夹名字
	 * @return FolderTraversal实例对象
	 * @throws Exception
	 */
	public static FolderTraversal traverse(String folderName) throws Exception {
		FolderTraversal folderTraver = new FolderTraversal();
		folderTraver.traverseFolder(folderName);
		return folderTraver;
	}

	/**
	 * 遍历文件夹 必须是文件夹，如果只有一个文件，也没有必要遍历
	 * 
	 * @param foldName
	 *            文件夹name
	 */
	private void traverseFolder(String folderName) throws Exception {
		if (folderName == null) {
			throw new IllegalArgumentException("文件夹name不能为null");
		}

		File cur_dir = new File(folderName);
		if (!cur_dir.exists() && !cur_dir.isDirectory()) {
			throw new IllegalAccessException("文件夹不存在");
		}
		// 得到文件夹的short name
		this.folderName = cur_dir.getName();
		String absolutePath = cur_dir.getCanonicalPath();// 返回抽象路径名的规范路径名字符串。(规范路径名是绝对路径名，并且是惟一的。规范路径名的准确定义与系统有关。)
		char sepaChar = File.separatorChar;
		// 文件夹上级路径全名
		this.parentPath = absolutePath.substring(0, absolutePath.lastIndexOf(sepaChar));

		// 检测遍历结果map
		if (folderFilesMap == null) {
			folderFilesMap = new TreeMap<String, FilePropertyMini>(String.CASE_INSENSITIVE_ORDER);
		} else {
			folderFilesMap.clear();
		}
		// 检测目录名
		String dir_name = "";
		// if(CONTAIN_FOLDER_NAME){
		dir_name = this.folderName;
		// }
		// 开始遍历
		traverseFolderInner(cur_dir, dir_name);
	}

	/**
	 * 遍历目录 inner方法
	 * 
	 * @param cur_dir
	 *            当前文件夹
	 * @param cur_dir_name
	 *            当前文件夹名字
	 */
	private void traverseFolderInner(File cur_dir, String cur_dir_name) throws Exception {
		// 获取目录下文件列表
		File[] files_list = cur_dir.listFiles();

		// 遍历目录
		for (int i = 0; i < files_list.length; i++) {
			File cur_file = files_list[i];
			// 拼接当前文件的名字（包含上级文件夹名字）
			String cur_file_name = cur_dir_name + File.separator + cur_file.getName();
			if (cur_file.isDirectory()) {// 得到的新子文件夹
				if (ALSO_LIST_DIR) {// 在列表中记住文件夹
					folderFilesMap.put(cur_file_name + File.separator, new FilePropertyMini(cur_file));
				}
				// 递归子目录
				traverseFolderInner(cur_file, cur_file_name);
				continue;
			}

			// 将《文件名，文件》放入到folderFilesMap
			folderFilesMap.put(cur_file_name, new FilePropertyMini(cur_file));
		}
	}

	/**
	 * folderFilesMap 的getter方法
	 * 
	 * @return folderFilesMap
	 */
	public TreeMap<String, FilePropertyMini> getFolderFilesMap() {
		return folderFilesMap;
	}

	/**
	 * 文件夹短名
	 * 
	 * @return 文件夹短名
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * 文件夹所在目录的绝对路径
	 * 
	 * @return 文件夹所在目录的绝对路径
	 */
	public String getParentPath() {
		return parentPath;
	}

	/**
	 * 输出遍历得到的folderFilesMap 用DataOutputStream对象输出到文件
	 * 
	 * @param outFileName
	 *            输出的文件名
	 * @throws Exception
	 */
	public void outputFolderTraversal(String outFileName) throws Exception {
		outputFolderTraversal(new FileOutputStream(new File(outFileName)));
	}

	/**
	 * 输出遍历得到的folderFilesMap 用DataOutputStream对象输出到文件
	 * 
	 * @param fileOutStream
	 *            文件输出流
	 * @throws Exception
	 */
	private void outputFolderTraversal(FileOutputStream fileOutStream) throws Exception {
		if (folderFilesMap == null) {
			throw new IllegalStateException("还没有遍历完目录");
		}
		if (fileOutStream == null) {
			throw new IllegalArgumentException("没有指定任何outputstream");
		}

		// 构造output
		DataOutputStream outer = new DataOutputStream(fileOutStream);
		outer.writeUTF(this.parentPath + File.separatorChar + this.folderName + '\n');

		// 开始遍历folderFilesMap
		Iterator<Map.Entry<String, FilePropertyMini>> filesItr = folderFilesMap.entrySet().iterator();
		while (filesItr.hasNext()) {
			Map.Entry<String, FilePropertyMini> fileEntry = filesItr.next();
			String fileName = fileEntry.getKey();
			FilePropertyMini fileProp = fileEntry.getValue();
			outer.writeUTF(fileName);// 文件名
			outer.writeBoolean(fileProp.isFolder());// 是否是文件夹
			outer.writeLong(fileProp.getFileSize());// 文件长度
			outer.writeLong(fileProp.getLastModiTime());// 文件最后修改时间
			outer.writeChar('\n');
		}

		outer.close();
	}

	/**
	 * 输出遍历得到的folderFilesMap 默认用标准输出，输出到控制台，此时须指定printer为null
	 * 
	 * @param printer
	 *            输出流，printer为null是更为System.out（标准输出）
	 * @throws Exception
	 */
	public void printFolderTraversal(PrintStream printer) throws Exception {
		if (folderFilesMap == null) {
			throw new IllegalStateException("还没有遍历完目录");
		}
		if (printer == null) {
			printer = System.out;
		}

		// 输出路径名
		printer.println(this.parentPath + File.separatorChar + this.folderName);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 开始遍历folderFilesMap
		Iterator<Map.Entry<String, FilePropertyMini>> filesItr = folderFilesMap.entrySet().iterator();
		StringBuffer propBuf = new StringBuffer(256);
		while (filesItr.hasNext()) {
			Map.Entry<String, FilePropertyMini> fileEntry = filesItr.next();
			String fileName = fileEntry.getKey();
			FilePropertyMini fileProp = fileEntry.getValue();
			// 输出各个文件属性：文件名 大小 上次修改时间
			propBuf.setLength(0);
			// 文件名
			propBuf.append(fileName).append(space(FILE_NAME_PAD_LEN - fileName.getBytes().length));// .append('|');

			// 是文件才输入文件大小
			if (fileProp.isFolder()) {
				propBuf.append(space(FILE_SIZE_PAD_LEN + 5));
			} else {
				long cur_file_size = fileProp.getFileSize();
				propBuf.append(space(FILE_SIZE_PAD_LEN - String.valueOf(cur_file_size).length())).append(cur_file_size)
						.append("B    ");
			}
			// 文件最后修改时间
			Date last_modi_time = new Date(fileProp.getLastModiTime());
			propBuf.append(dateFormat.format(last_modi_time));

			// 输出一个文件数据
			printer.println(propBuf.toString());
		}
	}

	/**
	 * 输出遍历得到的folderFilesMap输出到文件
	 * 
	 * @param textFileName
	 * @throws Exception
	 */
	public void printToFile(String textFileName) throws Exception {
		PrintStream printer = new PrintStream(new File(textFileName));
		this.printFolderTraversal(printer);
	}

	/**
	 * 把遍历得到的folderFilesMap转换为字符串
	 * 
	 * @return 遍历结果描述字符串
	 * @throws Exception
	 */
	public String toDescriptionString() throws Exception {
		if (folderFilesMap == null) {
			throw new IllegalStateException("还没有遍历完目录");
		}
		StringBuffer descriptionBuf = new StringBuffer(4096);

		// 路径名
		descriptionBuf.append("文件夹 \"" + this.parentPath + File.separatorChar + this.folderName).append("\" 的文件列表\n");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 开始遍历folderFilesMap
		Iterator<Map.Entry<String, FilePropertyMini>> filesItr = folderFilesMap.entrySet().iterator();
		StringBuffer propBuf = new StringBuffer(256);
		while (filesItr.hasNext()) {
			Map.Entry<String, FilePropertyMini> fileEntry = filesItr.next();
			String fileName = fileEntry.getKey();
			FilePropertyMini fileProp = fileEntry.getValue();
			// 输出各个文件属性：文件名 大小 上次修改时间
			propBuf.setLength(0);
			// 文件名
			propBuf.append(fileName).append(space(FILE_NAME_PAD_LEN - fileName.getBytes().length));// .append('|');

			// 是文件才输入文件大小
			if (fileProp.isFolder()) {
				propBuf.append(space(FILE_SIZE_PAD_LEN + 5));
			} else {
				long cur_file_size = fileProp.getFileSize();
				propBuf.append(space(FILE_SIZE_PAD_LEN - String.valueOf(cur_file_size).length())).append(cur_file_size)
						.append("B    ");
			}
			// 文件最后修改时间
			Date last_modi_time = new Date(fileProp.getLastModiTime());
			propBuf.append(dateFormat.format(last_modi_time));

			// 输出一个文件数据
			descriptionBuf.append(propBuf.toString()).append("\n");
		}

		return descriptionBuf.toString();
	}

	/**
	 * 生成长度为length的空格字符串
	 * 
	 * @param length
	 * @return
	 */
	private String space(int length) {
		StringBuffer spaceBuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			spaceBuf.append(' ');
		}
		return spaceBuf.toString();
	}

	/**
	 * 从文件中恢复FolderTraversal示例对象
	 * 
	 * @param folderTraverFileName
	 * @return FolderTraversal实例对象
	 */
	public static FolderTraversal initFromFile(String folderTraverFileName) throws Exception {
		FolderTraversal folderTraver = new FolderTraversal();

		DataInputStream input = new DataInputStream(new FileInputStream(new File(folderTraverFileName)));

		// 读取文件路径
		String fullPath = input.readUTF();
		// 注意input.readUTF()把第一行最后的换行符(\n)读到了fullPath变量里了
		int indexOfSepa = fullPath.lastIndexOf(File.separatorChar);
		if (indexOfSepa < 0) {
			input.close();
			throw new IllegalArgumentException("错误的文件名，不是FolderTraversal遍历属性文件");
		}
		folderTraver.folderName = fullPath.substring(indexOfSepa + 1, fullPath.length() - 1);
		folderTraver.parentPath = fullPath.substring(0, indexOfSepa);

		folderTraver.folderFilesMap = new TreeMap<String, FilePropertyMini>(String.CASE_INSENSITIVE_ORDER);
		// 读取文件夹各个文件属性
		while (input.available() > 0) {
			String fileName = input.readUTF();
			boolean isFolder = input.readBoolean();
			long fileSize = input.readLong();
			long lastModiTime = input.readLong();
			input.readChar();
			FilePropertyMini fileProp = new FilePropertyMini(isFolder, fileSize, lastModiTime);
			folderTraver.folderFilesMap.put(fileName, fileProp);
		}

		return folderTraver;
	}
}
