import ij.*;
import ij.gui.*;
import ij.text.*;
import java.awt.*;
import java.lang.reflect.Field;
import javax.swing.*;

/** 校验右键菜单、结果窗口菜单的汉化情况。 */
public class PopupCheck {

	static Object field(Object o, String name) throws Exception {
		Class<?> c = o.getClass();
		while (c != null) {
			try {
				Field f = c.getDeclaredField(name);
				f.setAccessible(true);
				return f.get(o);
			} catch (NoSuchFieldException e) {
				c = c.getSuperclass();
			}
		}
		return null;
	}

	static void dumpMenu(ij.menu.Menu m, String indent) {
		System.out.println(indent + m.getText());
		for (int j = 0; j < m.getItemCount(); j++) {
			JMenuItem it = m.getItem(j);
			if (it == null) continue;
			System.out.println(indent + "    - " + it.getText() + "   cmd=" + it.getActionCommand());
		}
	}

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		IJ.wait(1500);

		System.out.println("== 图像右键菜单 (Menus.getPopupMenu) ==");
		ij.menu.PopupMenu pm = Menus.getPopupMenu();
		if (pm == null) System.out.println("  null");
		else for (Component c : pm.getComponents()) {
			if (c instanceof JMenuItem) {
				JMenuItem it = (JMenuItem) c;
				System.out.println("  - " + it.getText() + "   cmd=" + it.getActionCommand());
			}
		}

		System.out.println("== 结果窗口菜单栏 ==");
		TextWindow tw = new TextWindow("Results", "Col1\tCol2", "", 500, 300);
		IJ.wait(600);
		ij.menu.MenuBar mb = (ij.menu.MenuBar) field(tw, "mb");
		if (mb == null) System.out.println("  mb=null");
		else for (int i = 0; i < mb.getMenuCount(); i++) dumpMenu(mb.getMenu(i), "  ");

		System.out.println("== 结果窗口右键菜单 ==");
		TextPanel tp = (TextPanel) field(tw, "textPanel");
		ij.menu.PopupMenu tpm = (ij.menu.PopupMenu) field(tp, "pm");
		if (tpm == null) System.out.println("  pm=null");
		else for (Component c : tpm.getComponents()) {
			if (c instanceof JMenuItem) {
				JMenuItem it = (JMenuItem) c;
				System.out.println("  - " + it.getText() + "   cmd=" + it.getActionCommand());
			}
		}
		System.exit(0);
	}
}
