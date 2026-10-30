import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** 截取整个屏幕，用于验证打包后 exe 的实际渲染效果。 */
public class ScreenShot {
	public static void main(String[] args) throws Exception {
		Robot r = new Robot();
		Rectangle s = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage img = r.createScreenCapture(s);
		String out = args.length > 0 ? args[0] : "screen.png";
		ImageIO.write(img, "png", new File(out));
		System.out.println("saved " + out + " " + img.getWidth() + "x" + img.getHeight());
	}
}
