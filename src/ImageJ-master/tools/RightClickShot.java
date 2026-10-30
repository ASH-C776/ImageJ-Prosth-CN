import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** 打开图像 → 右键 → 截图，验证右键菜单汉化与行距。 */
public class RightClickShot {

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		IJ.wait(1200);
		ImagePlus imp = new ImagePlus("示踪图", new ByteProcessor(420, 320));
		imp.setProcessor(new ByteProcessor(420, 320));
		imp.show();
		IJ.wait(1200);
		ImageWindow win = imp.getWindow();
		win.setLocation(60, 60);
		win.setSize(520, 420);
		win.toFront();
		ij.toFront();
		IJ.wait(800);
		ImageCanvas canvas = win.getCanvas();
		Point p = canvas.getLocationOnScreen();
		Robot r = new Robot();
		r.mouseMove(p.x + 200, p.y + 150);
		IJ.wait(300);
		r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		IJ.wait(120);
		r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		IJ.wait(900);
		Rectangle screen = new Rectangle(0, 0, 900, 700);
		BufferedImage img = r.createScreenCapture(screen);
		String out = args.length > 0 ? args[0] : "shot_popup.png";
		ImageIO.write(img, "png", new File(out));
		System.out.println("saved " + out);
		System.exit(0);
	}
}
