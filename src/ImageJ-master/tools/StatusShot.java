import ij.*;
import ij.gui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import ij.i18n.Lang;

/** 逐个触发工具的状态栏说明，读出文本并截图，用于验证工具说明是否已汉化。 */
public class StatusShot {

	static String text(ImageJ ij) {
		String s = ImageJ.getStatusBarText();
		if (s != null && s.length() > 0)
			return s;
		Panel sb = ij.getStatusBar();
		for (int i = 0; i < sb.getComponentCount(); i++) {
			Component c = sb.getComponent(i);
			if (c instanceof JLabel)
				return ((JLabel) c).getText();
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		IJ.wait(1500);
		System.out.println("instance=" + (IJ.getInstance() != null) + " protected=" + IJ.statusBarProtected() + " dict=" + Lang.size());
		Toolbar tb = Toolbar.getInstance();
		Method m = Toolbar.class.getDeclaredMethod("showMessage", int.class);
		m.setAccessible(true);
		ij.setLocation(40, 40);
		ij.toFront();
		IJ.wait(600);
		for (int t = -1; t <= 16; t++) {
			m.invoke(tb, t);
			IJ.wait(80);
			System.out.println("tool " + t + " : " + text(ij));
		}
		m.invoke(tb, 0);
		IJ.wait(200);
		Robot r = new Robot();
		BufferedImage img = r.createScreenCapture(new Rectangle(ij.getX(), ij.getY(), ij.getWidth(), ij.getHeight()));
		ImageIO.write(img, "png", new File(args.length > 0 ? args[0] : "shot_status.png"));
		System.out.println("saved " + (args.length > 0 ? args[0] : "shot_status.png"));
		System.exit(0);
	}
}
