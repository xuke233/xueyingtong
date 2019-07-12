/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.analyze.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;

public class Analyzer extends LFrame {
	// used by circuit analysis to select the relevant tab automatically.由电路分析自动选择相关选项卡。
	public static final int INPUTS_TAB = 0;
	public static final int OUTPUTS_TAB = 1;
	public static final int TABLE_TAB = 2;
	public static final int EXPRESSION_TAB = 3;
	public static final int MINIMIZED_TAB = 4;

	private class MyListener implements LocaleListener { //LocaleListener 局部监听 localeChanged()方法在MyListener中重写
		public void localeChanged() {
			Analyzer.this.setTitle(Strings.get("analyzerWindowTitle"));
			tabbedPane.setTitleAt(INPUTS_TAB, Strings.get("inputsTab"));
			tabbedPane.setTitleAt(OUTPUTS_TAB, Strings.get("outputsTab"));
			tabbedPane.setTitleAt(TABLE_TAB, Strings.get("tableTab"));
			tabbedPane.setTitleAt(EXPRESSION_TAB, Strings.get("expressionTab"));
			tabbedPane.setTitleAt(MINIMIZED_TAB, Strings.get("minimizedTab"));
			tabbedPane.setToolTipTextAt(INPUTS_TAB, Strings.get("inputsTabTip"));
			tabbedPane.setToolTipTextAt(OUTPUTS_TAB, Strings.get("outputsTabTip"));
			tabbedPane.setToolTipTextAt(TABLE_TAB, Strings.get("tableTabTip"));
			tabbedPane.setToolTipTextAt(EXPRESSION_TAB, Strings.get("expressionTabTip"));
			tabbedPane.setToolTipTextAt(MINIMIZED_TAB, Strings.get("minimizedTabTip"));
			buildCircuit.setText(Strings.get("buildCircuitButton"));
			inputsPanel.localeChanged();
			outputsPanel.localeChanged();
			truthTablePanel.localeChanged();
			expressionPanel.localeChanged();
			minimizedPanel.localeChanged();
			buildCircuit.localeChanged();
		}
	}
	
	private class EditListener implements ActionListener, ChangeListener {
		private void register(LogisimMenuBar menubar) {
			menubar.addActionListener(LogisimMenuBar.CUT, this);
			menubar.addActionListener(LogisimMenuBar.COPY, this);
			menubar.addActionListener(LogisimMenuBar.PASTE, this);
			menubar.addActionListener(LogisimMenuBar.DELETE, this);
			menubar.addActionListener(LogisimMenuBar.SELECT_ALL, this);
			tabbedPane.addChangeListener(this);
			enableItems(menubar);
		}
		
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			Component c = tabbedPane.getSelectedComponent();
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			}
			if (!(c instanceof TabInterface)) return;
			TabInterface tab = (TabInterface) c;
			if (src == LogisimMenuBar.CUT) {
				tab.copy();
				tab.delete();
			} else if (src == LogisimMenuBar.COPY) {
				tab.copy();
			} else if (src == LogisimMenuBar.PASTE) {
				tab.paste();
			} else if (src == LogisimMenuBar.DELETE) {
				tab.delete();
			} else if (src == LogisimMenuBar.SELECT_ALL) {
				tab.selectAll();
			}
		}
		
		private void enableItems(LogisimMenuBar menubar) {
			Component c = tabbedPane.getSelectedComponent();
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			}
			boolean support = c instanceof TabInterface;
			menubar.setEnabled(LogisimMenuBar.CUT, support);
			menubar.setEnabled(LogisimMenuBar.COPY, support);
			menubar.setEnabled(LogisimMenuBar.PASTE, support);
			menubar.setEnabled(LogisimMenuBar.DELETE, support);
			menubar.setEnabled(LogisimMenuBar.SELECT_ALL, support);
		}

		public void stateChanged(ChangeEvent e) {
			enableItems((LogisimMenuBar) getJMenuBar());
			
			Object selected = tabbedPane.getSelectedComponent();
			if (selected instanceof JScrollPane) {
				selected = ((JScrollPane) selected).getViewport().getView();
			}
			if (selected instanceof AnalyzerTab) {
				((AnalyzerTab) selected).updateTab();
			}
		}
	}
	
	private MyListener myListener = new MyListener();
	private EditListener editListener = new EditListener();
	private AnalyzerModel model = new AnalyzerModel();
	private JTabbedPane tabbedPane = new JTabbedPane(); //JTabbedPane 它允许用户通过单击具有给定标题和/或图标的选项卡，在一组组件之间进行切换。
	                                                    //通过使用 addTab 和 insertTab 方法将选项卡/组件添加到 TabbedPane 对象中
	                                                    //TabbedPane 使用 SingleSelectionModel 来表示选项卡索引集和当前所选择的索引
	private VariableTab inputsPanel;                    //VariableTab.java
	private VariableTab outputsPanel;
	private TableTab truthTablePanel;                   //TableTab.java
	private ExpressionTab expressionPanel;              //ExpressionTab.java
	private MinimizedTab minimizedPanel;                //MinimizedTab.java
	private BuildCircuitButton buildCircuit;            //BuildCircuitButton.java
	
	Analyzer() {
		inputsPanel = new VariableTab(model.getInputs());       //VariableTab.java
		outputsPanel = new VariableTab(model.getOutputs());
		truthTablePanel = new TableTab(model.getTruthTable());
		expressionPanel = new ExpressionTab(model);
		minimizedPanel = new MinimizedTab(model);
		buildCircuit = new BuildCircuitButton(this, model);

		truthTablePanel.addMouseListener(new TruthTableMouseListener()); //TruthTableMouseListener.java
		
		tabbedPane = new JTabbedPane();                          //JTabbedPane()：创建一个默认的选项卡面板，默认情况下标签在选项卡的上方，布局方式为限制布局
		addTab(INPUTS_TAB, inputsPanel);                         //添加Analyze Circuit 窗口的五个tab分界面
		addTab(OUTPUTS_TAB, outputsPanel);                       // addTab(String title,Component component)添加一个由 title 表示，且没有图标的 component。insertTab 的覆盖方法。
		addTab(TABLE_TAB, truthTablePanel);                      //参数：title - 此选项卡要显示的标题,component - 单击此选项卡时要显示的组件
		addTab(EXPRESSION_TAB, expressionPanel);
		addTab(MINIMIZED_TAB, minimizedPanel);
		
		Container contents = getContentPane();                   //返回此窗体的 contentPane 对象
		JPanel vertStrut = new JPanel(null);
		vertStrut.setPreferredSize(new Dimension(0, 300));       //设置此组件的首选大小。如果 preferredSize 为 null，则要求 UI 提供首选大小。
		JPanel horzStrut = new JPanel(null);
		horzStrut.setPreferredSize(new Dimension(450, 0));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buildCircuit);                           //add(PopupMenu popup)向组件添加指定的弹出菜单。
		                                                         // Component add(Component comp)将指定组件追加到此容器的尾部。
		contents.add(vertStrut, BorderLayout.WEST);
		contents.add(horzStrut, BorderLayout.NORTH);
		contents.add(tabbedPane, BorderLayout.CENTER);
		contents.add(buttonPanel, BorderLayout.SOUTH);
		
		DefaultRegistry registry = new DefaultRegistry(getRootPane());  //DefaultRegistry.java
		inputsPanel.registerDefaultButtons(registry);
		outputsPanel.registerDefaultButtons(registry);
		expressionPanel.registerDefaultButtons(registry);
		
		LocaleManager.addLocaleListener(myListener);                     //LocaleManager.java
		myListener.localeChanged();
		
		LogisimMenuBar menubar = new LogisimMenuBar(this, null);         //LogisimMenuBar.java    最上一栏菜单
		setJMenuBar(menubar);                                            //setJMenuBar(JMenuBar menubar)设置此窗体的菜单栏。  menubar - 放置于该窗体中的菜单栏
		editListener.register(menubar);
	}
	
	private void addTab(int index, final JComponent comp) {
		final JScrollPane pane = new JScrollPane(comp,                            //http://www.cjsdn.net/Doc/JDK50/javax/swing/JScrollPane.html
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,                    //int 用于设置垂直滚动条策略以使垂直滚动条一直显示。
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);                  //int 用于设置水平滚动条策略以使水平滚动条从不显示。
		if (comp instanceof TableTab) {                                           //instanceof作用是判断其左边对象是否为其右边类的实例，返回boolean类型的数据
			pane.setVerticalScrollBar(((TableTab) comp).getVerticalScrollBar());  //setVerticalScrollBar将控制视口垂直视图位置的滚动条添加到滚动窗格中。
		}
		pane.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent event) {
				int width = pane.getViewport().getWidth();
				comp.setSize(new Dimension(width, comp.getHeight()));
			}

			public void componentMoved(ComponentEvent arg0) { }
			public void componentShown(ComponentEvent arg0) { }
			public void componentHidden(ComponentEvent arg0) { }
		});
		tabbedPane.insertTab("Untitled", null, pane, null, index);
	}
	
	public AnalyzerModel getModel() {
		return model;
	}
	
	public void setSelectedTab(int index) {
		Object found = tabbedPane.getComponentAt(index);       //类 Object 是类层次结构的根类。每个类都使用 Object 作为超类。所有对象（包括数组）都实现这个类的方法。
		                                                       //getComponentAt(int index):返回 index 位置的组件。
		                                                       //参数：index - 正在被查询的项的索引  返回：index 位置的 Component
		if (found instanceof AnalyzerTab) {
			((AnalyzerTab) found).updateTab();
		}
		tabbedPane.setSelectedIndex(index);                    //setSelectedIndex(int index)	设置所选择的此选项卡窗格的索引。参数:index - 要选择的索引
	}
	
	public static void main(String[] args) {
		Analyzer frame = new Analyzer();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setDefaultCloseOperation设置当用户在此对话框上发起 "close" 时默认执行的操作.默认将该值设置为 HIDE_ON_CLOSE。
		//EXIT_ON_CLOSE退出应用程序后的默认窗口关闭操作。如果执行关闭操作时窗口具有此设置并且是在 applet 中关闭窗口，则可能抛出 SecurityException
		frame.pack();//pack()调整此窗口的大小，以适合其子组件的首选大小和布局。如果该窗口和/或其所有者仍不可显示，则两者在计算首选大小之前变得可显示。在计算首选大小之后，将会验证该 Window。
		frame.setVisible(true);//setVisible(boolean b)设置此对象的可见状态。
	}
}
