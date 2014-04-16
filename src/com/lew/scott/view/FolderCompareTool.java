package com.lew.scott.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.lew.scott.compare.FolderCompare;
import com.lew.scott.folder.FolderTraversal;

/**
 * 文件夹比较工具主界面 利用{@link FolderCompare}对象比较两个文件夹
 * 
 * @author Scott Lew
 * 
 */
public class FolderCompareTool extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	// private static final int TEXT_LINE_HEIGHT = 19;// 比实际值小2

	private String oldFolderPath;
	private String newFolderPath;
	private String oldFolderTraversalFileName;
	private String newFolderTraversalFileName;

	private FolderCompare folderCompareObj = null;
	private String backupFileSuffix;
	private String ignoreFileNamePatterns;// 排除要比较的文件的文件名字包含的字符串,可设定多种，以分号(;)分隔。
	// 如:"\bin\; .bak"
	private boolean trimIgnorePatterns = true;// 忽略ignoreFileNamePatterns中每个字符串两头的空格

	private String newFilesDestFolderPath;
	private String changedFilesDestFolderPath;
	private String backupFilesDestFolderPath;
	private String lostFilesDestFolderPath;

	private JTextArea resultDisplayObj;

	private JCheckBoxMenuItem styleWindowsItem;
	private JCheckBoxMenuItem styleMotifItem;
	private JCheckBoxMenuItem styleMetalItem;

	/**
	 * This is the default constructor
	 */
	public FolderCompareTool() {
		super();
		initGUI();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initGUI() {
		// get screen dimensions
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int frameWidth = screenSize.width / 2;
		int frameHeight = screenSize.height / 2;
		// center frame in screen
		this.setSize(frameWidth, frameHeight);
		this.setMinimumSize(new Dimension(325, 215));
		this.setLocation(frameWidth / 2, frameHeight / 2);
		// set frame icon and title
		Image img = kit.getImage("res/mainicon.png");
		this.setIconImage(img);
		this.setTitle("文件夹比较工具");

		this.setLayout(new BorderLayout(0, 3));

		// 添加菜单
		this.addMenu();

		try {// Windows风格
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
			styleWindowsItem.setState(true);
		} catch (Exception ex) {
			styleMetalItem.setState(true);
		}

		// 添加中间text面板
		JPanel centerPanel = new JPanel();
		// 比较结果集text显示Panel
		resultDisplayObj = new JTextArea("选择菜单: 文件(F) -> 比较文件夹，比较结果将在此处显示。\n" + "然后，选择菜单: 操作(O)，可以导出变化的文件。", 20, 65);
		resultDisplayObj.setTabSize(4);
		Font newFont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 13);
		resultDisplayObj.setFont(newFont);
		// resultDisplayObj.setLineWrap(true);// 激活自动换行功能
		resultDisplayObj.setWrapStyleWord(true);// 激活断行不断字功能
		// 嵌入到滚动面板
		final JScrollPane centerScrollPane = new JScrollPane(resultDisplayObj);
		centerPanel.add(centerScrollPane);

		this.add(centerPanel, BorderLayout.CENTER);

		// 添加窗口大小改变相应函数
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				FolderCompareTool thisFrame = (FolderCompareTool) e.getComponent();
				Dimension newSize = thisFrame.getSize();
				// 设定textarea大小
				int textareaWidth = newSize.width - 50;
				int textareaHeight = newSize.height - 100;
				// 注意textarea最好设定[rows,columns]，与setPreferredSize()一起使用有问题
				resultDisplayObj.setRows(textareaHeight / 20);
				resultDisplayObj.setColumns(textareaWidth / 9);
				// 设定滚动面板大小
				Dimension newScrollPaneSize = new Dimension(textareaWidth + 18, textareaHeight + 3);
				centerScrollPane.setPreferredSize(newScrollPaneSize);
				// 更新组件
				resultDisplayObj.updateUI();
				thisFrame.validate();
			}
		});
	}

	/**
	 * 添加菜单
	 */
	private void addMenu() {
		JMenuItem m;
		/* file菜单 */
		JMenu fileMenu = new JMenu("文件(F)");
		fileMenu.setMnemonic('F');
		m = new JMenuItem("比较文件夹");
		m.setActionCommand("showCompareDialog");
		m.addActionListener(this);
		m.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.ALT_MASK));
		fileMenu.add(m);
		fileMenu.addSeparator();
		m = new JMenuItem("Exit");
		m.addActionListener(this);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		fileMenu.add(m);
		/* 查看菜单 */
		JMenu viewMenu = new JMenu("查看(V)");
		viewMenu.setMnemonic('V');
		m = new JMenuItem("旧文件夹的文件列表");
		m.setActionCommand("viewOldFolderFiles");
		m.addActionListener(this);
		viewMenu.add(m);
		m = new JMenuItem("新文件夹的文件列表");
		m.setActionCommand("viewNewFolderFiles");
		m.addActionListener(this);
		viewMenu.add(m);
		/* 查看菜单 */
		JMenu operateMenu = new JMenu("操作(O)");
		operateMenu.setMnemonic('O');
		m = new JMenuItem("导出新增的文件");
		m.setActionCommand("exportNewFiles");
		m.addActionListener(this);
		operateMenu.add(m);
		m = new JMenuItem("导出修改的文件");
		m.setActionCommand("exportChangedFiles");
		m.addActionListener(this);
		operateMenu.add(m);
		m = new JMenuItem("备份修改的文件");
		m.setActionCommand("backupChangedFiles");
		m.addActionListener(this);
		operateMenu.add(m);
		m = new JMenuItem("导出丢失的文件");
		m.setActionCommand("exportLostFiles");
		m.addActionListener(this);
		operateMenu.add(m);
		// 分割条
		operateMenu.addSeparator();
		// 设置有关菜单项
		m = new JMenuItem("备份文件名后缀设置");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = dateFormat.format(new Date());
		m.setToolTipText("设置备份生成文件的后缀。如\"." + today + ".bak\",若原文件名为a.txt,备份后为a.txt." + today + ".bak");
		m.setActionCommand("setBackupFileSuffix");
		m.addActionListener(this);
		operateMenu.add(m);
		m = new JMenuItem("忽略文件设置");
		m.setToolTipText("设置不需要比较的文件名字包含的字符串，(可设多个，用分号(;)分割)如:\"\\bin\\; .bak\"");
		m.setActionCommand("setIgnoreFile");
		m.addActionListener(this);
		operateMenu.add(m);
		m = new JCheckBoxMenuItem("trim忽略文件设置字符串");
		m.setToolTipText("\"忽略文件设置\"中设置了字符串，修剪每个字符串的空格");
		m.setActionCommand("trimIgnoreStr");
		((JCheckBoxMenuItem) m).setState(trimIgnorePatterns);
		m.addActionListener(this);
		operateMenu.add(m);
		/* Style菜单 */
		JMenu styleMenu = new JMenu("风格(S)");
		styleMenu.setMnemonic('S');
		styleWindowsItem = new JCheckBoxMenuItem("Windows");
		styleWindowsItem.setActionCommand("styleWindows");
		styleWindowsItem.addActionListener(this);
		styleMenu.add(styleWindowsItem);
		styleMotifItem = new JCheckBoxMenuItem("Motif");
		styleMotifItem.setActionCommand("styleMotif");
		styleMotifItem.addActionListener(this);
		styleMenu.add(styleMotifItem);
		styleMetalItem = new JCheckBoxMenuItem("Metal");
		styleMetalItem.setActionCommand("styleMetal");
		styleMetalItem.addActionListener(this);
		styleMenu.add(styleMetalItem);
		/* Help菜单 */
		JMenu helpMenu = new JMenu("帮助(H)");
		helpMenu.setMnemonic('H');
		m = new JMenuItem("About");
		m.addActionListener(this);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.ALT_MASK));
		helpMenu.add(m);

		/* 设定菜单 */
		JMenuBar mBar = new JMenuBar();
		mBar.add(fileMenu);
		mBar.add(viewMenu);
		mBar.add(operateMenu);
		mBar.add(styleMenu);
		mBar.add(helpMenu);
		this.setJMenuBar(mBar);
	}

	/**
	 * 命令执行接口实现
	 */
	public void actionPerformed(ActionEvent e) {
		// 获取按钮或菜单命令
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("showCompareDialog")) {
			// 显示文件夹选择对话框，并比较
			doCompare();
		} else if (actionCommand.equals("exportNewFiles")) {
			// 复制新增的文件到特定目录
			doCopyNewFiles();
		} else if (actionCommand.equals("exportChangedFiles")) {
			// 复制修改的文件到特定目录
			doCopyChangedFiles();
		} else if (actionCommand.equals("backupChangedFiles")) {
			// 备份修改的文件到特定目录
			doBackupChangedFiles();
		} else if (actionCommand.equals("exportLostFiles")) {
			// 复制丢失的文件到特定目录
			doCopyLostFiles();
		} else if (actionCommand.equals("viewOldFolderFiles")) {
			// 查看旧文件夹的FolderTraversal遍历结果
			viewOldFolderTraversal();
		} else if (actionCommand.equals("viewNewFolderFiles")) {
			// 查看新文件夹的FolderTraversal遍历结果
			viewNewFolderTraversal();
		} else if (actionCommand.equals("Exit")) {
			System.exit(0);// 退出程序
		} else if (actionCommand.equals("setBackupFileSuffix")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String today = dateFormat.format(new Date());
			String inputString = JOptionPane.showInputDialog(this, "设置备份生成文件的后缀。如\"." + today
					+ ".bak\",若原文件名为a.txt,备份后为a.txt." + today + ".bak", backupFileSuffix);
			if (inputString != null) {
				backupFileSuffix = inputString;
			}
		} else if (actionCommand.equals("setIgnoreFile")) {
			String inputString = JOptionPane.showInputDialog(this, "设定排除要比较的文件的文件名字包含的字符串，可设定多种，以分号(;)分隔\n"
					+ "   如: \"\\bin\\; .bak\"", ignoreFileNamePatterns);
			if (inputString != null) {
				ignoreFileNamePatterns = inputString;
			}
		} else if (actionCommand.equals("trimIgnoreStr")) {
			JCheckBoxMenuItem boxMenuItem = (JCheckBoxMenuItem) e.getSource();
			trimIgnorePatterns = boxMenuItem.getState();
		} else if (actionCommand.equals("styleWindows")) {
			try {// Windows风格
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
				styleWindowsItem.setState(true);
				styleMotifItem.setState(false);
				styleMetalItem.setState(false);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("styleMotif")) {
			try {// Motif外观
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
				styleWindowsItem.setState(false);
				styleMotifItem.setState(true);
				styleMetalItem.setState(false);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("styleMetal")) {
			try {// Metal外观
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
				styleWindowsItem.setState(false);
				styleMotifItem.setState(false);
				styleMetalItem.setState(true);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("About")) {
			String copyrightMessage = "文件夹比较工具\n\n" + "version 1.0\n" + "Author: Scott Lew\n"
					+ "Email: zhonglijunyi@163.com\n" + "Please search zhonglijunyi";
			JOptionPane.showMessageDialog(this, copyrightMessage, "Info", JOptionPane.INFORMATION_MESSAGE);
		} else {
			System.out.println(actionCommand);
		}
	}

	/**
	 * 查看旧文件夹的FolderTraversal遍历结果
	 */
	private void viewOldFolderTraversal() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		try {
			// 得到文件列表text
			FolderTraversal oldFolderTraversal = this.folderCompareObj.getOldFolderTraversal();
			String oldFolderResultDesc = oldFolderTraversal.toDescriptionString();
			// 用TextAreaDialog显示
			TextAreaDialog showTextDialog = new TextAreaDialog(this, "旧文件夹中的文件列表");
			showTextDialog.setText(oldFolderResultDesc);
			Point ownerLocation = this.getLocationOnScreen();
			showTextDialog.setLocation(ownerLocation.x + 50, ownerLocation.y + 90);
			showTextDialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "查看旧文件夹文件列表出现错误：\n" + "    " + e.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 查看新文件夹的FolderTraversal遍历结果
	 */
	private void viewNewFolderTraversal() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		try {
			// 得到文件列表text
			FolderTraversal newFolderTraversal = this.folderCompareObj.getNewFolderTraversal();
			String newFolderResultDesc = newFolderTraversal.toDescriptionString();
			// 用TextAreaDialog显示
			TextAreaDialog showTextDialog = new TextAreaDialog(this, "旧文件夹中的文件列表");
			showTextDialog.setText(newFolderResultDesc);
			Point ownerLocation = this.getLocationOnScreen();
			showTextDialog.setLocation(ownerLocation.x + 50, ownerLocation.y + 90);
			showTextDialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "查看新文件夹文件列表出现错误：\n" + "    " + e.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 显示文件夹选择对话框，并比较
	 */
	private void doCompare() {
		Point parentLocation = this.getLocationOnScreen();
		CompareFolderSelectDialog compareFolderSelectDialog = new CompareFolderSelectDialog(this);
		compareFolderSelectDialog.setOldFolderPath(oldFolderPath);
		compareFolderSelectDialog.setNewFolderPath(newFolderPath);
		compareFolderSelectDialog.setLocation(parentLocation.x + 50, parentLocation.y + 90);
		compareFolderSelectDialog.setVisible(true);
		oldFolderPath = compareFolderSelectDialog.getOldFolderPath();
		newFolderPath = compareFolderSelectDialog.getNewFolderPath();
		boolean isOK = compareFolderSelectDialog.isConfirm();
		try {
			if (isOK) {
				if (oldFolderPath == null || oldFolderPath.equals("")) {
					JOptionPane.showMessageDialog(this, "请选择旧的文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (newFolderPath == null || newFolderPath.equals("")) {
					JOptionPane.showMessageDialog(this, "请选择新的文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			if (isOK && oldFolderPath != null && newFolderPath != null && oldFolderPath.length() != 0
					&& newFolderPath.length() != 0) {
				this.resultDisplayObj.setText("正在比较，请稍候...");
				// 用另一个线程做比较工作
				Thread doCompareThread = new Thread(new Runnable() {
					public void run() {
						try {
							FolderCompare.setIgnoreFileNamePatterns(ignoreFileNamePatterns);
							FolderCompare.setWhetherTrimIgnorePattern(trimIgnorePatterns);
							// 开始比较
							folderCompareObj = FolderCompare.compareFolder(oldFolderPath, newFolderPath);
							// 显示比较结果描述
							String resultDesc = folderCompareObj.toDescriptionString();
							resultDisplayObj.setText(resultDesc);
						} catch (Exception ex0) {
							resultDisplayObj.setText("比较文件夹出现错误：\n" + "    " + ex0.getMessage());
						}
					}
				});
				doCompareThread.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "比较文件夹出现错误：\n" + "    " + ex.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 复制新增的文件到特定目录
	 */
	private void doCopyNewFiles() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Point parentLocation = this.getLocationOnScreen();
		SingleFolderSelectDialog singleFolderSelectDialog = new SingleFolderSelectDialog(this, "选择目的文件夹",
				"选择一个文件夹，新增的文件将copy到该目录下");
		singleFolderSelectDialog.setFolderPath(newFilesDestFolderPath);
		singleFolderSelectDialog.setLocation(parentLocation.x + 50, parentLocation.y + 90);
		singleFolderSelectDialog.setVisible(true);
		newFilesDestFolderPath = singleFolderSelectDialog.getFolderPath();
		boolean isOK = singleFolderSelectDialog.isConfirm();
		try {
			if (isOK && newFilesDestFolderPath != null && newFilesDestFolderPath.length() != 0) {
				folderCompareObj.copyNewAddedFiles2Folder(newFilesDestFolderPath);
				JOptionPane.showMessageDialog(this, "导出新增的文件完毕");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "操作被终止，出现错误：\n" + "    " + ex.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 复制修改的文件到特定目录
	 */
	private void doCopyChangedFiles() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Point parentLocation = this.getLocationOnScreen();
		SingleFolderSelectDialog singleFolderSelectDialog = new SingleFolderSelectDialog(this, "选择目的文件夹",
				"选择一个文件夹，新文件夹中修改的文件将copy到该目录下");
		singleFolderSelectDialog.setFolderPath(changedFilesDestFolderPath);
		singleFolderSelectDialog.setLocation(parentLocation.x + 50, parentLocation.y + 90);
		singleFolderSelectDialog.setVisible(true);
		changedFilesDestFolderPath = singleFolderSelectDialog.getFolderPath();
		boolean isOK = singleFolderSelectDialog.isConfirm();
		try {
			if (isOK && changedFilesDestFolderPath != null && changedFilesDestFolderPath.length() != 0) {
				folderCompareObj.copyChangedFiles2Folder(changedFilesDestFolderPath);
				JOptionPane.showMessageDialog(this, "导出修改的文件完毕");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "操作被终止，出现错误：\n" + "    " + ex.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 备份修改的文件到特定目录
	 */
	private void doBackupChangedFiles() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Point parentLocation = this.getLocationOnScreen();
		SingleFolderSelectDialog singleFolderSelectDialog = new SingleFolderSelectDialog(this, "选择目的文件夹",
				"选择一个文件夹，旧文件夹中修改的文件将copy到该目录下");
		singleFolderSelectDialog.setFolderPath(backupFilesDestFolderPath);
		singleFolderSelectDialog.setLocation(parentLocation.x + 50, parentLocation.y + 90);
		singleFolderSelectDialog.setVisible(true);
		backupFilesDestFolderPath = singleFolderSelectDialog.getFolderPath();
		boolean isOK = singleFolderSelectDialog.isConfirm();
		try {
			if (isOK && backupFilesDestFolderPath != null && backupFilesDestFolderPath.length() != 0) {
				folderCompareObj.backupChangedFiles2Folder(backupFilesDestFolderPath, backupFileSuffix);
				JOptionPane.showMessageDialog(this, "备份修改的文件完毕");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "操作被终止，出现错误：\n" + "    " + ex.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 复制丢失的文件到特定目录
	 */
	private void doCopyLostFiles() {
		if (folderCompareObj == null) {
			JOptionPane.showMessageDialog(this, "请先比较两个文件夹\n" + "  菜单：文件 -> 比较文件夹", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Point parentLocation = this.getLocationOnScreen();
		SingleFolderSelectDialog singleFolderSelectDialog = new SingleFolderSelectDialog(this, "选择目的文件夹",
				"选择一个文件夹，丢失的文件将copy到该目录下");
		singleFolderSelectDialog.setFolderPath(lostFilesDestFolderPath);
		singleFolderSelectDialog.setLocation(parentLocation.x + 50, parentLocation.y + 90);
		singleFolderSelectDialog.setVisible(true);
		lostFilesDestFolderPath = singleFolderSelectDialog.getFolderPath();
		boolean isOK = singleFolderSelectDialog.isConfirm();
		try {
			if (isOK && lostFilesDestFolderPath != null && lostFilesDestFolderPath.length() != 0) {
				folderCompareObj.copyLostFiles2Folder(lostFilesDestFolderPath);
				JOptionPane.showMessageDialog(this, "备份丢失的文件完毕");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "操作被终止，出现错误：\n" + "    " + ex.getMessage(), "错误提示",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
