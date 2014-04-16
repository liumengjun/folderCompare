package com.lew.scott.compare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件复制工具类
 * @author Scott Lew
 * 
 */
public abstract class FileCopyTool {
	
	private static final int BUFFER_SIZE = 2*4*1024;
	
	/* whether over cover the already existing file flag */
	private static boolean overExistsFile = true;
	
	/**
	 * set whether over cover the already existing destination file
	 * @param flag
	 */
	public static void setOverExistsFile(boolean flag){
		overExistsFile = flag;
	}
	
	/**
	 * Copy the contents of the given input File to the given output File.
	 * also copy the last modified time attribute
	 * @param inFileName the file to copy from
	 * @param outFileName the file to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(String inFileName, String outFileName) throws IOException {
		if (inFileName == null || outFileName==null) {
			throw new IllegalArgumentException("没有指定输入文件文件名或输出文件名");
		}
		File inFile = new File(inFileName);
		File outFile = new File(outFileName);
		//copy file
		int copiedSize = copy(inFile, outFile);
		//copy lastModiTime attribute
		outFile.setLastModified(inFile.lastModified());
		return copiedSize;
	}
	
	/**
	 * Copy the contents of the given input File to the given output File.
	 * @param inFile the file to copy from
	 * @param outFile the file to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(File inFile, File outFile) throws IOException {
		if (inFile == null || outFile == null) {
			throw new IllegalArgumentException("没有指定输入文件或输出文件");
		}
		//检测源文件是否存在
		if(!inFile.exists() || inFile.isDirectory()){//不存在源文件（或者是文件夹）
			String inFilePath = inFile.getCanonicalPath();
			throw new IllegalArgumentException("源文件{"+inFilePath+"}不存在！");
		}
		//检测目的文件的父目录路径是否存在，不存在这创建
		String outFilePath = outFile.getCanonicalPath();
		if(outFile.exists()){//存在
			if(!overExistsFile){
				throw new IllegalArgumentException("目的文件{"+outFilePath+"}已经存在，且不允许覆盖！");
			}
		}else{//不存在
			String parentDirPath = outFilePath.substring(0, outFilePath.lastIndexOf(File.separator));
			File outDir = new File(parentDirPath);
			outDir.mkdirs();//mkdirs()方法内部会检测文件夹是否已经存在
		}
		return copy(new BufferedInputStream(new FileInputStream(inFile)),
		    new BufferedOutputStream(new FileOutputStream(outFile)));
	}
	
	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Closes both streams when done.
	 * @param inStream the stream to copy from
	 * @param outStream the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	private static int copy(InputStream inStream, OutputStream outStream) throws IOException {
		if (inStream == null || outStream==null) {
			throw new IllegalArgumentException("没有指定文件输入流或输出流");
		}
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			outStream.flush();
			return byteCount;
		}
		finally {
			try {
				inStream.close();
			}
			catch (IOException ex) {
			}
			try {
				outStream.close();
			}
			catch (IOException ex) {
			}
		}
	}
}
