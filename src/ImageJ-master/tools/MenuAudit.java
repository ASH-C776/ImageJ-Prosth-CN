import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.text.*;
import ij.i18n.Lang;
import ij.menu.Menu;
import ij.menu.MenuItem;
import ij.menu.CheckboxMenuItem;
import ij.menu.MenuBar;
import ij.menu.PopupMenu;
import javax.swing.*;
import java.awt.*;

/** 运行时遍历各类菜单，打印显示文本与内部命令名，检查汉化覆盖情况。 */
public class MenuAudit {

	static int bad = 0, total = 0;

	static void chk(String where, String text, String cmd) {
		total++;
		boolean cn = text != null && text.matches(".*[\\u4e00-\\u9fa5].*");
		if (!cn) { bad++; System.out.println("  [EN] " + where + " '" + text + "'"); }
	}

	static void dumpMenu(String where, Menu m, int depth) {
		if (m == null) return;
		chk(where + ">" + m.getText(), m.getText(), m.getActionCommand());
		for (int i = 0; i < m.getItemCount(); i++) {
			javax.swing.JMenuItem it = m.getItem(i);
			if (it == null) continue;
			String t = it.getText();
			if ("-".equals(t)) continue;
			chk(where + ">" + m.getText(), t, it.getActionCommand());
			if (it instanceof Menu) dumpMenu(where + ">" + m.getText(), (Menu) it, depth + 1);
		}
	}

	static void dumpPopup(String where, PopupMenu pm) {
		if (pm == null) return;
		for (int i = 0; i < pm.getItemCount(); i++) {
			javax.swing.JMenuItem it = pm.getItem(i);
			if (it == null) continue;
			String t = it.getText();
			if ("-".equals(t)) continue;
			chk(where, t, it.getActionCommand());
		}
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		IJ.wait(500);

		// 1) 主菜单栏
		MenuBar mb = Menus.getMenuBar();
		for (int i = 0; i < mb.getMenuCount(); i++)
			dumpMenu("MB", mb.getMenu(i), 0);
		System.out.println("主菜单栏 OK");

		// 2) 图像右键菜单
		dumpPopup("图像右键", Menus.getPopupMenu());
		System.out.println("图像右键 OK");

		// 3) 打开文件并做选区，取 ROI 右键菜单
		ImagePlus imp = IJ.openImage("http://imagej.net/images/blobs.gif");
		if (imp == null) imp = IJ.openImage("blobs.gif");
		if (imp != null) {
			imp.setRoi(new Roi(10, 10, 40, 40));
			imp.show();
			IJ.wait(300);
			ImageWindow win = imp.getWindow();
			ImageCanvas ic = win != null ? win.getCanvas() : null;
			java.lang.reflect.Field f = ImageCanvas.class.getDeclaredField("roiPopupMenu");
			f.setAccessible(true);
			PopupMenu roi = (PopupMenu) f.get(ic);
			if (roi == null) {
				java.lang.reflect.Method mm = ImageCanvas.class.getDeclaredMethod("addRoiPopupMenu");
				mm.setAccessible(true);
				mm.invoke(ic);
				roi = (PopupMenu) f.get(ic);
			}
			dumpPopup("ROI右键", roi);
			System.out.println("ROI 右键 OK");
		} else System.out.println("!! 没能打开示例图像");

		// 4) 工具栏弹出菜单
		Toolbar tb = Toolbar.getInstance();
		for (String name : new String[]{"rectPopup", "ovalPopup", "pointPopup", "linePopup", "zoomPopup", "pickerPopup"}) {
			java.lang.reflect.Field f = Toolbar.class.getDeclaredField(name);
			f.setAccessible(true);
			dumpPopup("工具栏." + name, (PopupMenu) f.get(tb));
		}
		System.out.println("工具栏菜单 OK");

		// 5) 结果表窗口菜单
		ResultsTable rt = new ResultsTable();
		rt.incrementCounter(); rt.addValue("A", 1);
		TextWindow tw = new TextWindow("Results", "", "", 200, 200);
		TextPanel tp = tw.getTextPanel();
		java.lang.reflect.Field ff = TextPanel.class.getDeclaredField("fileMenu");
		ff.setAccessible(true);
		dumpMenu("结果窗口", (Menu) ff.get(tp), 0);
		ff = TextPanel.class.getDeclaredField("editMenu");
		ff.setAccessible(true);
		dumpMenu("结果窗口", (Menu) ff.get(tp), 0);
		ff = TextPanel.class.getDeclaredField("pm");
		ff.setAccessible(true);
		dumpPopup("结果窗口右键", (PopupMenu) ff.get(tp));
		System.out.println("结果窗口菜单 OK");

		// 6) ROI Manager
		RoiManager rm = new RoiManager();
		java.lang.reflect.Field f = RoiManager.class.getDeclaredField("pm");
		f.setAccessible(true);
		dumpPopup("ROI管理器右键", (PopupMenu) f.get(rm));
		System.out.println("ROI 管理器 OK");

		System.out.println("\n合计 " + total + " 项，未汉化 " + bad + " 项");
		System.exit(0);
	}
}
