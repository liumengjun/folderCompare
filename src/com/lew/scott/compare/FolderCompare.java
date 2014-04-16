package com.lew.scott.compare;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.lew.scott.folder.FilePropertyMini;
import com.lew.scott.folder.FolderTraversal;

/**
 * 文件夹比较封装 利用两个{@link FolderTraversal}对象比较两个文件夹，以新旧之分，得到3中列表：丢失的文件，新增加的文件，修改了的文件
 * 
 * @author Scott Lew
 * 
 */
public class FolderCompare {

	private FolderTraversal oldFolderTraversal;// 原来的文件夹信息
	private FolderTraversal newFolderTraversal;// 被更改过的新的文件夹信息

	private TreeMap<String, FilePropertyMini> lostFilesMap;// 丢失的文件记录
	private TreeMap<String, FilePropertyMini> newAddedFilesMap;// 新增加的文件记录
	private TreeMap<String, FilePropertyMini> changedFilesMap;// 被修改了的文件记录

	private int diffTimes;// 记录总的差异次数

	private static String ignoreFileNamePatterns;// 排除要比较的文件的文件名字包含的字符串,可设定多种，以分号(;)分隔
	private static boolean trimIgnorePatterns = true;

	/**
	 * 不可以创建该类的对象
	 */
	private FolderCompare() {
	}

	/**
	 * 总的差异次数のgetter方法
	 * 
	 * @return 总的差异次数
	 */
	public int getDiffTimes() {
		return diffTimes;
	}

	/**
	 * 获取旧的文件夹的文件夹遍历对象（see {@link FolderTraversal}）
	 * 
	 * @return 旧的文件夹的遍历对象
	 */
	public FolderTraversal getOldFolderTraversal() {
		return oldFolderTraversal;
	}

	/**
	 * 获取新的文件夹的文件夹遍历对象（see {@link FolderTraversal}）
	 * 
	 * @return 新的文件夹的遍历对象
	 */
	public FolderTraversal getNewFolderTraversal() {
		return newFolderTraversal;
	}

	/**
	 * 获取比较结果中的“丢失的文件”列表集
	 * 
	 * @return 丢失的文件结果集
	 */
	public TreeMap<String, FilePropertyMini> getLostFilesMap() {
		return lostFilesMap;
	}

	/**
	 * 获取比较结果中的“新增的文件”列表集
	 * 
	 * @return 新增的文件结果集
	 */
	public TreeMap<String, FilePropertyMini> getNewAddedFilesMap() {
		return newAddedFilesMap;
	}

	/**
	 * 获取比较结果中的“修改的文件”列表集
	 * 
	 * @return 修改的文件结果集
	 */
	public TreeMap<String, FilePropertyMini> getChangedFilesMap() {
		return changedFilesMap;
	}

	/**
	 * 设定排除要比较的文件的文件名字包含的字符串，可设定多种，以分号(;)分隔<br/> &nbsp;&nbsp;如: "\bin\; .bak"<br/>
	 * 默认忽略每个段字符串两头的空格，若不想忽略，请参考方法{@link #setWhetherTrimIgnorePattern}<br/>
	 * 
	 * @param patternStrs
	 */
	public static void setIgnoreFileNamePatterns(String patternStrs) {
		ignoreFileNamePatterns = patternStrs;
	}

	/**
	 * 设定是否trim()在方法ignoreFileNameSubStrings()设定的每个字符串 trim即，每个字符串两头的空格
	 * 
	 * @param flag
	 */
	public static void setWhetherTrimIgnorePattern(boolean flag) {
		trimIgnorePatterns = flag;
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少
	 * 
	 * @param oldFolderName
	 *            旧文件夹名字
	 * @param newFolderName
	 *            新文件夹名字
	 * @throws Exception
	 */
	public static FolderCompare compareFolder(String oldFolderName, String newFolderName) throws Exception {
		FolderTraversal oldFolderTraversal = FolderTraversal.traverse(oldFolderName);
		FolderTraversal newFolderTraversal = FolderTraversal.traverse(newFolderName);
		return compareFolderTraversal(oldFolderTraversal, newFolderTraversal);
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少
	 * 
	 * @param oldTraverFileName
	 *            旧文件夹遍历后得到的文件（traversal file）名字
	 * @param newTraverFileName
	 *            新文件夹遍历后得到的文件（traversal file）名字
	 * @throws Exception
	 */
	public static FolderCompare compareFolderTraverFile(String oldTraverFileName, String newTraverFileName)
			throws Exception {
		FolderTraversal oldFolderTraversal = FolderTraversal.initFromFile(oldTraverFileName);
		FolderTraversal newFolderTraversal = FolderTraversal.initFromFile(newTraverFileName);
		return compareFolderTraversal(oldFolderTraversal, newFolderTraversal);
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少
	 * 
	 * @param oldTraverFileName
	 *            旧文件夹遍历后得到的文件（traversal file）名字
	 * @param newFolderName
	 *            新文件夹名字
	 * @throws Exception
	 */
	public static FolderCompare compareFolderWithTraverFile(String oldTraverFileName, String newFolderName)
			throws Exception {
		FolderTraversal oldFolderTraversal = FolderTraversal.initFromFile(oldTraverFileName);
		FolderTraversal newFolderTraversal = FolderTraversal.traverse(newFolderName);
		return compareFolderTraversal(oldFolderTraversal, newFolderTraversal);
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少
	 * 
	 * @param oldFolderName
	 *            旧文件夹名字
	 * @param newTraverFileName
	 *            新文件夹遍历后得到的文件（traversal file）名字
	 * @throws Exception
	 */
	public static FolderCompare compareTraverFileWithFolder(String oldFolderName, String newTraverFileName)
			throws Exception {
		FolderTraversal oldFolderTraversal = FolderTraversal.traverse(oldFolderName);
		FolderTraversal newFolderTraversal = FolderTraversal.initFromFile(newTraverFileName);
		return compareFolderTraversal(oldFolderTraversal, newFolderTraversal);
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少 用两个FolderTraversal对象来比较
	 * 
	 * @param oldFolderTraver
	 * @param newFolderTraver
	 */
	protected static FolderCompare compareFolderTraversal(FolderTraversal oldFolderTraver,
			FolderTraversal newFolderTraver) {
		FolderCompare folderCmp = new FolderCompare();
		folderCmp.oldFolderTraversal = oldFolderTraver;
		folderCmp.newFolderTraversal = newFolderTraver;
		folderCmp.compareFolder();
		return folderCmp;
	}

	/**
	 * 比较两个文件夹，得到new folder比old folder变化了多少 默认参数为private FolderTraversal 对象
	 */
	private void compareFolder() {
		// 得到待比较的文件夹名字
		String oldFolderName = this.oldFolderTraversal.getFolderName();
		String newFolderName = this.newFolderTraversal.getFolderName();
		// System.out.println("比较文件夹：[" +
		// this.oldFolderTraversal.getParentPath() + File.separator +
		// oldFolderName + "]"
		// + "与[" + this.newFolderTraversal.getParentPath() + File.separator +
		// newFolderName + "]");
		// System.out.println("... ...");

		// 得到遍历新旧的文件夹的folderFilesMap
		TreeMap<String, FilePropertyMini> oldFolderFilesMap = this.oldFolderTraversal.getFolderFilesMap();
		TreeMap<String, FilePropertyMini> newFolderFilesMap = this.newFolderTraversal.getFolderFilesMap();

		// （0）获得忽略比较的文件的名字包含的字符串列表
		String[] ignoreFileNamePtnStrArr = null;
		if (ignoreFileNamePatterns != null && !ignoreFileNamePatterns.trim().equals("")) {
			String[] temp = ignoreFileNamePatterns.split(";");
			if (trimIgnorePatterns) {// trim()整理
				ArrayList<String> strArr = new ArrayList<String>(temp.length);
				for (int pp = 0; pp < temp.length; pp++) {
					temp[pp] = temp[pp].trim();
					if (!temp[pp].equals("")) {
						strArr.add(temp[pp]);
					}
				}
				if (strArr.size() > 0) {
					ignoreFileNamePtnStrArr = new String[strArr.size()];
					strArr.toArray(ignoreFileNamePtnStrArr);
				}
			} else {// 不整理
				ignoreFileNamePtnStrArr = temp;
			}
		}

		this.diffTimes = 0;// 初始总的差异次数为0

		// （1）遍历新文件夹文件列表对象，得到新增加的文件记录
		// System.out.println("\n----------新增加的文件（夹）列表----------");
		// 初始化
		if (newAddedFilesMap == null) {
			newAddedFilesMap = new TreeMap<String, FilePropertyMini>(String.CASE_INSENSITIVE_ORDER);
		} else {
			newAddedFilesMap.clear();
		}
		Iterator<Map.Entry<String, FilePropertyMini>> newFolderFilesItr = newFolderFilesMap.entrySet().iterator();
		while (newFolderFilesItr.hasNext()) {
			// 新文件夹中的file
			Map.Entry<String, FilePropertyMini> newFileEntry = newFolderFilesItr.next();
			String newFileName = newFileEntry.getKey();
			String newNameNoPrefix = newFileName.substring(newFileName.indexOf(File.separator));

			// 检测是否忽略
			boolean escape = false;
			for (int pp = 0; ignoreFileNamePtnStrArr != null && pp < ignoreFileNamePtnStrArr.length; pp++) {
				if (newNameNoPrefix.contains(ignoreFileNamePtnStrArr[pp])) {
					escape = true;
					break;// 跳出for小循环
				}
			}
			if (escape) {
				continue;// (新文件判断while循环)忽略这个新文件判断，继续下个文件判断
			}

			// 旧文件夹中没有，则是新增加的
			String oldFileNameKey = oldFolderName + newNameNoPrefix;
			if (!oldFolderFilesMap.containsKey(oldFileNameKey)) {
				FilePropertyMini newFileProp = newFileEntry.getValue();
				this.newAddedFilesMap.put(newFileName, newFileProp);
				this.diffTimes++;
				// System.out.println("新增文件" + (newFileProp.isFolder() ? "夹" :
				// "") + "：{" + newFileName + "}");
			}
		}
		if (this.newAddedFilesMap.size() == 0) {
			// System.out.println("没有新添加文件。");
		}

		// （2）遍历旧文件夹文件列表对象，得到丢失的文件记录
		// System.out.println("\n----------丢失的文件（夹）列表----------");
		// 初始化
		if (lostFilesMap == null) {
			lostFilesMap = new TreeMap<String, FilePropertyMini>(String.CASE_INSENSITIVE_ORDER);
		} else {
			lostFilesMap.clear();
		}
		Iterator<Map.Entry<String, FilePropertyMini>> oldFolderFilesItr = oldFolderFilesMap.entrySet().iterator();
		while (oldFolderFilesItr.hasNext()) {
			// 旧文件夹中的file
			Map.Entry<String, FilePropertyMini> oldFileEntry = oldFolderFilesItr.next();
			String oldFileName = oldFileEntry.getKey();
			String oldNameNoPrefix = oldFileName.substring(oldFileName.indexOf(File.separator));

			// 检测是否忽略
			boolean escape = false;
			for (int pp = 0; ignoreFileNamePtnStrArr != null && pp < ignoreFileNamePtnStrArr.length; pp++) {
				if (oldNameNoPrefix.contains(ignoreFileNamePtnStrArr[pp])) {
					escape = true;
					break;// 跳出for小循环
				}
			}
			if (escape) {
				continue;// (旧文件判断while循环)忽略这个旧文件判断，继续下个文件判断
			}

			// 新文件夹中没有，则是丢失的
			String newFileNameKey = newFolderName + oldNameNoPrefix;
			if (!newFolderFilesMap.containsKey(newFileNameKey)) {
				FilePropertyMini oldFileProp = oldFileEntry.getValue();
				this.lostFilesMap.put(oldFileName, oldFileProp);
				this.diffTimes++;
				// System.out.println("丢失文件" + (oldFileProp.isFolder() ? "夹" :
				// "") + "：{" + oldFileName + "}");
			}
		}
		if (this.lostFilesMap.size() == 0) {
			// System.out.println("没有丢失任何文件。");
		}

		// （3）开始遍历比较，得到修改的文件记录列表
		// System.out.println("\n----------被修改的文件（夹）列表----------");
		// 初始化
		if (changedFilesMap == null) {
			changedFilesMap = new TreeMap<String, FilePropertyMini>(String.CASE_INSENSITIVE_ORDER);
		} else {
			changedFilesMap.clear();
		}
		oldFolderFilesItr = oldFolderFilesMap.entrySet().iterator();
		newFolderFilesItr = newFolderFilesMap.entrySet().iterator();
		boolean finish = false;
		while (oldFolderFilesItr.hasNext() && newFolderFilesItr.hasNext()) {
			// （3.1）旧文件夹中的file
			Map.Entry<String, FilePropertyMini> oldFileEntry = oldFolderFilesItr.next();
			String oldFileName = oldFileEntry.getKey();
			// 跳过丢失的文件，和设定忽略的文件（use loop）
			while (true) {
				boolean lostFlag = false;
				boolean ignoreFlag = false;
				// 检测是否忽略
				for (int pp = 0; ignoreFileNamePtnStrArr != null && pp < ignoreFileNamePtnStrArr.length; pp++) {
					if (oldFileName.contains(ignoreFileNamePtnStrArr[pp])) {
						ignoreFlag = true;
						break;// 跳出for小循环
					}
				}
				if (!ignoreFlag) {
					// 检测是否丢失
					lostFlag = lostFilesMap.containsKey(oldFileName);
				}
				if (lostFlag || ignoreFlag) {
					if (oldFolderFilesItr.hasNext()) {
						oldFileEntry = oldFolderFilesItr.next();
						oldFileName = oldFileEntry.getKey();
					} else {
						finish = true;
						break;
					}
				} else {
					break;
				}
			}
			if (finish) {
				break;
			}
			// 剔除文件夹短名
			String oldNameNoPrefix = oldFileName.substring(oldFileName.indexOf(File.separator));

			// （3.2）新文件夹中的file
			Map.Entry<String, FilePropertyMini> newFileEntry = newFolderFilesItr.next();
			String newFileName = (String) newFileEntry.getKey();
			// 跳过新增的文件，和设定忽略的文件（use loop）
			while (true) {
				boolean newAddFlag = false;
				boolean ignoreFlag = false;
				// 检测是否忽略
				for (int pp = 0; ignoreFileNamePtnStrArr != null && pp < ignoreFileNamePtnStrArr.length; pp++) {
					if (newFileName.contains(ignoreFileNamePtnStrArr[pp])) {
						ignoreFlag = true;
						break;// 跳出for小循环
					}
				}
				if (!ignoreFlag) {
					// 检测是否是新增加的
					newAddFlag = newAddedFilesMap.containsKey(newFileName);
				}
				if (newAddFlag || ignoreFlag) {
					if (newFolderFilesItr.hasNext()) {
						newFileEntry = newFolderFilesItr.next();
						newFileName = newFileEntry.getKey();
					} else {
						finish = true;
						break;
					}
				} else {
					break;
				}
			}
			if (finish) {
				break;
			}
			// 剔除文件夹短名
			String newNameNoPrefix = newFileName.substring(newFileName.indexOf(File.separator));

			// （3.3）开始比较文件是否修改
			// System.out.println("比较：{"+newFileName+"} vs {"+oldFileName+"}");
			// 按正常逻辑来讲，TreeMap对象是排序的，两个FolderTraversal的treeMap按文件名排序，排除新增或丢失的一定相等
			if (newNameNoPrefix.equals(oldNameNoPrefix)) {
				// 获得文件mini属性对象
				FilePropertyMini oldFileProp = oldFileEntry.getValue();
				FilePropertyMini newFileProp = newFileEntry.getValue();
				if (!oldFileProp.isFolder() && !newFileProp.isFolder()) {// 都是文件
					if (oldFileProp.getLastModiTime() != newFileProp.getLastModiTime()) {
						this.changedFilesMap.put(newFileName, newFileProp);
						this.diffTimes++;
						// System.out.println("文件{" + newFileName + "}被修改了");
					}
				} else if (oldFileProp.isFolder() && newFileProp.isFolder()) {// 都是子文件夹
					// do nothing
				} else {// 有一个是文件夹一个是文件，应该不会这样，因为系统默认子文件夹名字以‘File.separator’结尾
					// System.out.println("'文件" + (oldFileProp.isFolder() ? "夹'"
					// : "'") + "{" + oldFileName + "}，"
					// + "被修改成了'文件" + (newFileProp.isFolder() ? "夹'" : "'"));
				}
			} else {
				System.err.println("Warning: 比较文件逻辑出错。");
			}
		}
		if (this.changedFilesMap.size() == 0) {
			// System.out.println("没有被修改的文件。");
		}
	}

	/**
	 * 从新的文件夹中，复制文件夹比较结果集中 所有“新增加的文件（夹）” 到 【特定的文件夹】
	 * 
	 * @param destFolderName
	 *            目的文件夹名字
	 */
	public void copyNewAddedFiles2Folder(String destFolderName) throws Exception {
		if (this.newAddedFilesMap == null || this.newFolderTraversal == null) {
			throw new IllegalStateException("还没有比较结果！");
		}
		if (this.newAddedFilesMap.size() == 0) {
			return;
		}
		// 获得新增加的文件所在“新文件夹”的目录路径
		String newAddedFilesParentPath = this.newFolderTraversal.getParentPath();

		// 遍历新增加的文件列表treeMap
		Iterator<Map.Entry<String, FilePropertyMini>> newAddedFilesItr = this.newAddedFilesMap.entrySet().iterator();
		while (newAddedFilesItr.hasNext()) {
			// 新增加的文件
			Map.Entry<String, FilePropertyMini> newAddedFileEntry = newAddedFilesItr.next();
			String newAddedFileName = newAddedFileEntry.getKey();
			String newAddedFileFullPath = newAddedFilesParentPath + File.separatorChar + newAddedFileName;
			// 拼接目的文件全路径
			String destFileFullPath = destFolderName + File.separator + newAddedFileName;
			// 复制文件，或者创建文件夹
			FilePropertyMini newAddedFileProp = newAddedFileEntry.getValue();
			if (newAddedFileProp.isFolder()) {// 创建文件夹
				new File(destFileFullPath).mkdirs();
			} else {// 复制文件
				FileCopyTool.copy(newAddedFileFullPath, destFileFullPath);
			}
		}

	}

	/**
	 * 从新的文件夹中，复制文件夹比较结果集中 所有“被修改的文件（夹）” 到 【特定的文件夹】
	 * 
	 * @param destFolderName
	 *            目的文件夹名字
	 */
	public void copyChangedFiles2Folder(String destFolderName) throws Exception {
		if (this.changedFilesMap == null || this.newFolderTraversal == null) {
			throw new IllegalStateException("还没有比较结果！");
		}
		if (this.changedFilesMap.size() == 0) {
			return;
		}
		// 获得被修改过的文件所在“新文件夹”的目录路径
		String changedFilesParentPath = this.newFolderTraversal.getParentPath();

		// 遍历被修改过的文件列表treeMap
		Iterator<Map.Entry<String, FilePropertyMini>> changedFilesItr = this.changedFilesMap.entrySet().iterator();
		while (changedFilesItr.hasNext()) {
			// 被修改过的文件
			Map.Entry<String, FilePropertyMini> changedFileEntry = changedFilesItr.next();
			String changedFileName = changedFileEntry.getKey();
			String changedFileFullPath = changedFilesParentPath + File.separatorChar + changedFileName;
			// 拼接目的文件全路径
			String destFileFullPath = destFolderName + File.separator + changedFileName;
			// 复制文件，或者创建文件夹
			FilePropertyMini changedFileProp = changedFileEntry.getValue();
			if (changedFileProp.isFolder()) {// 创建文件夹
				new File(destFileFullPath).mkdirs();
			} else {// 复制文件
				FileCopyTool.copy(changedFileFullPath, destFileFullPath);
			}
		}

	}

	/**
	 * 从旧的文件夹中，备份复制文件夹比较结果集中 所有“被修改的文件（夹）” 到 【特定的文件夹】 并且为文件添加特定的后缀
	 * 
	 * @param destFolderName
	 *            目的文件夹名字
	 * @param backupFileSuffix
	 *            备份文件后缀（拼接到源文件名后），如果是null则保持原文件名
	 */
	public void backupChangedFiles2Folder(String destFolderName, String backupFileSuffix) throws Exception {
		if (this.changedFilesMap == null || this.newFolderTraversal == null) {
			throw new IllegalStateException("还没有比较结果！");
		}
		if (this.changedFilesMap.size() == 0) {
			return;
		}
		if (backupFileSuffix == null) {
			backupFileSuffix = "";
		}

		// 获得被修改过的文件所在“旧文件夹”的目录路径
		String changedFilesParentPath = this.oldFolderTraversal.getParentPath();
		String oldFolderName = this.oldFolderTraversal.getFolderName();

		// 遍历被修改过的文件列表treeMap
		Iterator<Map.Entry<String, FilePropertyMini>> changedFilesItr = this.changedFilesMap.entrySet().iterator();
		while (changedFilesItr.hasNext()) {
			// 被修改过的文件
			Map.Entry<String, FilePropertyMini> changedFileEntry = changedFilesItr.next();
			String changedFileName = changedFileEntry.getKey();//changedFileName保存的新文件夹中路径，应拼接为旧文件的路径
			String changedFileNameNoPrefix = changedFileName.substring(changedFileName.indexOf(File.separator));
			changedFileName = oldFolderName + changedFileNameNoPrefix;
			String changedFileFullPath = changedFilesParentPath + File.separatorChar + changedFileName;
			// 拼接目的文件全路径
			String destFileFullPath = destFolderName + File.separator + changedFileName;
			// 复制文件，或者创建文件夹
			FilePropertyMini changedFileProp = changedFileEntry.getValue();
			if (changedFileProp.isFolder()) {// 创建文件夹
				new File(destFileFullPath).mkdirs();
			} else {// 复制文件
				FileCopyTool.copy(changedFileFullPath, destFileFullPath + backupFileSuffix);
			}
		}

	}

	/**
	 * 从旧的文件夹中，复制文件夹比较结果集中 所有“丢失的文件（夹）” 到 【特定的文件夹】
	 * 
	 * @param destFolderName
	 *            目的文件夹名字
	 */
	public void copyLostFiles2Folder(String destFolderName) throws Exception {
		if (this.lostFilesMap == null || this.newFolderTraversal == null) {
			throw new IllegalStateException("还没有比较结果！");
		}
		if (this.lostFilesMap.size() == 0) {
			return;
		}
		// 获得丢失的文件所在“旧文件夹”的目录路径
		String lostFilesParentPath = this.oldFolderTraversal.getParentPath();

		// 遍历丢失的文件列表treeMap
		Iterator<Map.Entry<String, FilePropertyMini>> lostFilesItr = this.lostFilesMap.entrySet().iterator();
		while (lostFilesItr.hasNext()) {
			// 丢失的文件
			Map.Entry<String, FilePropertyMini> lostFileEntry = lostFilesItr.next();
			String lostFileName = lostFileEntry.getKey();
			String lostFileFullPath = lostFilesParentPath + File.separatorChar + lostFileName;
			// 拼接目的文件全路径
			String destFileFullPath = destFolderName + File.separator + lostFileName;
			// 复制文件，或者创建文件夹
			FilePropertyMini lostFileProp = lostFileEntry.getValue();
			if (lostFileProp.isFolder()) {// 创建文件夹
				new File(destFileFullPath).mkdirs();
			} else {// 复制文件
				FileCopyTool.copy(lostFileFullPath, destFileFullPath);
			}
		}

	}

	/**
	 * 将比较集结果转换为字符串
	 * 
	 * @return 比较结果描述字符串
	 */
	public String toDescriptionString() throws Exception {
		if (this.oldFolderTraversal == null || this.newFolderTraversal == null || this.lostFilesMap == null
				|| this.newAddedFilesMap == null || this.changedFilesMap == null) {
			throw new IllegalStateException("还没有比较结果！");
		}

		StringBuffer descriptionBuf = new StringBuffer(4096);

		// 得到待比较的文件夹名字
		String oldFolderName = this.oldFolderTraversal.getFolderName();
		String newFolderName = this.newFolderTraversal.getFolderName();
		descriptionBuf.append("比较文件夹:\n");
		descriptionBuf.append("  <旧> ")
				.append(this.oldFolderTraversal.getParentPath() + File.separator + oldFolderName).append('\n');
		descriptionBuf.append("  <新> ")
				.append(this.newFolderTraversal.getParentPath() + File.separator + newFolderName).append('\n');

		// 得到新增加的文件记录
		descriptionBuf.append("\n----------新增加的文件（夹）列表----------\n");
		if (this.newAddedFilesMap.size() == 0) {
			descriptionBuf.append("没有新添加文件。\n");
		} else {
			// 遍历新增加的文件列表treeMap
			Iterator<Map.Entry<String, FilePropertyMini>> newAddedFilesItr = this.newAddedFilesMap.entrySet()
					.iterator();
			while (newAddedFilesItr.hasNext()) {
				// 新增加的文件
				Map.Entry<String, FilePropertyMini> newAddedFileEntry = newAddedFilesItr.next();
				String newAddedFileName = newAddedFileEntry.getKey();
				FilePropertyMini newAddedFileProp = newAddedFileEntry.getValue();
				descriptionBuf.append("新增文件" + (newAddedFileProp.isFolder() ? "夹" : "") + "：{" + newAddedFileName
						+ "}\n");
			}
			descriptionBuf.append("共新增文件(夹) " + this.newAddedFilesMap.size() + " 个。\n");
		}

		// 得到丢失的文件记录
		descriptionBuf.append("\n----------丢失的文件（夹）列表----------\n");
		if (this.lostFilesMap.size() == 0) {
			descriptionBuf.append("没有丢失任何文件。\n");
		} else {
			// 遍历丢失的文件列表treeMap
			Iterator<Map.Entry<String, FilePropertyMini>> lostFilesItr = this.lostFilesMap.entrySet().iterator();
			while (lostFilesItr.hasNext()) {
				// 丢失的文件
				Map.Entry<String, FilePropertyMini> lostFileEntry = lostFilesItr.next();
				String lostFileName = lostFileEntry.getKey();
				FilePropertyMini lostFileProp = lostFileEntry.getValue();
				descriptionBuf.append("丢失文件" + (lostFileProp.isFolder() ? "夹" : "") + "：{" + lostFileName + "}\n");
			}
			descriptionBuf.append("共丢失文件(夹) " + this.lostFilesMap.size() + " 个。\n");
		}

		// 得到修改的文件记录列表
		descriptionBuf.append("\n----------被修改的文件列表----------\n");
		if (this.changedFilesMap.size() == 0) {
			descriptionBuf.append("没有被修改的文件。\n");
		} else {
			// 遍历被修改过的文件列表treeMap
			Iterator<Map.Entry<String, FilePropertyMini>> changedFilesItr = this.changedFilesMap.entrySet().iterator();
			while (changedFilesItr.hasNext()) {
				// 被修改过的文件
				Map.Entry<String, FilePropertyMini> changedFileEntry = changedFilesItr.next();
				String changedFileName = changedFileEntry.getKey();
				// FilePropertyMini changedFileProp =
				// changedFileEntry.getValue();
				descriptionBuf.append("文件{" + changedFileName + "}被修改了\n");
			}
			descriptionBuf.append("共修改文件 " + this.changedFilesMap.size() + " 个。\n");
		}
		descriptionBuf.append("\n----------\n");
		descriptionBuf.append("总共差异次数: " + this.diffTimes + " 次。\n");

		return descriptionBuf.toString();
	}
}
