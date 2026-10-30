import ij.*;
import ij.menu.*;
import javax.swing.*;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/** 展开 Swing 菜单栏的第 N 个菜单并截图，打印布局诊断信息。 */
public class SwingMenuShot {

	public static void main(String[] args) throws Exception {
		final int idx = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		if (args.length>1) { Prefs.set(Prefs.GUI_SCALE, Double.parseDouble(args[1])); Prefs.set(Prefs.MENU_SIZE, (int)Double.parseDouble(args[1])==0?15:15); }
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setAlwaysOnTop(true);
		ij.toFront();
		ij.setLocation(10, 10);
		ij.setSize(760, 420);
		Thread.sleep(1500);

		MenuBar mb = Menus.getMenuBar();
		System.out.println("menubar=" + mb + " font=" + mb.getFont() + " size=" + mb.getHeight());
		System.out.println("menuCount=" + mb.getMenuCount());
		for (int i = 0; i < mb.getMenuCount(); i++) {
			Menu m = mb.getMenu(i);
			System.out.println(" menu[" + i + "] '" + m.getText() + "' pref=" + m.getPreferredSize()
				+ " font=" + m.getFont());
		}
		Menu m = mb.getMenu(idx);
		System.out.println("--- " + m.getText() + " items:");
		for (int i = 0; i < m.getItemCount(); i++) {
			JMenuItem it = m.getItem(i);
			System.out.println("  [" + i + "] '" + (it == null ? "null" : it.getText()) + "' h="
				+ (it == null ? -1 : it.getPreferredSize().height)
				+ " pref=" + (it == null ? "null" : it.getPreferredSize().toString()));
		}

		MenuSelectionManager.defaultManager().setSelectedPath(
			new MenuElement[] {mb, m, m.getPopupMenu()});
		Thread.sleep(1200);

		Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage img = new Robot().createScreenCapture(r);
		ImageIO.write(img, "png", new File("shot_swing" + idx + ".png"));
		System.out.println("SAVED shot_swing" + idx + ".png");
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}
}
