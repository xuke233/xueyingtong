
import javax.swing.*;
import java.awt.*;
import java.io.*;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.gui.menu.ProjectCircuitActions;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;


public class Newcaidan {

	private JFrame f;
	private JLabel label;
	private JTextArea textArea;
	private JFileChooser fileChooser;
	private FileInputStream fileInStream;

	public Newcaidan() {
		// TODO Auto-generated constructor stub
		f=new JFrame("测试窗口");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane=f.getContentPane();
		textArea=new JTextArea();
		JScrollPane scrollPane=new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(350, 300));
		JTextArea textArea = new JTextArea(10, 10);
		JPanel panel=new JPanel();
		JMenuBar menuBar1=new JMenuBar();  //添加菜单条组件
		f.setJMenuBar(menuBar1);          //将菜单栏添加到顶层容器中
		JMenu menu1=new JMenu("文件");
		JMenu menu2=new JMenu("编辑");
		JMenu menu3=new JMenu("视图");
		JMenu menu4=new JMenu("运行");
		JMenu menu5=new JMenu("工具");
		JMenu menu6=new JMenu("帮助");

		//将菜单组件添加到菜单条组件中
		menuBar1.add(menu1);
		menuBar1.add(menu2);
		menuBar1.add(menu3);
		menuBar1.add(menu4);
		menuBar1.add(menu5);
		menuBar1.add(menu6);

		//创建菜单项组件
		JMenuItem item1=new JMenuItem("打开");
		JMenuItem item2=new JMenuItem("保存");
		JMenuItem item3=new JMenuItem("打印");
		JMenuItem item4=new JMenuItem("退出");
		menu1.add(item1);
		menu1.add(item2);
		menu1.addSeparator();           //菜单项之间的分隔线组件
		menu1.add(item3);
		menu1.addSeparator();
		menu1.add(item4);

		//设置顶层容器类的可见性
		f.setVisible(true);
		label=new JLabel("",JLabel.CENTER);
		contentPane.add(label, BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(panel, BorderLayout.SOUTH);
		f.pack();
		//处理退出菜单项的动作事件
		item4.addActionListener(e -> {
			// TODO Auto-generated method stub
			int i=JOptionPane.showConfirmDialog(null, "是否真的退出系统",
					"退出确认对话框",JOptionPane.YES_NO_CANCEL_OPTION);
			//通过对话框中按钮的选择来决定结果，单机yes时，窗口直接消失
			if(i==0)
				f.dispose();

		});
		item1.addActionListener(e -> {
			// TODO Auto-generated method stub
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(item1)==JFileChooser.APPROVE_OPTION) {//
				File file = chooser.getSelectedFile();
				System.out.println("file = " + file);
				//textArea.setText(file.getName()+":"+file.getPath()+"\n"+file.length());
				getTrueTable(file);
				//readFile(file);
			}

		});
		item2.addActionListener(e -> {
			// TODO Auto-generated method stub
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(item2)==JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				writeFile(file.getPath());
			}
		});
	}
	public void copyFile(File file){//复制文件
		File to=new File(file.getAbsolutePath()+"_copy");
		if (file.isFile()) {
			byte[] buf = new byte[1024];//字节流
			int length=0;
			try {
				FileInputStream in=new FileInputStream(file);
				FileOutputStream out=new FileOutputStream(to);
				while((length=in.read(buf))>0){
					out.write(buf,0,length);
				}
				out.flush();
				in.close();
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catchblock
				e.printStackTrace();
			}

		}
	}

	private void readFile(File file){//读文件
		BufferedReader bReader;
		try {
			bReader=new BufferedReader(new FileReader(file));
			StringBuffer sBuffer=new StringBuffer();
			String str;
			while((str=bReader.readLine())!=null){
				sBuffer.append(str+'\n');
				System.out.println(str);
			}
			textArea.setText(sBuffer.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void writeFile(String savepath){//写文件
		FileOutputStream fos= null;
		try {
			fos=new FileOutputStream(savepath);
			fos.write(textArea.getText().getBytes());
			fos.close();
			System.out.println("已保存");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textArea.getText();
	}
	private static void getTrueTable(File file){
		Project proj;
		proj = ProjectActions.doOpenT(null,null, file);
		Circuit cur = proj == null ? null : proj.getCurrentCircuit();
		ProjectCircuitActions.doAnalyzeT(proj, cur);

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Newcaidan();
		File file = new File("C:\\Users\\kxg\\Desktop\\logisimfile\\123.circ");
		//String str  = "C:\\Users\\kxg\\Desktop\\logisimfile\\123.circ";
		getTrueTable(file);

	}
}