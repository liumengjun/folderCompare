package com.lew.scott.folder;

import java.io.File;

/**
 * 文件属性mini类：
 *     只保存文件的大小，和文件的修改时间
 * 该类只用于{@link FolderTraversal}类的便利记录map中<key为文件名，value为该类>
 * @author Scott Lew
 */
public class FilePropertyMini {
	
	private boolean isFolder;
	private long fileSize;
	private long lastModiTime;
	
	public FilePropertyMini(boolean isFolder, long fileSize, long lastModiTime){
		this.isFolder = isFolder;
		this.fileSize = fileSize;
		this.lastModiTime = lastModiTime;
	}
	
	public FilePropertyMini(File file) throws Exception{
		this.setFile(file);
	}
	
	protected void setFile(File file) throws Exception{
		if(file==null || !file.exists()){
			throw new IllegalArgumentException("错误的文件");
		}
		this.isFolder = file.isDirectory();
		this.fileSize = file.length();
		this.lastModiTime = file.lastModified();
	}
	
	public boolean isFolder(){
		return isFolder;
	}
	
	public long getFileSize(){
		return fileSize;
	}
	
	public long getLastModiTime(){
		return lastModiTime;
	}
}
