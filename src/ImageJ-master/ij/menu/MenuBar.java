package ij.menu;

import java.awt.*;
import javax.swing.*;

/** Swing 版菜单栏，兼容 java.awt.MenuBar 的 add/getMenu/getMenuCount/setHelpMenu。 */
public class MenuBar extends JMenuBar {

	public MenuBar() {
		super();
		setFont(MenuStyle.font());
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MenuStyle.BORDER));
		setOpaque(true);
	}

	/** AWT 语义：getMenu 返回我们自己的 Menu 类型。 */
	@Override
	public Menu getMenu(int i) {
		Component c = getComponent(i);
		return (c instanceof Menu) ? (Menu) c : null;
	}

	@Override
	public int getMenuCount() {
		return getComponentCount();
	}

	public Menu add(Menu m) {
		MenuStyle.styleMenu(m);
		super.add(m);
		return m;
	}
}
