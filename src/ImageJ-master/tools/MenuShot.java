import ij.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/** 展开指定菜单并截图：java MenuShot <菜单序号> [延迟ms] */
public class MenuShot {

	public static void main(String[] args) throws Exception {
		final int idx = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		MenuBar mb = ij.getMenuBar();
		System.out.println("menubar font=" + mb.getFont());
		FontMetrics fm = ij.getFontMetrics(mb.getFont());
		int acc = 6;
		for (int i = 0; i < mb.getMenuCount(); i++) {
			Menu m = mb.getMenu(i);
			int w = fm.stringWidth(m.getLabel()) + 16;
			System.out.println("menu[" + i + "] '" + m.getLabel() + "' font=" + m.getFont()
				+ " estX=" + acc + " w=" + w + " h=" + fm.getHeight() + " asc=" + fm.getAscent());
			for (int j = 0; j < m.getItemCount() && j < 3; j++) {
				MenuItem it = m.getItem(j);
				System.out.println("   item '" + it.getLabel() + "' font=" + it.getFont());
			}
			if (i < idx) acc += w;
		}
		Thread.sleep(1200);
		ij.setExtendedState(Frame.NORMAL);
		ij.setAlwaysOnTop(true);
		ij.toFront();
		ij.requestFocus();
		Thread.sleep(1200);
		Point p = ij.getLocationOnScreen();
		System.out.println("frame at " + p + " size " + ij.getSize());
		int x = p.x + acc + fm.stringWidth(mb.getMenu(idx).getLabel()) / 2 + 8;
		int y = p.y + fm.getHeight() / 2 + 4;
		System.out.println("click at " + x + "," + y);
		Robot r = new Robot();
		r.mouseMove(x, y);
		r.delay(200);
		r.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		r.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		Thread.sleep(1200);
		BufferedImage img = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(img, "png", new File("shot_menu" + idx + ".png"));
		System.out.println("SAVED shot_menu" + idx + ".png");
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}
}
