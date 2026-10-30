import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
public class Front {
	public static void main(String[] a) throws Exception {
		String name = a.length > 0 ? a[0] : "front";
		Thread.sleep(6000);
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ALT); r.keyPress(KeyEvent.VK_TAB); r.keyRelease(KeyEvent.VK_TAB); r.keyRelease(KeyEvent.VK_ALT);
		Thread.sleep(2000);
		Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage img = r.createScreenCapture(rect);
		ImageIO.write(img, "png", new File("shot_" + name + ".png"));
		System.out.println("ok");
		System.exit(0);
	}
}
