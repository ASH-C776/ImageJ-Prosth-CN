import ij.*;
import ij.i18n.Lang;
import java.awt.*;
import java.io.*;

/** 自检工具：输出界面缩放信息 + 完整菜单树（中文/英文对照）。 */
public class DumpMenus {
	public static void main(String[] args) throws Exception {
		PrintStream out = new PrintStream(new FileOutputStream("menu_dump.txt"), true, "UTF-8");
		out.println("Lang entries: " + Lang.size());
		out.println("screen: " + Toolkit.getDefaultToolkit().getScreenSize()
			+ " dpi: " + Toolkit.getDefaultToolkit().getScreenResolution()
			+ " guiScale: " + Prefs.getGuiScale() + " auto: " + Prefs.autoGuiScale());
		ImageJ ij = new ImageJ(ImageJ.NO_SHOW);
		MenuBar mb = Menus.getMenuBar();
		out.println("menus: " + mb.getMenuCount());
		for (int i = 0; i < mb.getMenuCount(); i++) {
			Menu m = mb.getMenu(i);
			dump(out, m, "");
		}
		out.println("--- 未翻译的菜单项（应尽量为空）---");
		for (int i = 0; i < mb.getMenuCount(); i++)
			untranslated(out, mb.getMenu(i), "");
		out.close();
		System.exit(0);
	}

	static void dump(PrintStream out, Menu m, String indent) {
		out.println(indent + "[" + m.getLabel() + "]");
		for (int i = 0; i < m.getItemCount(); i++) {
			MenuItem it = m.getItem(i);
			if (it instanceof Menu) dump(out, (Menu) it, indent + "  ");
			else out.println(indent + "  - " + it.getLabel() + "   |cmd=" + it.getActionCommand());
		}
	}

	static void untranslated(PrintStream out, Menu m, String path) {
		String p = path + m.getLabel();
		for (int i = 0; i < m.getItemCount(); i++) {
			MenuItem it = m.getItem(i);
			if (it instanceof Menu) untranslated(out, (Menu) it, p + ">");
			else if (it.getLabel().equals(it.getActionCommand()) && it.getLabel().length() > 1)
				out.println(p + ">" + it.getLabel());
		}
	}
}
