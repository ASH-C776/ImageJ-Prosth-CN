import ij.IJ;
import ij.ImageJ;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** 打开指定对话框并截图，用于检查缩放后的布局溢出。args: 输出名 命令 [延迟ms] */
public class SnapDlg {
	public static void main(String[] a) throws Exception {
		final String out = a.length > 0 ? a[0] : "dlg";
		final String cmd = a.length > 1 ? a[1] : "Appearance...";
		final int delay = a.length > 2 ? Integer.parseInt(a[2]) : 4000;

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2500);
					IJ.run(cmd);
					Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					BufferedImage img = new Robot().createScreenCapture(r);
					ImageIO.write(img, "png", new File("shot_" + out + ".png"));
					System.out.println("SAVED shot_" + out + ".png " + img.getWidth() + "x" + img.getHeight());
				} catch (Throwable e) {
					System.out.println("SHOW_FAILED " + e);
				}
			}
		});
		t.setDaemon(true);
		t.start();

		Thread cap = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2500 + delay);
					Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					BufferedImage img = new Robot().createScreenCapture(r);
					ImageIO.write(img, "png", new File("shot_" + out + ".png"));
					System.out.println("SAVED shot_" + out + ".png " + img.getWidth() + "x" + img.getHeight());
				} catch (Throwable e) {
					System.out.println("CAPTURE_FAILED " + e);
				}
				System.out.flush();
				Runtime.getRuntime().halt(0);
			}
		});
		cap.setDaemon(true);
		cap.start();

		System.out.println("creating ImageJ...");
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.process.ByteProcessor bp = new ij.process.ByteProcessor(640, 420);
		bp.setValue(30); bp.fill();
		bp.setColor(java.awt.Color.white);
		for (int i = 0; i < 12; i++) bp.fillOval(40 + i * 48, 150, 34, 96);
		new ij.ImagePlus("示例影像", bp).show();
		System.out.println("ready");
	}
}
