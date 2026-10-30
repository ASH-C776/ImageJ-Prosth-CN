import ij.*;
import ij.gui.Toolbar;
import ij.i18n.Lang;
import ij.plugin.MacroInstaller;
import javax.imageio.ImageIO;
import java.awt.Robot;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Method;

/** 验证：Developer Menu 汉化、语言切换往返、About 署名、语言对话框渲染。 */
public class LangVerify {
	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setLocation(30, 30);
		ij.toFront();
		IJ.wait(1200);
		System.out.println("dict=" + Lang.size());
		System.out.println("tr(Developer Menu)=" + Lang.tr("Developer Menu"));
		System.out.println("tr(Language...)=" + Lang.tr("Language..."));

		// 安装 Developer Menu 工具（反射调用私有方法）
		try {
			Method m = Toolbar.class.getDeclaredMethod("installBuiltinTool", String.class);
			m.setAccessible(true);
			System.out.println("installBuiltinTool -> " + m.invoke(Toolbar.getInstance(), "Developer Menu"));
		} catch (Throwable t) {
			System.out.println("reflect fail: " + t);
			new MacroInstaller().installFromIJJar("/macros/DeveloperMenuTool.txt");
		}
		IJ.wait(1000);

		// 菜单栏 中→英→中 往返
		ij.menu.MenuBar mb = ij.getSwingMenuBar();
		String b0 = mb.getMenu(0).getText(), b1 = mb.getMenu(1).getText();
		Lang.setEnabled(false); Lang.restoreMenuBar(mb);
		String e0 = mb.getMenu(0).getText(), e1 = mb.getMenu(1).getText();
		Lang.setEnabled(true); Lang.translateMenuBar(mb);
		String z0 = mb.getMenu(0).getText(), z1 = mb.getMenu(1).getText();
		System.out.println("menus zh->en->zh: " + b0 + "," + b1 + " -> " + e0 + "," + e1 + " -> " + z0 + "," + z1);

		// About 署名
		new ij.plugin.AboutBox().run("");
		IJ.wait(1500);
		Robot r = new Robot();
		ImageIO.write(r.createScreenCapture(new Rectangle(0, 0, 1100, 800)), "png", new File("shot_lang.png"));
		System.out.println("saved shot_lang.png");

		// 语言切换对话框
		new Thread(() -> {
			try {
				Method m2 = ImageJ.class.getDeclaredMethod("showLanguageDialog");
				m2.setAccessible(true);
				m2.invoke(ij);
			} catch (Throwable t) { System.out.println("dlg fail: " + t); }
		}).start();
		IJ.wait(1500);
		ImageIO.write(r.createScreenCapture(new Rectangle(0, 0, 1100, 800)), "png", new File("shot_langdlg.png"));
		System.out.println("saved shot_langdlg.png");
		System.exit(0);
	}
}
