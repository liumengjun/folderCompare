package com.lew.scott.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextAreaDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 8105742711425541437L;
	private JTextArea resultDisplayObj;

	public TextAreaDialog(Frame owner, String title) {
		super(owner, title);
		initGUI();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initGUI() {
		// 设定大小和位置
		this.setSize(600, 330);
		this.setLocation(330, 250);
		this.setLayout(new BorderLayout(0, 3));

		// 添加菜单
		this.addMenu();

		// 添加中间text面板
		JPanel centerPanel = new JPanel();
		// 比较结果集text显示Panel
		resultDisplayObj = new JTextArea(20, 65);
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
				TextAreaDialog thisFrame = (TextAreaDialog) e.getComponent();
				Dimension newSize = thisFrame.getSize();
				// 设定textarea大小
				int textareaWidth = newSize.width - 50;
				int textareaHeight = newSize.height - 50;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void setText(String t) {
		this.resultDisplayObj.setText(t);
	}

	public String getText() {
		return this.resultDisplayObj.getText();
	}
}
