package com.lew.scott.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 选择两个要比较的文件夹
 * 
 * @author Scott Lew
 * 
 */
public class CompareFolderSelectDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String oldFolderPath;// 旧文件夹路径
	private String newFolderPath;// 新文件夹路径
	private boolean isConfirm;// 是否已经确定选择

	private JTextField oldFolderPathInput;
	private JTextField newFolderPathInput;

	/**
	 * 构造方法，必须指明owner
	 * 
	 * @param owner
	 */
	public CompareFolderSelectDialog(Frame owner) {
		super(owner, "需要需要比较的文件夹", true);
		isConfirm = false;
		initGUI();
	}

	/**
	 * 
	 */
	private void initGUI() {
		// 设定大小和位置
		this.setSize(600, 180);
		this.setLocation(300, 400);
		// 添加组件
		this.setLayout(new BorderLayout());
		FlowLayout northLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel northPanel = new JPanel(northLayout);
		JLabel titleLabel = new JLabel("需要需要比较的文件夹");
		northPanel.add(titleLabel);
		this.add(northPanel, BorderLayout.NORTH);
		// 中间面板
		JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		// 选择旧的文件夹组件
		JPanel centerUpperPanel = new JPanel();
		JLabel oldFolderLabel = new JLabel("旧的文件夹:");
		oldFolderPathInput = new JTextField(oldFolderPath, 40);
		JButton oldFolderSelectButton = new JButton("选择");
		oldFolderSelectButton.setActionCommand("oldFolderSelect");
		oldFolderSelectButton.addActionListener(this);
		centerUpperPanel.add(oldFolderLabel);
		centerUpperPanel.add(oldFolderPathInput);
		centerUpperPanel.add(oldFolderSelectButton);
		// 选择新的文件夹组件
		JPanel centerLowerPanel = new JPanel();
		JLabel newFolderLabel = new JLabel("新的文件夹:");
		newFolderPathInput = new JTextField(newFolderPath, 40);
		JButton newFolderSelectButton = new JButton("选择");
		newFolderSelectButton.setActionCommand("newFolderSelect");
		newFolderSelectButton.addActionListener(this);
		centerLowerPanel.add(newFolderLabel);
		centerLowerPanel.add(newFolderPathInput);
		centerLowerPanel.add(newFolderSelectButton);
		// 填充中间面板
		centerPanel.add(centerUpperPanel);
		centerPanel.add(centerLowerPanel);
		this.add(centerPanel, BorderLayout.CENTER);
		// 添加确定按钮
		FlowLayout southLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel southPanel = new JPanel(southLayout);
		JButton confirmButton = new JButton("确定");
		confirmButton.setActionCommand("confirm");
		confirmButton.addActionListener(this);
		southPanel.add(confirmButton);
		this.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * 命令执行(ActionListener)接口实现
	 */
	public void actionPerformed(ActionEvent e) {
		// 获取按钮或菜单命令
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("confirm")) {
			oldFolderPath = oldFolderPathInput.getText();
			newFolderPath = newFolderPathInput.getText();
			if (oldFolderPath == null || oldFolderPath.equals("")) {
				JOptionPane.showMessageDialog(this, "请选择旧的文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (newFolderPath == null || newFolderPath.equals("")) {
				JOptionPane.showMessageDialog(this, "请选择新的文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			this.dispose();
			isConfirm = true;
		} else if (actionCommand.equals("oldFolderSelect")) {
			// 但在Windows中FileDialog + FilenameFilter无法正常工作
			// jdoc的原注释为:Filename filters do not function in Sun's reference
			// implementation for Microsoft Windows.
			// FileDialog fileDialog = new FileDialog(this, "选择旧的文件夹");
			// fileDialog.setDirectory(oldFolderPath);
			// fileDialog.setFilenameFilter(new FilenameFilter(){
			// //过滤器
			// public boolean accept(File dir, String name) {
			// if(dir.isDirectory()){
			// return true;
			// }
			// return false;
			// }
			//				
			// });
			// fileDialog.setVisible(true);
			// oldFolderPath = fileDialog.getDirectory();
			// oldFolderPathInput.setText(oldFolderPath);
			// System.out.println(oldFolderPath+fileDialog.getFile());
			// fileDialog.dispose();

			// 用JFileChooser实现选择文件夹
			String initFolderPath = oldFolderPath;
			if (initFolderPath == null || initFolderPath.equals("")) {
				initFolderPath = newFolderPath;
			}
			JFileChooser fileChooser = new JFileChooser(initFolderPath);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
			fileChooser.showDialog(this, "选择旧的文件夹");
			try {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					oldFolderPath = file.getCanonicalPath();
					oldFolderPathInput.setText(oldFolderPath);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (actionCommand.equals("newFolderSelect")) {
			// 用JFileChooser实现选择文件夹
			String initFolderPath = newFolderPath;
			if (initFolderPath == null || initFolderPath.equals("")) {
				initFolderPath = oldFolderPath;
			}
			JFileChooser fileChooser = new JFileChooser(initFolderPath);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
			fileChooser.showDialog(this, "选择新的文件夹");
			try {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					newFolderPath = file.getCanonicalPath();
					newFolderPathInput.setText(newFolderPath);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println(actionCommand);
		}
	}

	/**
	 * 返回旧的文件夹路径
	 * 
	 * @return 旧的文件夹路径
	 */
	public String getOldFolderPath() {
		return oldFolderPath;
	}

	/**
	 * 返回新的文件夹路径
	 * 
	 * @return 新的文件夹路径
	 */
	public String getNewFolderPath() {
		return newFolderPath;
	}

	/**
	 * 初始设定已经选择的路径
	 * 
	 * @param oldFolderPath
	 */
	public void setOldFolderPath(String oldFolderPath) {
		this.oldFolderPath = oldFolderPath;
		this.oldFolderPathInput.setText(oldFolderPath);
	}

	/**
	 * 初始设定已经选择的路径
	 * 
	 * @param newFolderPath
	 */
	public void setNewFolderPath(String newFolderPath) {
		this.newFolderPath = newFolderPath;
		this.newFolderPathInput.setText(newFolderPath);
	}

	/**
	 * 是否已经确定选择
	 * 
	 * @return isConfirm
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

}
