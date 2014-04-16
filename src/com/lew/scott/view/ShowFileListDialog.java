package com.lew.scott.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ShowFileListDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	private String[] filenameList; // 初始化文件名列表
	private String[] selectedFilenameList; // 被选择的文件名列表
	private String folderPath;// 文件夹路径
	private boolean isConfirm;// 是否已经确定选择

	private JList fileList;
	private DefaultComboBoxModel selectFileListModel;
	private JTextField folderPathInput;

	private String messge;

	/**
	 * 构造方法，必须指明owner
	 * 
	 * @param owner
	 */
	public ShowFileListDialog(Frame owner, String title, String messge) {
		super(owner, title, true);
		this.messge = messge;
		this.isConfirm = false;
		initGUI();
	}

	/**
	 * 
	 */
	private void initGUI() {
		// 设定大小和位置
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setLocation(330, 220);
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
		// 文件列表显示List
		if (filenameList == null) {
			filenameList = new String[] {};
		}
		selectFileListModel = new DefaultComboBoxModel(filenameList);
		fileList = new JList(selectFileListModel);
		fileList.setVisibleRowCount(HEIGHT / 28);
		fileList.setFixedCellWidth(WIDTH - 50);
		// 嵌入到滚动面板
		final JScrollPane centerScrollPane = new JScrollPane(fileList);
		centerPanel.add(centerScrollPane);
		// 填充中间面板
		this.add(centerPanel, BorderLayout.CENTER);

		FlowLayout southLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel southPanel = new JPanel(southLayout);
		// 选择文件夹组件
		folderPathInput = new JTextField(folderPath, 40);
		JButton folderSelectButton = new JButton("选择");
		folderSelectButton.setActionCommand("folderSelect");
		folderSelectButton.addActionListener(this);
		southPanel.add(folderPathInput);
		southPanel.add(folderSelectButton);
		this.add(southPanel, BorderLayout.SOUTH);
		// 添加确定按钮
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
			// 得到选择的文件名列表
			Object[] selectedObjects = fileList.getSelectedValues();
			if (selectedObjects == null || selectedObjects.length == 0) {
				JOptionPane.showMessageDialog(this, "请选择文件列表", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			// 得到目的目录
			folderPath = folderPathInput.getText();
			if (folderPath == null || folderPath.equals("")) {
				JOptionPane.showMessageDialog(this, "请选择文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			// 处理返回
			this.dispose();
			selectedFilenameList = new String[selectedObjects.length];
			for (int i = 0; i < selectedFilenameList.length; i++) {
				selectedFilenameList[i] = selectedObjects[i].toString();
				System.out.println(selectedFilenameList[i]);
			}
			this.isConfirm = true;
		} else if (actionCommand.equals("folderSelect")) {
			// 用JFileChooser实现选择文件夹
			JFileChooser fileChooser = new JFileChooser(folderPath);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
			fileChooser.showDialog(this, "选择目的文件夹");
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

	/**
	 * 得到已经选择的文件名列表
	 * 
	 * @return
	 */
	public String[] getSelectedFilenameList() {
		return selectedFilenameList;
	}

	/**
	 * 设定可选择的文件名列表
	 * 
	 * @param filenameList
	 */
	public void setFilenameList(String[] filenameList) {
		if (filenameList == null) {
			return;
		}
		this.filenameList = filenameList;
		selectFileListModel.removeAllElements();
		for (int i = 0; i < this.filenameList.length; i++) {
			selectFileListModel.addElement(this.filenameList[i]);
		}
	}

}
