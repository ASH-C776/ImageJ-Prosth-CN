import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/** 仅截图（不启动 ImageJ），用于检查外部启动的应用窗口。 */
public class Capturer {
	public static void main(String[] a) throws Exception {
		String name = a.length > 0 ? a[0] : "capture";
		int delay = a.length > 1 ? Integer.parseInt(a[1]) : 6000;
		Thread.sleep(delay);
		Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage img = new Robot().createScreenCapture(r);
		ImageIO.write(img, "png", new File("shot_" + name + ".png"));
		System.out.println("ok " + img.getWidth() + "x" + img.getHeight());
		System.exit(0);
	}
}
