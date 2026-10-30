package ij.menu;

import javax.swing.*;

/** Swing 版弹出菜单（右键菜单），兼容 java.awt.PopupMenu 的常用 API。 */
public class PopupMenu extends JPopupMenu {

	private static final MenuItem SEPARATOR = new MenuItem("-");

	public PopupMenu() {
		super();
		init();
	}

	public PopupMenu(String label) {
		super(label);
		init();
	}

	private void init() {
		setFont(MenuStyle.font());
	}

	@Override
	public void addSeparator() {
		JPopupMenu.Separator sep = new JPopupMenu.Separator();
		MenuStyle.styleSeparator(sep);
		add(sep);
	}

	public MenuItem add(String label) {
		MenuItem item = new MenuItem(label);
		add(item);
		return item;
	}

	@Override
	public JMenuItem add(JMenuItem item) {
		MenuStyle.style(item);
		return super.add(item);
	}

	public int getItemCount() {
		return getComponentCount();
	}

	public JMenuItem getItem(int pos) {
		if (pos < 0 || pos >= getComponentCount()) return null;
		java.awt.Component c = getComponent(pos);
		if (c instanceof JPopupMenu.Separator) return SEPARATOR;
		return (c instanceof JMenuItem) ? (JMenuItem) c : SEPARATOR;
	}
}
