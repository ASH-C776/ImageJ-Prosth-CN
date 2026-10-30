import ij.*;
import ij.menu.*;
import javax.swing.*;
import java.util.*;

/** 菜单自检：统计条目、检查 actionCommand 是否仍为英文、触发一次菜单命令。 */
public class MenuCheck {

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		MenuBar mb = Menus.getMenuBar();
		int menus = mb.getMenuCount(), items = 0, noCmd = 0, zhCmd = 0, noListener = 0;
		for (int i = 0; i < menus; i++) {
			Menu m = mb.getMenu(i);
			items += count(m);
		}
		System.out.println("menus=" + menus + " items=" + items);
		dump(mb.getMenu(1), "  ");
		JMenuItem about = find(mb.getMenu(7), "About ImageJ...");
		System.out.println("about found=" + about + " cmd=" + (about != null ? about.getActionCommand() : ""));
		if (about != null) {
			System.out.println("listeners=" + about.getActionListeners().length);
			about.doClick();
			Thread.sleep(1500);
		}
		for (java.awt.Frame f : java.awt.Frame.getFrames()) {
			if (f.isShowing()) System.out.println("window: " + f.getClass().getName() + " '" + f.getTitle() + "'");
		}
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}

	static int count(Menu m) {
		int n = 0;
		for (int i = 0; i < m.getItemCount(); i++) {
			JMenuItem it = m.getItem(i);
			if (it == null) continue;
			String t = it.getText();
			if (t == null || t.equals("-")) continue;
			n++;
			if (it instanceof Menu) n += count((Menu) it);
		}
		return n;
	}

	static void dump(Menu m, String ind) {
		for (int i = 0; i < m.getItemCount(); i++) {
			JMenuItem it = m.getItem(i);
			if (it == null) continue;
			System.out.println(ind + "[" + i + "] text='" + it.getText() + "' cmd='" + it.getActionCommand()
				+ "' lis=" + it.getActionListeners().length
				+ " en=" + it.isEnabled()
				+ " font=" + (it.getFont() != null ? it.getFont().getFamily() + "/" + it.getFont().getSize() : "null"));
		}
	}

	static JMenuItem find(Menu m, String cmd) {
		for (int i = 0; i < m.getItemCount(); i++) {
			JMenuItem it = m.getItem(i);
			if (it == null) continue;
			if (cmd.equals(it.getActionCommand())) return it;
			if (it instanceof Menu) {
				JMenuItem r = find((Menu) it, cmd);
				if (r != null) return r;
			}
		}
		return null;
	}
}
