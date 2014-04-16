package com.lew.scott.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
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

public class SingleFolderSelectDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private String folderPath;// 文件夹路径
	private boolean isConfirm;// 是否已经确定选择

	private JTextField folderPathInput;
	private String title;
	private String messge;

	/**
	 * 构造方法，必须指明owner
	 * 
	 * @param owner
	 */
	public SingleFolderSelectDialog(Frame owner, String title, String messge) {
		super(owner, title, true);
		this.title = title;
		this.messge = messge;
		this.isConfirm = false;
		initGUI();
	}

	/**
	 * 
	 */
	private void initGUI() {
		// 设定大小和位置
		this.setSize(600, 130);
		this.setLocation(330, 450);
		// 添加组件
		this.setLayout(new BorderLayout());
		FlowLayout northLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel northPanel = new JPanel(northLayout);
		JLabel titleLabel = new JLabel(this.messge);
		northPanel.add(titleLabel);
		this.add(northPanel, BorderLayout.NORTH);
		// 中间面板
		FlowLayout centerLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel centerPanel = new JPanel(centerLayout);
		// 选择文件夹组件
		folderPathInput = new JTextField(folderPath, 40);
		JButton folderSelectButton = new JButton("选择");
		folderSelectButton.setActionCommand("folderSelect");
		folderSelectButton.addActionListener(this);
		centerPanel.add(folderPathInput);
		centerPanel.add(folderSelectButton);
		// 填充中间面板
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
			folderPath = folderPathInput.getText();
			if (folderPath == null || folderPath.equals("")) {
				JOptionPane.showMessageDialog(this, "请选择文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			this.dispose();
			this.isConfirm = true;
		} else if (actionCommand.equals("folderSelect")) {
			// 用JFileChooser实现选择文件夹
			JFileChooser fileChooser = new JFileChooser(folderPath);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
			fileChooser.showDialog(this, this.title);
			try {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					folderPath = file.getCanonicalPath();
					folderPathInput.setText(folderPath);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println(actionCommand);
		}
	}

	/**
	 * 返回文件夹路径
	 * 
	 * @return folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}

	/**
	 * 初始设定已经选择的路径
	 * 
	 * @param folderPath
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
		this.folderPathInput.setText(folderPath);
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
