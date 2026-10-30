import javax.swing.*;
import java.awt.*;
public class CJKTest {
	public static void main(String[] a) {
		Frame f = new Frame("汉字渲染测试");
		f.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		Label l1 = new Label("AWT 正畸口腔影像：文件 编辑 图像 处理 分析 插件 窗口 帮助");
		l1.setFont(new Font("Dialog", Font.PLAIN, 16));
		f.add(l1);
		JLabel l2 = new JLabel("Swing 头影测量：标志点 阈值 分水岭 面积 周长 角度");
		l2.setFont(new Font("Dialog", Font.PLAIN, 16));
		f.add(l2);
		JLabel l3 = new JLabel("雅黑测试：蝶鞍点 鼻根点 颏前点 下颌角点");
		l3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		f.add(l3);
		System.out.println("Dialog canDisplayUpTo: " + new Font("Dialog", Font.PLAIN, 12).canDisplayUpTo("正畸口腔"));
		System.out.println("YaHei canDisplayUpTo: " + new Font("Microsoft YaHei UI", Font.PLAIN, 12).canDisplayUpTo("正畸口腔"));
		f.setSize(760, 200);
		f.setLocation(120, 120);
		f.setVisible(true);
	}
}
