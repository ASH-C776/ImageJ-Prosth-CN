import ij.*;
import ij.gui.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/** 界面自检：启动 ImageJ 并截图（确认中文菜单与高 DPI 缩放）。 */
public class Shot {

	public static void main(String[] args) throws Exception {
		final String mode = args.length > 0 ? args[0] : "main";
		final int delay = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

		Thread t = new Thread(new Runnable() {
			public void run() {
				try { Thread.sleep(delay); } catch (InterruptedException e) {}
				try {
					Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					BufferedImage img = new Robot().createScreenCapture(r);
					ImageIO.write(img, "png", new File("shot_" + mode + ".png"));
					System.out.println("SAVED shot_" + mode + ".png " + img.getWidth() + "x" + img.getHeight());
				} catch (Throwable e) {
					System.out.println("CAPTURE_FAILED " + e);
				}
				System.out.flush();
				Runtime.getRuntime().halt(0);
			}
		});
		t.setDaemon(true);
		t.start();

		System.out.println("creating ImageJ...");
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		System.out.println("created");
		ij.process.ByteProcessor bp = new ij.process.ByteProcessor(640, 420);
		bp.setValue(30); bp.fill();
		bp.setColor(java.awt.Color.white);
		for (int i = 0; i < 12; i++) {
			int x = 40 + i * 48;
			bp.fillOval(x, 150, 34, 90 + (i % 3) * 12);
		}
		bp.setColor(java.awt.Color.gray);
		bp.drawRect(120, 120, 400, 220);
		ImagePlus imp = new ImagePlus("示例影像 — 全景片", bp);
		imp.show();
		if ("main".equals(mode)) {
			IJ.run("Set Measurements...");
		} else if ("dialog".equals(mode)) {
			IJ.run("Set Scale...");
		}
		System.out.println("ready");
	}
}
