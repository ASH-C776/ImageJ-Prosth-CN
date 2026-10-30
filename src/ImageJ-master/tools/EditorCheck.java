import ij.*;
import ij.menu.*;
import ij.plugin.frame.Editor;
import javax.swing.*;

/** 检查 Editor 窗口的菜单栏是否正常安装、菜单项是否可用。 */
public class EditorCheck {

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		Editor ed = new Editor();
		ed.setVisible(true);
		IJ.wait(1200);
		java.awt.Component[] comps = ed.getComponents();
		for (int i = 0; i < comps.length; i++)
			System.out.println("comp[" + i + "] " + comps[i].getClass().getName() + " bounds=" + comps[i].getBounds());
		MenuBar mb = null;
		for (int i = 0; i < comps.length; i++)
			if (comps[i] instanceof MenuBar) mb = (MenuBar) comps[i];
		if (mb == null) { System.out.println("EDITOR MENUBAR NOT FOUND"); }
		else {
			System.out.println("editor menuCount=" + mb.getMenuCount());
			Menu macros = mb.getMenu(mb.getMenuCount() - 1);
			System.out.println("last menu='" + macros.getText() + "' items=" + macros.getItemCount());
			Menu file = mb.getMenu(0);
			for (int i = 0; i < file.getItemCount(); i++) {
				JMenuItem it = file.getItem(i);
				System.out.println("  FILE[" + i + "] '" + (it == null ? "null" : it.getText())
					+ "' lis=" + (it == null ? -1 : it.getActionListeners().length));
			}
			// 打开 Edit 菜单看视觉效果
			Menu edit = mb.getMenu(1);
			MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] {mb, edit, edit.getPopupMenu()});
			IJ.wait(900);
			java.awt.Rectangle r = new java.awt.Rectangle(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
			javax.imageio.ImageIO.write(new java.awt.Robot().createScreenCapture(r), "png",
				new java.io.File("shot_editor.png"));
			System.out.println("SAVED shot_editor.png");
		}
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}
}
