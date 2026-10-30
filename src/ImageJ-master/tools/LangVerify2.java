import ij.*;
import ij.gui.GenericDialog;
import ij.i18n.Lang;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

/** 复现「语言…→English→确定」完整切换流程，抓异常。 */
public class LangVerify2 {
	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setLocation(30, 30);
		IJ.wait(1000);
		System.out.println("before: enabled=" + Lang.isEnabled());

		final Throwable[] err = new Throwable[1];
		new Thread(() -> {
			try {
				Method m = ImageJ.class.getDeclaredMethod("showLanguageDialog");
				m.setAccessible(true);
				m.invoke(ij);
			} catch (Throwable t) { err[0] = t; }
		}).start();
		IJ.wait(1500);

		// 找到语言对话框
		Dialog dlg = null;
		for (Window w : Window.getWindows()) {
			if (!(w instanceof Dialog)) continue;
			Dialog d = (Dialog) w;
			if (d.isVisible() && d.getTitle() != null && d.getTitle().length() > 0)
				dlg = d;
		}
		System.out.println("dialog=" + (dlg == null ? "none" : dlg.getTitle()));
		if (dlg != null) {
			// 1) choice 选 English（index 1）
			Field fc = GenericDialog.class.getDeclaredField("choice");
			fc.setAccessible(true);
			Vector v = (Vector) fc.get(dlg);
			Choice ch = (Choice) v.elementAt(0);
			ch.select(1);
			System.out.println("choice=" + ch.getSelectedItem());

			// 2) 点确定（与真实点击等效：source==okay）
			Field fo = GenericDialog.class.getDeclaredField("okay");
			fo.setAccessible(true);
			Button okay = (Button) fo.get(dlg);
			Method am = GenericDialog.class.getMethod("actionPerformed", ActionEvent.class);
			am.invoke(dlg, new ActionEvent(okay, ActionEvent.ACTION_PERFORMED, "OK"));
			System.out.println("OK dispatched");
		}
		IJ.wait(2000);

		ij.menu.MenuBar mb = ij.getSwingMenuBar();
		System.out.println("after: enabled=" + Lang.isEnabled() + " menu0=" + mb.getMenu(0).getText()
			+ " menu1=" + mb.getMenu(1).getText());
		System.out.println("pref language=" + Prefs.get(Prefs.LANGUAGE, "unset"));
		// 验证完恢复中文偏好，避免影响真实使用
		if (!"zh_CN".equals(Prefs.get(Prefs.LANGUAGE, "zh_CN"))) {
			Prefs.set(Prefs.LANGUAGE, "zh_CN");
			System.out.println("pref restored to zh_CN");
		}
		System.out.println("thread error=" + (err[0] == null ? "none" : err[0].toString()));
		if (err[0] != null && err[0].getCause() != null)
			err[0].getCause().printStackTrace();
		ImageIO.write(new Robot().createScreenCapture(new Rectangle(0, 0, 1100, 800)), "png", new File("shot_switch.png"));
		System.exit(0);
	}
}
