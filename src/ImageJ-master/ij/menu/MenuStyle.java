package ij.menu;

import ij.i18n.Lang;
import ij.ui.Theme;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.BorderUIResource;

/**
 * 菜单外观（Swing 版）：统一字体、行距、内边距与配色。
 * <p>
 * 之所以放弃 AWT 原生菜单，是因为 Windows 原生菜单的行高由系统度量固定，
 * 中文字号调大后文字会上下挤压、互相遮挡（原 Menus.fixFontSize 的注释也提到
 * “Windows 限制菜单子项不超过 17 点”）。Swing 菜单自行布局，行高随字号自适应。
 */
public class MenuStyle {

	public static final Color BAR_BG = Theme.PANEL;
	public static final Color MENU_BG = Theme.SURFACE;
	public static final Color BAR_HOVER = Theme.ACCENT_SOFT;
	public static final Color ITEM_HOVER = Theme.ACCENT_SOFT;
	public static final Color BORDER = Theme.BORDER;
	public static final Color TEXT = Theme.TEXT;
	public static final Color TEXT_DISABLED = Theme.TEXT_DISABLED;

	private static boolean initialized;
	private static Font menuFont;

	/** 初始化外观，可重复调用（字号变化时重新调用）。 */
	public static void init(Font font) {
		menuFont = font;
		if (!initialized) {
			initialized = true;
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (Throwable t) {
				// 退回默认外观
			}
		}
		UIDefaults d = UIManager.getDefaults();
		// 所有菜单相关字体统一替换，避免部分控件仍是默认小字号
		String[] prefixes = {"Menu", "MenuItem", "CheckBoxMenuItem", "RadioButtonMenuItem",
			"PopupMenu", "MenuBar", "PopupMenuSeparator"};
		java.util.Enumeration<Object> keys = d.keys();
		java.util.List<Object> fontKeys = new java.util.ArrayList<Object>();
		while (keys.hasMoreElements()) {
			Object k = keys.nextElement();
			String s = k.toString();
			if (!s.endsWith(".font")) continue;
			for (int i = 0; i < prefixes.length; i++) {
				if (s.startsWith(prefixes[i])) { fontKeys.add(k); break; }
			}
		}
		for (int i = 0; i < fontKeys.size(); i++)
			d.put(fontKeys.get(i), font);

		int pad = Math.max(8, font.getSize() / 2);
		Insets itemMargin = new InsetsUIResource(pad - 2, pad + 4, pad - 2, pad + 8);
		String[] marginKeys = {"MenuItem.contentMargins", "CheckBoxMenuItem.contentMargins",
			"RadioButtonMenuItem.contentMargins", "Menu.contentMargins", "MenuBar.contentMargins"};
		for (int i = 0; i < marginKeys.length; i++) {
			if (d.get(marginKeys[i]) != null || i < 4) d.put(marginKeys[i], itemMargin);
		}
		// 配色
		d.put("MenuItem.background", MENU_BG);
		d.put("MenuItem.foreground", TEXT);
		d.put("MenuItem.disabledForeground", TEXT_DISABLED);
		d.put("MenuItem.selectionBackground", ITEM_HOVER);
		d.put("MenuItem.selectionForeground", TEXT);
		d.put("Menu.background", MENU_BG);
		d.put("Menu.foreground", TEXT);
		d.put("Menu.selectionBackground", ITEM_HOVER);
		d.put("Menu.selectionForeground", TEXT);
		d.put("MenuBar.background", BAR_BG);
		d.put("MenuBar.foreground", TEXT);
		d.put("MenuBar.gradient", null);
		d.put("PopupMenu.background", MENU_BG);
		d.put("PopupMenu.foreground", TEXT);
		d.put("PopupMenu.border", new BorderUIResource(compound()));
	}

	private static Border compound() {
		return BorderFactory.createCompoundBorder(
			new LineBorder(BORDER, 1, true),
			new EmptyBorder(4, 0, 4, 0));
	}

	public static Font font() {
		return menuFont != null ? menuFont : Lang.uiFont(Font.PLAIN, 15);
	}

	/** 为菜单项设置内边距（行高与左右留白随字号自适应，避免文字贴边或上下挤压）。 */
	public static void style(javax.swing.JMenuItem item) {
		if (item == null) return;
		Font f = font();
		item.setFont(f);
		int v = Math.max(6, f.getSize() / 3);
		int h = Math.max(12, f.getSize() / 2 + 2);
		item.setMargin(new Insets(v, h, v, h));
		item.setBorderPainted(false);
		item.setIconTextGap(Math.max(6, f.getSize() / 3));
	}

	/** 为菜单栏上的顶级菜单设置内边距。 */
	public static void styleMenu(javax.swing.JMenu m) {
		if (m == null) return;
		Font f = font();
		m.setFont(f);
		int v = Math.max(4, f.getSize() / 4);
		int h = Math.max(10, f.getSize() / 2);
		m.setMargin(new Insets(v, h, v, h));
	}

	/** 分隔线：加高、加浅灰，视觉上把菜单分隔开。 */
	public static void styleSeparator(javax.swing.JPopupMenu.Separator sep) {
		if (sep == null) return;
		int size = font().getSize();
		sep.setForeground(new Color(0xDCE0E6));
		sep.setBackground(MENU_BG);
		sep.setBorder(BorderFactory.createEmptyBorder(Math.max(4, size / 3), 0,
			Math.max(4, size / 3), 0));
		sep.setPreferredSize(new Dimension(1, Math.max(5, size / 3)));
	}
}
