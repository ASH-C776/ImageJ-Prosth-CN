import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/** 关掉可能弹出的防火墙提示后截图。 */
public class Dismiss {
	public static void main(String[] a) throws Exception {
		String name = a.length > 0 ? a[0] : "pkg";
		Thread.sleep(7000);
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE); r.keyRelease(KeyEvent.VK_ESCAPE);
		Thread.sleep(1500);
		Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage img = r.createScreenCapture(rect);
		ImageIO.write(img, "png", new File("shot_" + name + ".png"));
		System.out.println("ok");
		System.exit(0);
	}
}
