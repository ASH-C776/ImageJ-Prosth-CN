import ij.*;
import ij.menu.*;
import javax.swing.*;

/** 端到端：通过菜单项触发的路径执行命令，验证 Swing 菜单改造后命令仍能运行。 */
public class ClickTest {

	static JMenuItem find(Menu m, String cmd) {
		for (int i = 0; i < m.getItemCount(); i++) {
			JMenuItem it = m.getItem(i);
			if (it == null) continue;
			if (cmd.equals(it.getActionCommand()) || cmd.equals(it.getText())) return it;
			if (it instanceof Menu) {
				JMenuItem r = find((Menu) it, cmd);
				if (r != null) return r;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		WindowManager.setTempCurrentImage(null);
		ij.process.ColorProcessor cp = new ij.process.ColorProcessor(300, 200);
		cp.setColor(java.awt.Color.red);
		cp.fill();
		ImagePlus imp = new ImagePlus("测试图", cp);
		imp.show();
		IJ.wait(600);
		System.out.println("before type=" + imp.getType() + " (RGB=" + ImagePlus.COLOR_RGB + ")");

		Menu image = Menus.getMenuBar().getMenu(2);
		JMenuItem type = find(image, "类型");
		System.out.println("Type item=" + (type == null ? "NOT FOUND" : type.getText())
			+ " cls=" + (type == null ? "" : type.getClass().getName())
			+ " isMenu=" + (type instanceof Menu) + " isJMenu=" + (type instanceof JMenu));
		if (type != null && type instanceof Menu) {
			JMenuItem eightBit = find((Menu) type, "8-bit");
			System.out.println("8-bit item=" + (eightBit == null ? "NOT FOUND" : eightBit.getText()));
			if (eightBit != null) {
				System.out.println("8bit cls=" + eightBit.getClass().getName() + " parent=" + eightBit.getParent()
					+ " enabled=" + eightBit.isEnabled() + " listeners=" + eightBit.getActionListeners().length);
				eightBit.doClick();
			}
			IJ.wait(1500);
			System.out.println("status=" + ImageJ.getStatusBarText());
		}
		ImagePlus cur = WindowManager.getCurrentImage();
		System.out.println("after type=" + (cur == null ? "no image" : cur.getType())
			+ " (GRAY8=" + ImagePlus.GRAY8 + ")");

		// 窗口菜单
		Menu window = Menus.getMenuBar().getMenu(6);
		System.out.println("window items=" + window.getItemCount());
		for (int i = 0; i < window.getItemCount(); i++) {
			JMenuItem it = window.getItem(i);
			System.out.println("  [" + i + "] '" + (it == null ? "null" : it.getText()) + "' cmd='"
				+ (it == null ? "" : it.getActionCommand()) + "' checked="
				+ (it instanceof JCheckBoxMenuItem ? ((JCheckBoxMenuItem) it).isSelected() : false));
		}
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}
}
