import ij.IJ;
import ij.ImageJ;
import ij.Menus;
import ij.Prefs;
import ij.i18n.Lang;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;

/** 验证「语言」顶级菜单：位置、勾选状态、点击立即切换。 */
public class LangMenuVerify {
	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setLocation(60, 60);
		ij.toFront();
		IJ.wait(1200);

		// 1) 找到语言菜单及其位置（按文本判断，JMenu 未显式设 actionCommand）
		Object mb = Menus.getMenuBar();
		int nMenus = invokeInt(mb, "getMenuCount");
		int langIdx = -1, winIdx = -1, helpIdx = -1;
		String[] names = new String[nMenus];
		for (int i = 0; i < nMenus; i++) {
			String t = invokeStr(invoke(mb, "getMenu", i), "getText");
			names[i] = t;
			if ("Language".equals(t) || "语言".equals(t)) langIdx = i;
			else if ("Window".equals(t) || "窗口".equals(t)) winIdx = i;
			else if ("Help".equals(t) || "帮助".equals(t)) helpIdx = i;
		}
		System.out.println("menus=" + String.join(" | ", names));
		System.out.println("order ok=" + (winIdx >= 0 && langIdx == winIdx + 1 && helpIdx == langIdx + 1)
			+ " (window=" + winIdx + ", language=" + langIdx + ", help=" + helpIdx + ")");

		Object langMenu = invoke(mb, "getMenu", langIdx);
		int items = invokeInt(langMenu, "getItemCount");
		for (int i = 0; i < items; i++) {
			Object it = invoke(langMenu, "getItem", i);
			if (it instanceof JMenuItem) {
				JMenuItem mi = (JMenuItem) it;
				boolean sel = ((javax.swing.JCheckBoxMenuItem) it).isSelected();
				System.out.println("  item: ac=" + mi.getActionCommand() + " label=" + mi.getText() + " checked=" + sel);
			}
		}

		// 2) 点击 English（走真实 ActionEvent 链路）
		ij.actionPerformed(new java.awt.event.ActionEvent(langEn(), 0, "English"));
		IJ.wait(600);
		System.out.println("after English: enabled=" + Lang.isEnabled() + " pref=" + Prefs.get(Prefs.LANGUAGE, "?")
			+ " zhChecked=" + Menus.langZhItem.getState() + " enChecked=" + Menus.langEnItem.getState()
			+ " firstMenu=" + invokeStr(invoke(mb, "getMenu", 0), "getText"));

		// 截图（英文状态）
		shot("shot_langmenu_en.png", ij);

		// 3) 点击 Chinese 切回
		ij.actionPerformed(new java.awt.event.ActionEvent(Menus.langZhItem, 0, "Chinese"));
		IJ.wait(600);
		System.out.println("after Chinese: enabled=" + Lang.isEnabled() + " pref=" + Prefs.get(Prefs.LANGUAGE, "?")
			+ " zhChecked=" + Menus.langZhItem.getState() + " enChecked=" + Menus.langEnItem.getState()
			+ " firstMenu=" + invokeStr(invoke(mb, "getMenu", 0), "getText"));

		shot("shot_langmenu_zh.png", ij);
		System.exit(0);
	}

	static Object langEn() throws Exception { return Menus.langEnItem; }

	static void shot(String name, ImageJ ij) throws Exception {
		ij.toFront();
		IJ.wait(400);
		Rectangle r = ij.getBounds();
		BufferedImage img = new Robot().createScreenCapture(r);
		ImageIO.write(img, "png", new File(name));
		System.out.println("saved " + name);
	}

	static Object invoke(Object o, String m, int i) throws Exception {
		return o.getClass().getMethod(m, int.class).invoke(o, i);
	}
	static int invokeInt(Object o, String m) throws Exception {
		return (Integer) o.getClass().getMethod(m).invoke(o);
	}
	static String invokeStr(Object o, String m) throws Exception {
		return (String) o.getClass().getMethod(m).invoke(o);
	}
}
