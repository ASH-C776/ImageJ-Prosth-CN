import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

	public class P0Shot {
	static GenericDialog lastGd;
	public static void main(String[] args) throws Exception {
		// 模拟 4K：强制 GUI 缩放 2.0
		Prefs.setGuiScale(2.0);
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setLocation(60, 60);
		ij.setAlwaysOnTop(true);
		ij.toFront();
		IJ.wait(900);

		ImagePlus imp = new ImagePlus("样本-my2.png", new ByteProcessor(360, 240));
		imp.show();
		IJ.wait(400);

		// 在独立线程弹对话框，主线程截图
		Thread t = new Thread(() -> {
			GenericDialog gd = new GenericDialog("图像计算");
			gd.addChoice("运算", new String[]{"相加", "相减", "相乘", "相除"}, "相加");
			gd.addCheckbox("创建新窗口", true);
			gd.addCheckbox("浮点结果", false);
			gd.addNumericField("值", 25, 0);
			gd.addStringField("名称", "结果", 10);
			gd.setLocation(520, 360);
			gd.setAlwaysOnTop(true);
			lastGd = gd;
			gd.showDialog();
		});
		t.start();
		IJ.wait(1400);
		ij.toFront();
		// 打印对话框组件标签与字体，确认无乱码来源
		if (lastGd != null) {
			lastGd.setAlwaysOnTop(true);
			lastGd.toFront();
			Component[] cs = lastGd.getComponents();
			StringBuilder sb = new StringBuilder("DIALOG font=" + lastGd.getFont() + "\n");
			for (Component c : cs) {
				if (c instanceof Panel) for (Component c2 : ((Panel)c).getComponents())
					sb.append("  ").append(c2.getClass().getSimpleName()).append("=")
					  .append(c2 instanceof Label ? ((Label)c2).getText() : c2.getClass().getSimpleName())
					  .append(" font=").append(c2.getFont()).append("\n");
			}
			System.out.println(sb);
		}

		Robot r = new Robot();
		Toolkit tk = Toolkit.getDefaultToolkit();
		Rectangle all = new Rectangle(tk.getScreenSize());
		java.awt.image.BufferedImage img = r.createScreenCapture(all);
		ImageIO.write(img, "png", new File("shot_p0.png"));
		System.out.println("saved shot_p0.png  screensize=" + all.width + "x" + all.height);

		// 单独截主窗口（含工具栏）
		Rectangle mb = ij.getBounds();
		java.awt.image.BufferedImage win = r.createScreenCapture(mb);
		ImageIO.write(win, "png", new File("shot_p0_main.png"));
		System.out.println("saved shot_p0_main.png bounds=" + mb);

		// 单独截对话框
		if (lastGd != null) {
			Rectangle db = lastGd.getBounds();
			db.grow(8, 8);
			java.awt.image.BufferedImage dlg = r.createScreenCapture(db);
			ImageIO.write(dlg, "png", new File("shot_p0_dlg.png"));
			System.out.println("saved shot_p0_dlg.png bounds=" + db);
		}
		System.exit(0);
	}
}
